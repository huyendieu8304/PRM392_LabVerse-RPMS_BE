package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.team.*;

import java.util.List;

public interface TeamService {
    List<TeamResponse> listAllTeams(String id);
    List<MemberResponse> getMembersByTeamId(String teamId);
    TeamResponse createTeam(TeamRequest request);
    TeamResponse updateTeam(String teamId, TeamRequest request);
    void deleteTeam(String teamId);
    void removeTeamMember(String teamId, String memberId);
    List<TeamReadingListResponse> getTeamReadingListsByTeamId(String teamId);
    TeamReadingListResponse getTeamReadingListById(String teamId, String readingListId);
    TeamReadingListResponse createTeamReadingList(String teamId, TeamReadingListRequest request);
    TeamReadingListResponse updateTeamReadingList(String teamId, String readingListId, TeamReadingListRequest request);
    void deleteTeamReadingList(String teamId, String readingListId);
}