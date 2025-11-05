package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamReadingListRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.entity.Membership;
import com.prm392.be.labverse.entity.Team;
import com.prm392.be.labverse.entity.TeamReadingList;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.repository.MembershipRepository;
import com.prm392.be.labverse.repository.TeamReadingListRepository;
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
    private final TeamReadingListRepository teamReadingListRepository;

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

        membershipRepository.delete(membership);
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
                .orElseThrow(() -> new RuntimeException("Team reading list not found"));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Team reading list does not belong to this team");
        }

        if (teamReadingList.isDeleteFlag()) {
            throw new RuntimeException("Team reading list has been deleted");
        }

        return mapToTeamReadingListResponse(teamReadingList);
    }

    @Override
    @Transactional
    public TeamReadingListResponse createTeamReadingList(String teamId, TeamReadingListRequest request) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("No authenticated user found");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

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
            throw new RuntimeException("No authenticated user found");
        }

        verifyTeamMembership(currentUserId, teamId);

        TeamReadingList teamReadingList = teamReadingListRepository.findById(teamReadingListId)
                .orElseThrow(() -> new RuntimeException("Team reading list not found"));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Team reading list does not belong to this team");
        }

        if (teamReadingList.isDeleteFlag()) {
            throw new RuntimeException("Cannot update deleted team reading list");
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
            throw new RuntimeException("No authenticated user found");
        }

        verifyTeamMembership(currentUserId, teamId);

        TeamReadingList teamReadingList = teamReadingListRepository.findById(teamReadingListId)
                .orElseThrow(() -> new RuntimeException("Team reading list not found"));

        if (!teamReadingList.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Team reading list does not belong to this team");
        }

        // Soft delete
        teamReadingList.setDeleteFlag(true);
        teamReadingListRepository.save(teamReadingList);
    }

    // ==================== HELPER METHODS ====================

    private void verifyTeamExists(String teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new RuntimeException("Team not found");
        }
    }

    private void verifyTeamMembership(String userId, String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        boolean isOwner = team.getCreatedBy().getId().equals(userId);
        boolean isMember = membershipRepository.existsByTeam_IdAndUserId_Id(teamId, userId);

        if (!isOwner && !isMember) {
            throw new RuntimeException("You are not a member of this team");
        }
    }

    private TeamReadingListResponse mapToTeamReadingListResponse(TeamReadingList teamReadingList) {
        return TeamReadingListResponse.builder()
                .teamId(teamReadingList.getTeam().getId())
                .name(teamReadingList.getName())
                .description(teamReadingList.getDescription())
                .createdAt(teamReadingList.getCreatedAt())
                .updatedAt(teamReadingList.getUpdatedAt())
                .build();
    }
}