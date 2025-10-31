package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/my-teams")
    public List<TeamResponse> getTeamsByCreator(@RequestParam String userId) {
        return teamService.listAllTeams(userId);
    }

    @GetMapping("/{teamId}/members")
    public Object getMembersByTeam(@PathVariable String teamId) {
        return teamService.getMembersByTeamId(teamId);
    }

    @PostMapping("/create")
    public TeamResponse createTeam(@RequestBody TeamRequest teamRequest) {
        return teamService.createTeam(teamRequest);
    }

    @DeleteMapping("delete/{teamId}")
    public void deleteTeam(@PathVariable String teamId) {
        teamService.deleteTeam(teamId);
    }
}
