package com.prm392.be.labverse.security;

import com.prm392.be.labverse.dto.auth.LoginRequest;
import com.prm392.be.labverse.dto.auth.LoginResponse;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            // mật khẩu hoặc email sai
            log.info("Login fail, invalid login information - email={}", request.email());
            throw new AppException(AuthErrorCode.INVALID_LOGIN_INFORMATION);
         } catch (DisabledException e) {
            // tài khỏan bị khóa/ inactive/ deleted (isEnable() = false
            log.info("Login fail, inactive account - email={}", request.email());
            throw new AppException(AuthErrorCode.INACTIVE_ACCOUNT);
        } catch (AuthenticationException e) {
            // các lỗi auth khác
            log.info("Login fail, a strange error occur during login");
            log.error(e.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        assert authentication != null;
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userRole = userDetails.getRole().getName().toString();

        // login success
        String accessTk = jwtUtil.generateAccessToken(request.email(), userRole);
        return new LoginResponse(accessTk);
    }

}
