package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.auth.LoginRequest;
import com.prm392.be.labverse.dto.auth.LoginResponse;
import com.prm392.be.labverse.dto.auth.LoginWGoogleRequest;
import com.prm392.be.labverse.dto.auth.VerifyForgotPasswordOtpResponse;
import com.prm392.be.labverse.security.AuthService;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication's operations")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.login(loginRequest));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody LoginWGoogleRequest request) {
        return ResponseEntity.ok().body(authService.loginWGoogle(request));
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return "Logged out successfully";
    }


    @GetMapping("/forgot-pass")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("OTP sent to the email.");
    }

    @PostMapping("/verify-reset-password-otp")
    public ResponseEntity<VerifyForgotPasswordOtpResponse> verifyResetPasswordOtp(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(authService.verifyResetPasswordOtp(email, otp));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String resetPasswordToken, @RequestParam String newPassword) {
        authService.resetPassword(email, resetPasswordToken, newPassword);
        return ResponseEntity.ok("Reset password successfully.");
    }
}
