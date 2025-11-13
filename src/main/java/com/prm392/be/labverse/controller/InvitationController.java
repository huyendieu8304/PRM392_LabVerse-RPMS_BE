package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.team.InvitationRequest;
import com.prm392.be.labverse.dto.team.InvitationResponse;
import com.prm392.be.labverse.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/pi/send")
    public ResponseEntity<String> sendInvitation(@RequestBody InvitationRequest request) {
        invitationService.sendInvitation(request);
        return ResponseEntity.ok("Invitation sent successfully");
    }

    @GetMapping("/user/my-invitations")
    public ResponseEntity<List<InvitationResponse>> getMyInvitations() {
        List<InvitationResponse> invitations = invitationService.getMyInvitations();
        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/user/{invitationId}/accept")
    public ResponseEntity<String> acceptInvitation(@PathVariable String invitationId) {
        invitationService.acceptInvitation(invitationId);
        return ResponseEntity.ok("Invitation accepted successfully");
    }


    @PostMapping("/user/{invitationId}/reject")
    public ResponseEntity<String> rejectInvitation(@PathVariable String invitationId) {
        invitationService.rejectInvitation(invitationId);
        return ResponseEntity.ok("Invitation rejected");
    }



}