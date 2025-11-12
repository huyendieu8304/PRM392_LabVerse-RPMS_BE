package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.team.MemberResponse;
import com.prm392.be.labverse.dto.team.TeamReadingListRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListResponse;
import com.prm392.be.labverse.dto.team.TeamRequest;
import com.prm392.be.labverse.dto.team.TeamResponse;
import com.prm392.be.labverse.dto.team.SetPaperPriorityRequest;
import com.prm392.be.labverse.dto.team.TeamReadingListPaperResponse;
import com.prm392.be.labverse.service.TeamService;
import com.prm392.be.labverse.service.TeamReadingListPaperService;
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
    private final TeamReadingListPaperService teamReadingListPaperService;

    @GetMapping("/my-teams")
    public List<TeamResponse> getMyTeams() {
        return teamService.getMyTeamsForCurrentUser();
    }


    @GetMapping("/pi/my-teams")
    public List<TeamResponse> getTeamsByCreator(@RequestParam String userId) {
        return teamService.listAllTeams(userId);
    }

    @GetMapping("/{teamId}/members")
    public List<MemberResponse> getMembersByTeam(@PathVariable String teamId) {
        return teamService.getMembersByTeamId(teamId);
    }

    @PostMapping("/pi/create")
    public TeamResponse createTeam(@RequestBody TeamRequest teamRequest) {
        return teamService.createTeam(teamRequest);
    }

    @PutMapping("/pi/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable String teamId,
            @RequestBody TeamRequest request) {
        TeamResponse response = teamService.updateTeam(teamId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/pi/delete/{teamId}")
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

    // ==================== READING LIST PAPERS ====================
    @GetMapping("/{teamId}/reading-lists/{readingListId}/papers")
    public java.util.List<TeamReadingListPaperResponse> listPapersInReadingList(
            @PathVariable String teamId,
            @PathVariable String readingListId) {
        return teamReadingListPaperService.listPapers(teamId, readingListId);
    }

    @DeleteMapping("/{teamId}/reading-lists/{readingListId}/papers/{paperId}")
    public ResponseEntity<String> removePaperFromReadingList(
            @PathVariable String teamId,
            @PathVariable String readingListId,
            @PathVariable String paperId) {
        teamReadingListPaperService.removePaper(teamId, readingListId, paperId);
        return ResponseEntity.ok("Paper removed from reading list successfully");
    }

    @PostMapping("/{teamId}/reading-lists/{readingListId}/papers/{paperId}")
    public ResponseEntity<TeamReadingListPaperResponse> addPaperToReadingList(
            @PathVariable String teamId,
            @PathVariable String readingListId,
            @PathVariable String paperId,
            @RequestBody(required = false) SetPaperPriorityRequest request) {

        TeamReadingListPaperResponse response = teamReadingListPaperService.addPaper(
                teamId,
                readingListId,
                paperId,
                request != null ? request.getPriority() : null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // ==================== READING LIST PAPER PRIORITY ====================
    @PutMapping("/{teamId}/reading-lists/{readingListId}/papers/{paperId}/priority")
    public TeamReadingListPaperResponse setPaperPriority(
            @PathVariable String teamId,
            @PathVariable String readingListId,
            @PathVariable String paperId,
            @RequestBody SetPaperPriorityRequest request) {
        return teamReadingListPaperService.setPaperPriority(teamId, readingListId, paperId, request.getPriority());
    }
}