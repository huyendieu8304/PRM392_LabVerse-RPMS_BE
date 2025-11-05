package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaperAnnotationErrorCode {

    //from 6001
    ANNOTATION_NOT_FOUND(6001, "The annotation was not found", HttpStatus.BAD_REQUEST),
//    PAPER_IS_DELETED(3002, "The paper has been already deleted", HttpStatus.BAD_REQUEST),


    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
