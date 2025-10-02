package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.account.UserSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.service.UserService;
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
public class AccountController {

    UserService userService;

    @PostMapping("/register")
    ResponseEntity<UserSimpleResponse> register(@Valid @RequestBody RegisterAccountRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

}
