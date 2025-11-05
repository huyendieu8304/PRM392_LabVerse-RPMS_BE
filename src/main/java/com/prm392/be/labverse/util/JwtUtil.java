package com.prm392.be.labverse.util;

import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.AuthErrorCode;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${application.security.jwt.access-token-secret-key}")
    @NonFinal
    private String accessTokenSecretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    @NonFinal
    private long accessTokenExpiration;

    @Value("${application.domain-name}")
    @NonFinal
    private String domainName;

    /**
     * transform secret key string to SecretKey object
     * @return SecretKey object which is used in generate and decode token
     */
    private SecretKey getSecretKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * ==================================================================================
     * Generate jwt
     */
    public String generateAccessToken(String userEmail, String userRole, String userId) {
        return generateJwtToken(userEmail, userRole, userId, accessTokenSecretKey, accessTokenExpiration);
    }

    private String generateJwtToken(String email, String userRole, String userId, String secretKey, long expiration) {
        return Jwts.builder()
                .subject(email)
                .claim("role", userRole)
                .claim("id", userId)
                .issuer(domainName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration)) //set expiration date for token
                .signWith(getSecretKey(secretKey)) //the algorithm is automatically determine by the api of jjwt
                .compact();
    }

    /**
     * ==================================================================================
     * Validate jwt
     */
    /**
     * Validate the access token
     * @param accessToken
     * @return
     */
    public boolean validateJwtAccessToken(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey(accessTokenSecretKey))
                    .build()
                    .parse(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            //jwt invalid or empty/null
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * ==================================================================================
     * Get claim from jwt
     */

    /**
     * decode the token then get the email from the claim "sub"
     * @param accessToken
     * @return
     */
    public String getUserEmailFromAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey(accessTokenSecretKey))
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
    }

    public LocalDateTime getExpirationFromToken(String accessToken) {
        Instant expInstant = Jwts.parser()
                .verifyWith(getSecretKey(accessTokenSecretKey))
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getExpiration()
                .toInstant();
        return LocalDateTime.ofInstant(expInstant, ZoneId.systemDefault());
    }
    public String getUserRoleFromAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey(accessTokenSecretKey))
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .get("role")
                .toString();
    }
    public String getUserIdFromAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey(accessTokenSecretKey))
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .get("id")
                .toString();
    }
}
