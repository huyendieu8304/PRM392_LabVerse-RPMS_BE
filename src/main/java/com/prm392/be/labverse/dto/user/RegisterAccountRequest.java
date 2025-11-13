package com.prm392.be.labverse.dto.user;

import com.prm392.be.labverse.validation.ValidRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RegisterAccountRequest {
    @NotBlank(message = "INVALID_EMAIL")
    @Email(message = "INVALID_EMAIL")
    private String email;

    //The password must have at least 8 characters, contain at least 1 digit
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "INVALID_PASSWORD")
    private String password;

}
