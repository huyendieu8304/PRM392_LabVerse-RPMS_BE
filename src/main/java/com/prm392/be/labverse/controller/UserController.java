package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.user.UserSimpleResponse;
import com.prm392.be.labverse.dto.user.RegisterAccountRequest;
import com.prm392.be.labverse.service.UserService;
import com.prm392.be.labverse.validation.ValidRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Tag(name = "User", description = "API for user-related operations")
public class UserController {

    UserService userService;

    @PostMapping("/register")
    ResponseEntity<UserSimpleResponse> register(@Valid @RequestBody RegisterAccountRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/resent-otp-verify-account")
    ResponseEntity<String> resentOtpVerifyAccount(@RequestParam String email){
        userService.resentOtpVerifyAccount(email);
        return ResponseEntity.ok("Resent OTP verify account successfully");
    }

    @PutMapping("/verify-account")
    ResponseEntity<String> verifyAccount(@RequestParam String email, @RequestParam String otp){
        userService.verifyAccount(email, otp);
        return ResponseEntity.ok("Verify account successfully");
    }

    @PutMapping("/select-role")
    ResponseEntity<UserSimpleResponse> selectRole(@RequestParam String userId, @RequestParam @ValidRole String roleName){
        return ResponseEntity.ok(userService.selectRole(userId, roleName));
    }
}
