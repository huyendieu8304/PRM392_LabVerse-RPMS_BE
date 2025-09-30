package com.prm392.be.labverse.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends RuntimeException{
    private int code;
    private String message;
    private HttpStatus httpStatus;

    public AppException(final int code, final String message, final HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public AppException(AccountErrorCode error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

}
