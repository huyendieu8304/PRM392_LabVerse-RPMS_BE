package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.entity.Membership;
import com.prm392.be.labverse.entity.Team;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.repository.MembershipRepository;
import com.prm392.be.labverse.repository.TeamRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public List<TeamResponse> listAllTeams(String userId) {
        return teamRepository.findByCreatedBy_Id(userId)
                .stream()
                .map(team -> TeamResponse.builder()
                        .id(team.getId())
                        .name(team.getName())
                        .description(team.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberResponse> getMembersByTeamId(String teamId) {
        List<Membership> memberships = membershipRepository.findByTeam_Id(teamId);

        if (memberships.isEmpty()) {
            return Collections.emptyList();
        }

        return memberships.stream()
                .filter(m -> !m.getUserId().isDeleteFlag())
                .map(m -> {
                    User user = m.getUserId();
                    return MemberResponse.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .role(user.getRole() != null ? user.getRole().getName().name() : null)
                            .status(m.getStatus() != null ? m.getStatus().name() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();

        if (currentUserId == null) {
            throw new RuntimeException("No authenticated user found");
        }

        User createdByUser = new User();
        createdByUser.setId(currentUserId);

        Team team = Team.builder()
                .createdBy(createdByUser)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        teamRepository.save(team);

        return TeamResponse.builder()
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }

    @Override
    @Transactional
    public void deleteTeam(String teamId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();

        if (currentUserId == null) {
            throw new RuntimeException("No authenticated user found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));

        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new RuntimeException("You don't have permission to delete this team");
        }
        membershipRepository.deleteByTeam_Id(teamId);
        teamRepository.delete(team);
    }

    @Override
    @Transactional
    public void removeTeamMember(String teamId, String memberId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("No authenticated user found");
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new RuntimeException("You don't have permission to remove members from this team");
        }
        if (memberId.equals(currentUserId)) {
            throw new RuntimeException("Cannot remove this member");
        }
        Membership membership = membershipRepository.findByTeam_IdAndUserId_Id(teamId, memberId)
                .orElseThrow(() -> new RuntimeException("Member not found in this team"));

        // Xóa member
        membershipRepository.delete(membership);
    }
}