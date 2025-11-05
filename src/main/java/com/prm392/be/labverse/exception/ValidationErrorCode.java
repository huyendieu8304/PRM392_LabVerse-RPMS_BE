package com.prm392.be.labverse.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ValidationErrorCode {

    //from 1000
    INVALID_EMAIL(1001, "Invalid email address.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1002, "The password must have at least 8 characters, contain at least 1 digit", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(1003, "Invalid role name.", HttpStatus.BAD_REQUEST),


    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}