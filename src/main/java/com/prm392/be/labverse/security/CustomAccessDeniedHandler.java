package com.prm392.be.labverse.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prm392.be.labverse.dto.ErrorResponse;
import com.prm392.be.labverse.exception.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handle unauthorized access request
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.info("Access denied, catch by AccessDeniedHandler");
        log.warn("Access denied at [{} {}] - user: {} - message: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                accessDeniedException.getMessage());
        //User doesn't have permission to access
        AuthErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;
        response.setStatus(errorCode.getHttpStatus().value()); //Set the http status code
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type

        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());

        ObjectMapper objectMapper = new ObjectMapper(); //map ApiResponse to string to put it in HttpServletResponse
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
