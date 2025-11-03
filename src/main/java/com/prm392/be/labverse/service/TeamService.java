package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import java.util.List;

public interface TeamService {
    List<TeamResponse> listAllTeams(String id);
    List<MemberResponse> getMembersByTeamId(String teamId);
    TeamResponse createTeam(TeamRequest request);
    void deleteTeam(String teamId);
    void removeTeamMember(String teamId, String memberId);
}