package com.prm392.be.labverse.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class SecurityConfig {
    private String[] publicEndpoints = {
            "/api/accounts/register",
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
//                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(getAllowCorsUrl());
//                        config.setAllowedMethods(Collections.singletonList("*"));
//                        config.setAllowedHeaders(Collections.singletonList("*"));
//                        config.setExposedHeaders(Collections.singletonList("Authorization"));
//                        config.setAllowCredentials(true);
//                        return config;
//                    }
//                }))
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) //unauthenticated request
//                        .accessDeniedHandler(new CustomAccessDeniedHandler()) //unauthorized access
//                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //make each request independently
                .authorizeHttpRequests( //authorization in http url
                        request -> request
                                //open public endpoints
                                .requestMatchers(publicEndpoints).permitAll()
                                //endpoints for user has role CAR_OWNER
//
//                                .requestMatchers("/car/operator/**").hasRole("OPERATOR")
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
