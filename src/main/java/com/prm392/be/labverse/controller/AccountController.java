package com.prm392.be.labverse.controller;

import com.prm392.be.labverse.dto.account.AccountSimpleResponse;
import com.prm392.be.labverse.dto.account.RegisterAccountRequest;
import com.prm392.be.labverse.service.AccountService;
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

    AccountService accountService;

    @PostMapping("/register")
    ResponseEntity<AccountSimpleResponse> register(@Valid @RequestBody RegisterAccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

}
