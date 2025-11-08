package com.prm392.be.labverse.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends RuntimeException{
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    public String getRootCauseMessage() {
        if (getCause() != null) {
            return "Root cause: " + getCause().getMessage();
        }
        return message;
    }

    public AppException(UserErrorCode error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public AppException(AuthErrorCode error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public AppException(CommonErrorCode error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public AppException(PaperErrorCode error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public AppException(PaperAnnotationErrorCode error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public AppException(TeamErrorCode error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }


}
