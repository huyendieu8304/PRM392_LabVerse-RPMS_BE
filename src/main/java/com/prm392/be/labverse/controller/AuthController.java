package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.auth.LoginRequest;
import com.prm392.be.labverse.dto.auth.LoginResponse;
import com.prm392.be.labverse.dto.auth.LoginWGoogleRequest;
import com.prm392.be.labverse.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//    @GetMapping("/test")
//    public String checkAuth(@RequestHeader("Authorization") String authHeader) {
//        String token = authHeader.replace("Bearer ", "");
//            return token;
//    }
}
