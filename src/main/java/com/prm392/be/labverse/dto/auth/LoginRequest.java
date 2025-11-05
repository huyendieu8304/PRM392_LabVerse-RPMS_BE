package com.prm392.be.labverse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "INVALID_EMAIL")
        @Email(message = "INVALID_EMAIL")
        String email,
        @NotBlank( message = "INVALID_PASSWORD")
        String password
) {

}
