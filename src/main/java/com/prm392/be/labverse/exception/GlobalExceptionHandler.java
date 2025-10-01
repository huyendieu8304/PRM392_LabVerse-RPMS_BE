package com.prm392.be.labverse.exception;

import jakarta.validation.ConstraintViolationException;
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

    /**
     * for fail validation, object/DTO validation
     * when using @Valid @Validate in DTO(request body)
     *
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Exception is catch by handleValidationException");
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(e -> {
                            try {
                                ValidationErrorCode errorCode = ValidationErrorCode.valueOf(e.getDefaultMessage());
                                errors.put(e.getField(), errorCode.getMessage());
                            } catch (IllegalArgumentException exception) { //the error code not existed
                                log.error("Invalid error code");
                                errors.put(e.getField(), "Invalid data input");
                            }
                        }
                );
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * for fail validation, method-level validation
     * validate method's parameters (request param, path variable, service layer)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("Exception is caught by handleConstraintViolationException");
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            try {
                // Lấy error code trong message
                ValidationErrorCode errorCode = ValidationErrorCode.valueOf(violation.getMessage());

                // Lấy tên field từ propertyPath (vd: "registerAccountRequest.email")
                String field = violation.getPropertyPath().toString();
                // Nếu muốn chỉ lấy tên cuối cùng (vd: "email")
                if (field.contains(".")) {
                    field = field.substring(field.lastIndexOf('.') + 1);
                }

                errors.put(field, errorCode.getMessage());
            } catch (IllegalArgumentException exception) { // error code không tồn tại
                log.error("Invalid error code");
                String field = violation.getPropertyPath().toString();
                if (field.contains(".")) {
                    field = field.substring(field.lastIndexOf('.') + 1);
                }
                errors.put(field, "Invalid data input");
            }
        });

        return ResponseEntity.badRequest().body(errors);
    }

}
