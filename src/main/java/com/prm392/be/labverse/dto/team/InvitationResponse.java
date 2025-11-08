package com.prm392.be.labverse.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    private String id;              // Membership ID
    private String teamId;
    private String teamName;
    private String teamDescription;
    private String invitedBy;       // Tên người mời
    private String invitedUserEmail;
    private String status;          // PENDING, APPROVED, REJECTED
    private LocalDateTime invitedAt;
    private LocalDateTime expiresAt;
    private int daysLeft;
    private boolean isExpired;
}