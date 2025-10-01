package com.prm392.be.labverse.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode {

    //from 2001
    EMAIL_USED(2001, "The email has been already used by other account", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST_IN_DB(2002, "The role is currently not existing in database", HttpStatus.BAD_REQUEST),

    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
