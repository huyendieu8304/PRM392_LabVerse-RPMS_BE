package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    //from 2001
    EMAIL_USED(2001, "The email has been already used by other account", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST_IN_DB(2002, "The role is currently not existing in database", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(2003, "The account was not found", HttpStatus.NOT_FOUND),
    UN_AUTHENTICATED(2004, "No authenticated user found", HttpStatus.UNAUTHORIZED),
    ACCOUNT_VERIFIED(2014, "The account has been already verified", HttpStatus.BAD_REQUEST),
    INVALID_VERIFIED_OTP(2015, "The OTP is invalid or has been already expired/used", HttpStatus.BAD_REQUEST),
    USER_ROLE_ALREADY_SET(2016, "User account has been already set, can not be updated", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
