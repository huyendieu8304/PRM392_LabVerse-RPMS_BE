package com.prm392.be.labverse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReadingStatusErrorCode {

        //from 7001
        READING_STATUS_NOT_FOUND(7001, "The reading status was not found", HttpStatus.BAD_REQUEST),


        ;

        private final int code;
        private final String message;
        private final HttpStatus httpStatus;
}
