package com.prm392.be.labverse.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prm392.be.labverse.dto.ErrorResponse;
import com.prm392.be.labverse.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler the case that the provided token is invalid or not provided in the header
 */
@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {


        log.info("Exception occurs during authenticate -> go to AuthEntryPointJwt commence");

        // Is request response with 404
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type

            ErrorResponse apiResponse = new ErrorResponse(4999, "Resource not found");

            ObjectMapper objectMapper = new ObjectMapper(); //map ApiResponse to string to put it in HttpServletResponse
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            response.getWriter().flush();
            return;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); //Set the http status code
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type
        //Set the body of the response
        ObjectMapper objectMapper = new ObjectMapper(); //map ApiResponse to string to put it in HttpServletResponse
        response.getWriter().write(objectMapper.writeValueAsString(request.getAttribute("AUTH_RESPONSE")));
        response.getWriter().flush();
    }
}
