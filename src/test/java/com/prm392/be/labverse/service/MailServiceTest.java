package com.prm392.be.labverse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private com.prm392.be.labverse.service.MailService mailService;

    @Test
    void testSendRegisterOtp() {
        mailService.sendRegisterOTP("anhvucp6@gmail.com", "123456");
        System.out.println("✅ Register OTP sent!");
    }

    @Test
    void testSendForgotPasswordOtp() {
        mailService.sendForgotPasswordOTP("anhvucp6@gmail.com", "789012");
        System.out.println("✅ Forgot Password OTP sent!");
    }

    @Test
    void testSendEnglishOtp() {
        mailService.sendRegisterOTP("anhvucp6@gmail.com", "222333", Locale.ENGLISH);
        System.out.println("✅ English OTP sent!");
    }
}
