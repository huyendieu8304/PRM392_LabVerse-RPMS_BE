package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.account.UserSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.service.UserService;
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

}
