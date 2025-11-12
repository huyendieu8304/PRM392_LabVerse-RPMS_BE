package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.EStatus;
import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamReadingListRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.entity.Membership;
import com.prm392.be.labverse.entity.Team;
import com.prm392.be.labverse.entity.TeamReadingList;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.TeamErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.MembershipRepository;
import com.prm392.be.labverse.repository.TeamReadingListRepository;
import com.prm392.be.labverse.repository.TeamRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;
    private final TeamReadingListRepository teamReadingListRepository;

    public List<TeamResponse> getMyTeamsForCurrentUser() {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        // Team do mình tạo (nếu là PI)
        List<Team> ownedTeams = teamRepository.findByCreatedBy_Id(currentUserId);

        // Team mình là member (INTERN hoặc PI join team người khác)
        List<Membership> memberships =
                membershipRepository.findByUserId_IdAndStatus(currentUserId, EStatus.APPROVED);

        Set<String> teamIds = new HashSet<>();
        List<TeamResponse> result = new ArrayList<>();

        for (Team team : ownedTeams) {
            if (teamIds.add(team.getId())) {
                result.add(mapTeamToResponse(team));
            }
        }
        for (Membership m : memberships) {
            Team team = m.getTeam();
            if (team != null && teamIds.add(team.getId())) {
                result.add(mapTeamToResponse(team));
            }
        }
        return result;
    }

    private TeamResponse mapTeamToResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }


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
        List<Membership> memberships = membershipRepository.findByTeam_IdOrderByName(teamId);

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
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
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
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }


    @Override
    @Transactional
    public TeamResponse updateTeam(String teamId, TeamRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();

        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        team.setName(request.getName());
        team.setDescription(request.getDescription());

        teamRepository.save(team);

        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }

    @Override
    @Transactional
    public void deleteTeam(String teamId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();

        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        team.setDeleteFlag(true);
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public void removeTeamMember(String teamId, String memberId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        if (!team.getCreatedBy().getId().equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        if (memberId.equals(currentUserId)) {
            throw new AppException(TeamErrorCode.TEAM_CANNOT_REMOVE_SELF);
        }

        Membership membership = membershipRepository
                .findByTeam_IdAndUserId_IdAndDeleteFlagFalse(teamId, memberId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(TeamErrorCode.MEMBER_NOT_FOUND));

        membership.setDeleteFlag(true);
        membershipRepository.save(membership);
    }


    @Override
    public List<TeamReadingListResponse> getTeamReadingListsByTeamId(String teamId) {
        verifyTeamExists(teamId);

        return teamReadingListRepository.findByTeam_IdAndDeleteFlagFalse(teamId)
                .stream()
                .map(this::mapToTeamReadingListResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamReadingListResponse getTeamReadingListById(String teamId, String teamReadingListId) {
        verifyTeamExists(teamId);

        TeamReadingList teamReadingList = teamReadingListRepository.findById(teamReadingListId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_FOUND));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_IN_TEAM);
        }

        if (teamReadingList.isDeleteFlag()) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_DELETED);
        }

        return mapToTeamReadingListResponse(teamReadingList);
    }

    @Override
    @Transactional
    public TeamReadingListResponse createTeamReadingList(String teamId, TeamReadingListRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        verifyTeamMembership(currentUserId, teamId);

        TeamReadingList teamReadingList = TeamReadingList.builder()
                .team(team)
                .name(request.getName())
                .description(request.getDescription())
                .deleteFlag(false)
                .build();
        teamReadingListRepository.save(teamReadingList);
        return mapToTeamReadingListResponse(teamReadingList);
    }

    @Override
    @Transactional
    public TeamReadingListResponse updateTeamReadingList(String teamId, String teamReadingListId, TeamReadingListRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        verifyTeamMembership(currentUserId, teamId);

        TeamReadingList teamReadingList = teamReadingListRepository.findById(teamReadingListId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_FOUND));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_IN_TEAM);
        }

        if (teamReadingList.isDeleteFlag()) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_DELETED);
        }

        teamReadingList.setName(request.getName());
        teamReadingList.setDescription(request.getDescription());

        teamReadingListRepository.save(teamReadingList);

        return mapToTeamReadingListResponse(teamReadingList);
    }

    @Override
    @Transactional
    public void deleteTeamReadingList(String teamId, String teamReadingListId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        verifyTeamMembership(currentUserId, teamId);

        TeamReadingList teamReadingList = teamReadingListRepository.findById(teamReadingListId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_FOUND));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_IN_TEAM);
        }

        teamReadingList.setDeleteFlag(true);
        teamReadingListRepository.save(teamReadingList);
    }

    // ==================== HELPER METHODS ====================

    private void verifyTeamExists(String teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_NOT_FOUND);
        }
    }

    private void verifyTeamMembership(String userId, String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));

        boolean isOwner = team.getCreatedBy().getId().equals(userId);
        boolean isMember = membershipRepository.existsByTeam_IdAndUserId_Id(teamId, userId);

        if (!isOwner && !isMember) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }
    }

    private TeamReadingListResponse mapToTeamReadingListResponse(TeamReadingList teamReadingList) {
        return TeamReadingListResponse.builder()
                .id(teamReadingList.getId())
                .teamId(teamReadingList.getTeam().getId())
                .name(teamReadingList.getName())
                .description(teamReadingList.getDescription())
                .createdAt(teamReadingList.getCreatedAt())
                .updatedAt(teamReadingList.getUpdatedAt())
                .build();
    }
}
