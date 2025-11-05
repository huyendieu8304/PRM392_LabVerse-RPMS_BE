package com.prm392.be.labverse.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private String address;
    private String phoneNumber;
}
