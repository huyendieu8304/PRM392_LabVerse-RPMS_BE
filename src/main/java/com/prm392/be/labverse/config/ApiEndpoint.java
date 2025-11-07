package com.prm392.be.labverse.config;

public class ApiEndpoint {

    private ApiEndpoint() {
        throw new IllegalStateException("Utility class");
    }
    public static final String[] PUBLIC_API =  {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-config",
            "/swagger-resources/**",
            "/api/accounts/register",
            "/api/accounts/resent-otp-verify-account",
            "/api/accounts/verify-account",
            "/api/accounts/register",
            "/api/auth/login",
            "/api/auth/google",
            "/api/auth/logout",
            "/api/auth/forgot-pass",
            "/api/auth/verify-reset-password-otp",
            "/api/auth/reset-password",

    };

}