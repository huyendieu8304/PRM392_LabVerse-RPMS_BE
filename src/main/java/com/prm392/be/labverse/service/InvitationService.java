package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.team.InvitationRequest;
import com.prm392.be.labverse.dto.team.InvitationResponse;

import java.util.List;

public interface InvitationService {
    void sendInvitation(InvitationRequest request);
    List<InvitationResponse> getMyInvitations();
    void acceptInvitation(String invitationId);
    void rejectInvitation(String invitationId);
}