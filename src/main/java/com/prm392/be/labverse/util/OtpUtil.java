package com.prm392.be.labverse.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpUtil {
    private static final String FORGOT_PASS_KEY_PREFIX = "pass_";
    private static final String VERIFY_ACC_KEY_PREFIX = "acc_";

    private final Cache<String, String> otpCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public String generateForgotPassOtp(String email) {
        String key = FORGOT_PASS_KEY_PREFIX + normalize(email);
        return generateOtp(key);
    }

    public boolean isValidForgotPassOtp(String email, String otp) {
        String key = FORGOT_PASS_KEY_PREFIX + normalize(email);
        return isOtpValid(key, otp);
    }

    public String regenerateForgotPassOtp(String email) {
        String key = FORGOT_PASS_KEY_PREFIX + normalize(email);
        return regenerateOtp(key);
    }


    public String generateVerifyAccOtp(String email) {
        String key = VERIFY_ACC_KEY_PREFIX + normalize(email);
        return generateOtp(key);
    }

    public boolean isValidVerifyAccOtp(String email, String otp) {
        String key = VERIFY_ACC_KEY_PREFIX + normalize(email);
        return isOtpValid(key, otp);
    }

    public String regenerateVerifyAccOtp(String email) {
        String key = VERIFY_ACC_KEY_PREFIX + normalize(email);
        return regenerateOtp(key);
    }

    private String regenerateOtp(String key) {
        String cached = otpCache.getIfPresent(key);
        if (cached != null) {
            // còn hạn, invalidate, tạo opt mới
            otpCache.invalidate(key);
        }
        // tạo mới
        return generateOtp(key);
    }

    private String generateOtp(String key) {
        // 6 digits
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(1_000_000));
        otpCache.put(key, otp);
        return otp;
    }

    private boolean isOtpValid(String key, String otp) {
        String cached = otpCache.getIfPresent(key);
        if (cached != null && cached.equals(otp)) {
            otpCache.invalidate(key);
            return true;
        }
        return false;
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
