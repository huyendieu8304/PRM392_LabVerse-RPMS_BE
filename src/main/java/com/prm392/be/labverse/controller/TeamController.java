package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamReadingListRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<MemberResponse> getMembersByTeam(@PathVariable String teamId) {
        return teamService.getMembersByTeamId(teamId);
    }

    @PostMapping("/create")
    public TeamResponse createTeam(@RequestBody TeamRequest teamRequest) {
        return teamService.createTeam(teamRequest);
    }

    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok("Team deleted successfully");
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<String> removeTeamMember(@PathVariable String teamId, @PathVariable String memberId) {
        teamService.removeTeamMember(teamId, memberId);
        return ResponseEntity.ok("Member removed successfully");
    }

    // ==================== TEAM READING LIST  ====================

    @GetMapping("/{teamId}/reading-lists")
    public List<TeamReadingListResponse> getAllTeamReadingLists(@PathVariable String teamId) {
        return teamService.getTeamReadingListsByTeamId(teamId);
    }

    @GetMapping("/{teamId}/reading-lists/{readingListId}")
    public TeamReadingListResponse getTeamReadingListById(
            @PathVariable String teamId,
            @PathVariable String readingListId) {
        return teamService.getTeamReadingListById(teamId, readingListId);
    }

    @PostMapping("/{teamId}/create-reading-lists")
    public ResponseEntity<TeamReadingListResponse> createTeamReadingList(
            @PathVariable String teamId,
            @RequestBody TeamReadingListRequest request) {
        TeamReadingListResponse response = teamService.createTeamReadingList(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{teamId}/update-reading-lists/{readingListId}")
    public TeamReadingListResponse updateTeamReadingList(
            @PathVariable String teamId,
            @PathVariable String readingListId,
            @RequestBody TeamReadingListRequest request) {
        return teamService.updateTeamReadingList(teamId, readingListId, request);
    }

    @DeleteMapping("/{teamId}/delete-reading-lists/{readingListId}")
    public ResponseEntity<String> deleteTeamReadingList(
            @PathVariable String teamId,
            @PathVariable String readingListId) {
        teamService.deleteTeamReadingList(teamId, readingListId);
        return ResponseEntity.ok("Team reading list deleted successfully");
    }
}