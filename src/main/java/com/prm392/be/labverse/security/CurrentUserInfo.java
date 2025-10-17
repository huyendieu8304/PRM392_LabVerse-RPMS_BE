package com.prm392.be.labverse.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserInfo {
    private String userId;
    private String email;
    private String roles;
}
