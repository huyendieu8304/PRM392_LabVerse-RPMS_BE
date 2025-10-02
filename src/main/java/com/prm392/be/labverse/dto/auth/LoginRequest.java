package com.prm392.be.labverse.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "INVALID_EMAIL")
        @Email(message = "INVALID_EMAIL")
        String email,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "INVALID_PASSWORD")
        String password
) {

}
