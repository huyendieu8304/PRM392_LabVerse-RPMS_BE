package com.prm392.be.labverse.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode {

    INVALID_EMAIL(1001, "Invalid email address.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1002, "The password must have at least 8 characters, contain at least 1 digit", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1003, "Invalid role name.", HttpStatus.BAD_REQUEST),
    EMAIL_USED(1004, "The email has been already used by other account", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST_IN_DB(1005, "The role is currently not existing in database", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
