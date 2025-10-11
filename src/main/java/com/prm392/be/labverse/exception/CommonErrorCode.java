package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode {

    SERVER_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_OBJECT_TO_S3_FAIL(5001, "There was error occurred during uploading files.", HttpStatus.SERVICE_UNAVAILABLE),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
