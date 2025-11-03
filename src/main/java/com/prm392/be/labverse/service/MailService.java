package com.prm392.be.labverse.service;

import java.util.Locale;

public interface MailService {
    void sendRegisterOTP(String email, String otp);                    // VI mặc định
    void sendForgotPasswordOTP(String email, String otp);              // VI mặc định

    void sendRegisterOTP(String email, String otp, Locale locale);     // ép EN nếu cần
    void sendForgotPasswordOTP(String email, String otp, Locale locale);
}