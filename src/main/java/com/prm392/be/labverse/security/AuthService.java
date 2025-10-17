package com.prm392.be.labverse.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.dto.auth.LoginRequest;
import com.prm392.be.labverse.dto.auth.LoginResponse;
import com.prm392.be.labverse.dto.auth.LoginWGoogleRequest;
import com.prm392.be.labverse.entity.InvalidatedToken;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.exception.CommonErrorCode;
import com.prm392.be.labverse.repository.InvalidatedTokenRepository;
import com.prm392.be.labverse.service.UserService;
import com.prm392.be.labverse.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserService userService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;


    @Value("${application.client-id}")
    private String WEB_CLIENT_ID;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

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
            throw new AppException(CommonErrorCode.SERVER_ERROR);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        assert authentication != null;
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userRole = userDetails.getRole().getName().toString();

        // login success
        String userId = userDetails.getUserId();
        String accessTk = jwtUtil.generateAccessToken(request.email(), userRole, userId);
        return new LoginResponse(accessTk, userId);
    }

    public LoginResponse loginWGoogle(LoginWGoogleRequest request) {
        String idTokenString = request.idToken();
        if (idTokenString == null) {
            log.info("android request is missing idToken");
            throw new AppException(AuthErrorCode.GG_LOGIN_MISSING_TOKEN);
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(WEB_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                log.info("login with gg false due to invalid ID token");
                throw new AppException(AuthErrorCode.GG_LOGIN_INVALID_TOKEN);
            }
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            //todo tạm thời fix cứng role
            String roleName = ERole.INTERN.name();
            String defaultPassword = generateRandomString(8); //random rồi, khỏi mã hóa
            // Lưu hoặc đăng nhập người dùng
            User user = userService.findOrCreateUser(email, name, roleName, defaultPassword);

            //login successful
            //generate access token
            return new LoginResponse(jwtUtil.generateAccessToken(email, roleName, user.getId()), user.getId());

        } catch (IOException e) {
            //khi có lỗi trong quá trình đọc hoặc phân tích (parse) nội dung idTokenString.
            log.info("Cannot read token or key of Google");
            throw new AppException(AuthErrorCode.GG_LOGIN_CANT_PARSE_TOKEN);
        } catch (GeneralSecurityException e) {
            //token có định dạng hợp lệ, nhưng chữ ký hoặc tính an toàn không đạt yêu cầu.
            log.info("Cannot verifying Google token");
            throw new AppException(AuthErrorCode.GG_LOGIN_CANT_VERIFY_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("server error");
            throw new AppException(CommonErrorCode.SERVER_ERROR);
        }
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    @Transactional
    public void logout(String accessToken) {
        LocalDateTime expiredAt = jwtUtil.getExpirationFromToken(accessToken);

        // Lưu vào danh sách token bị vô hiệu hóa
        InvalidatedToken token = new InvalidatedToken();
        token.setAccessToken(accessToken);
        token.setExpiredAt(expiredAt);
        invalidatedTokenRepository.save(token);
    }

}
