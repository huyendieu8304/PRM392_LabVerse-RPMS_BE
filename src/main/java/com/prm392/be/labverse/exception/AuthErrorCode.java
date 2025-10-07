package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode {

    //from 4001
    UNAUTHENTICATED(4001, "Unauthenticated access", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_EXIST(4002, "Account not exist", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(4003, "The user doesn't have permission to access the endpoint.", HttpStatus.FORBIDDEN),
    INVALID_LOGIN_INFORMATION(4004, "Either email address or password is incorrect.", HttpStatus.UNAUTHORIZED),
    MISSING_ACCESS_TOKEN(4005, "Missing access token.", HttpStatus.UNAUTHORIZED),
    INACTIVE_ACCOUNT(4006, "Inactive account.", HttpStatus.FORBIDDEN),
    GG_LOGIN_MISSING_TOKEN(4007, "Missing ID Token.", HttpStatus.UNAUTHORIZED),
    GG_LOGIN_INVALID_TOKEN(4008, "Invalid ID Token.", HttpStatus.UNAUTHORIZED),
    GG_LOGIN_CANT_PARSE_TOKEN(4009, "Can not read token or key of Google.", HttpStatus.UNAUTHORIZED),
    GG_LOGIN_CANT_VERIFY_TOKEN(4010, "Can not verifying Google token.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED_OR_INVALIDATED(4011, "Access token expired or invalidated.", HttpStatus.UNAUTHORIZED),

    SERVER_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
