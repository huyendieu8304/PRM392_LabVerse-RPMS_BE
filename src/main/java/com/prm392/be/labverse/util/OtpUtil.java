package com.prm392.be.labverse.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OtpUtil {
    private final Cache<String, String> otpCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public String generateOtp(String email) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        otpCache.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String cached = otpCache.getIfPresent(email);
        if (cached != null && cached.equals(otp)) {
            otpCache.invalidate(email);
            return true;
        }
        return false;
    }
}
