package com.prm392.be.labverse.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    ResponseEntity<String> appExceptionHandler(AppException e) {
        log.info("Exception is catch by appExceptionHandler, exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(e.getCode() + ":" + e.getMessage());
    }

    //for fail validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Exception is catch by handleValidationException");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        //TODO: test return cua cai nay
        return ResponseEntity.badRequest().body(errors);
//        {
//            "password": "INVALID_PASSWORD",
//                "email": "INVALID_EMAIL"
//        }
    }
}
