package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.EStatus;
import com.prm392.be.labverse.dto.team.InvitationRequest;
import com.prm392.be.labverse.dto.team.InvitationResponse;
import com.prm392.be.labverse.entity.Membership;
import com.prm392.be.labverse.entity.Team;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.TeamErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.MembershipRepository;
import com.prm392.be.labverse.repository.TeamRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final MembershipRepository membershipRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private static final int INVITATION_EXPIRY_DAYS = 7;

    @Override
    @Transactional
    public void sendInvitation(InvitationRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        // Verify team + ownership
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));

        // CHECK 1: Đã là member chưa (status = APPROVED)
        List<Membership> allMemberships = membershipRepository.findByTeam_IdAndUserId_Id(
                request.getTeamId(),
                invitedUser.getId()
        );

        for (Membership m : allMemberships) {
            if (m.getStatus() == EStatus.APPROVED) {
                throw new AppException(TeamErrorCode.MEMBER_ALREADY_IN_TEAM);
            }
        }

        // CHECK 2: Invitation pending chưa expired
        Optional<Membership> existingInvitation = membershipRepository
                .findByTeam_IdAndUserId_IdAndStatus(
                        request.getTeamId(),
                        invitedUser.getId(),
                        EStatus.PENDING
                );

        if (existingInvitation.isPresent()) {
            Membership existing = existingInvitation.get();
            if (!isExpired(existing)) {
                sendInvitationEmail(existing, team, invitedUser);
                return;
            }
            membershipRepository.delete(existing);
        }

        Membership invitation = Membership.builder()
                .team(team)
                .userId(invitedUser)
                .status(EStatus.PENDING)
                .build();

        membershipRepository.save(invitation);

        sendInvitationEmail(invitation, team, invitedUser);
    }


    @Override
    public List<InvitationResponse> getMyInvitations() {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        List<Membership> invitations = membershipRepository
                .findByUserId_IdAndStatus(currentUserId, EStatus.PENDING);

        return invitations.stream()
                .map(this::mapToInvitationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptInvitation(String invitationId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Membership invitation = membershipRepository.findById(invitationId)
                .orElseThrow(() -> new AppException(TeamErrorCode.MEMBER_NOT_FOUND));

        if (!invitation.getUserId().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        if (invitation.getStatus() != EStatus.PENDING) {
            throw new AppException(TeamErrorCode.INVITATION_INVALID);
        }

        if (isExpired(invitation)) {
            throw new AppException(TeamErrorCode.INVITATION_EXPIRED);
        }

        invitation.setStatus(EStatus.APPROVED);
        membershipRepository.save(invitation);
    }


    @Override
    @Transactional
    public void rejectInvitation(String invitationId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Membership invitation = membershipRepository.findById(invitationId)
                .orElseThrow(() -> new AppException(TeamErrorCode.MEMBER_NOT_FOUND));

        if (!invitation.getUserId().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        if (invitation.getStatus() != EStatus.PENDING) {
            throw new AppException(TeamErrorCode.MEMBER_NOT_FOUND);
        }

        // Reject
        invitation.setStatus(EStatus.REJECTED);
        membershipRepository.save(invitation);
    }

    // ========== HELPER METHODS ==========

    private boolean isExpired(Membership invitation) {
        LocalDateTime expireTime = invitation.getCreatedAt().plusDays(INVITATION_EXPIRY_DAYS);
        return LocalDateTime.now().isAfter(expireTime);
    }

    private int calculateDaysLeft(Membership invitation) {
        if (isExpired(invitation)) {
            return 0;
        }
        LocalDateTime expireTime = invitation.getCreatedAt().plusDays(INVITATION_EXPIRY_DAYS);
        return (int) ChronoUnit.DAYS.between(LocalDateTime.now(), expireTime);
    }

    private void sendInvitationEmail(Membership invitation, Team team, User invitedUser) {
        User inviter = team.getCreatedBy();
        String invitationLink = frontendUrl + "/invitations/accept?id=" + invitation.getId();

        mailService.sendMemberInvitation(
                invitedUser.getEmail(),
                team.getName(),
                inviter.getFullName(),
                invitationLink
        );
    }

    private InvitationResponse mapToInvitationResponse(Membership invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .teamId(invitation.getTeam().getId())
                .teamName(invitation.getTeam().getName())
                .teamDescription(invitation.getTeam().getDescription())
                .invitedBy(invitation.getTeam().getCreatedBy().getFullName())
                .invitedUserEmail(invitation.getUserId().getEmail())
                .status(invitation.getStatus().name())
                .invitedAt(invitation.getCreatedAt())
                .expiresAt(invitation.getCreatedAt().plusDays(INVITATION_EXPIRY_DAYS))
                .daysLeft(calculateDaysLeft(invitation))
                .isExpired(isExpired(invitation))
                .build();
    }
}
