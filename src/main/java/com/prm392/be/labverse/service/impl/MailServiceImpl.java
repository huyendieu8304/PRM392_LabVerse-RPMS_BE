package com.prm392.be.labverse.service.impl;

import com.prm392.be.labverse.config.MailingProperties;
import com.prm392.be.labverse.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class
MailServiceImpl implements com.prm392.be.labverse.service.MailService {

    private final JavaMailSender mailSender;
    private final com.prm392.be.labverse.config.MailingProperties props;

    @Override
    public void sendRegisterOTP(String email, String otp) {
        sendRegisterOTP(email, otp, defaultLocale());
    }

    @Override
    public void sendForgotPasswordOTP(String email, String otp) {
        sendForgotPasswordOTP(email, otp, defaultLocale());
    }

    @Override
    public void sendRegisterOTP(String email, String otp, Locale locale) {
        String lang = normalize(locale);
        String subject = lang.equals("en") ? "[Lab Verse] Verify your account" : "[Lab Verse] Mã xác nhận đăng ký tài khoản";
        String heading = lang.equals("en") ? "Account registration" : "Đăng ký tài khoản";
        String html = buildOtpHtml(heading, otp, lang);
        send(email, subject, html);
    }

    @Override
    public void sendForgotPasswordOTP(String email, String otp, Locale locale) {
        String lang = normalize(locale);
        String subject = lang.equals("en") ? "[Lab Verse] Password reset code" : "[Lab Verse] Mã xác thực quên mật khẩu";
        String heading = lang.equals("en") ? "Password reset" : "Khôi phục mật khẩu";
        String html = buildOtpHtml(heading, otp, lang);
        send(email, subject, html);
    }

    /* ---------------- helpers ---------------- */

    private void send(String to, String subject, String html) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
            );
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            if (props.getFrom() != null && !props.getFrom().isBlank()) {
                helper.setFrom(props.getFrom());
            }
            if (props.getReplyTo() != null && !props.getReplyTo().isBlank()) {
                helper.setReplyTo(props.getReplyTo());
            }

            ClassPathResource logo = new ClassPathResource("email/logo_labverse.png");
            helper.addInline("logo", logo, "image/png");

            mailSender.send(mime);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Cannot send email right now");
        }
    }

    private String normalize(@Nullable Locale locale) {
        String def = props.getDefaultLocale();
        String lang = locale == null ? def : locale.getLanguage();
        return "en".equalsIgnoreCase(lang) ? "en" : "vi";
    }

    private Locale defaultLocale() {
        return "en".equalsIgnoreCase(props.getDefaultLocale()) ? Locale.ENGLISH : new Locale("vi");
    }

    /** HTML template: gọn, responsive cơ bản, có logo + hai ngôn ngữ. */
    private String buildOtpHtml(String heading, String otp, String lang) {
        String brand = props.getBrand().getName();
        String logo = props.getBrand().getLogoUrl();
        String color = props.getBrand().getPrimaryColor();

        String intro = lang.equals("en")
                ? "Use the verification code below to continue."
                : "Hãy dùng mã xác thực bên dưới để tiếp tục.";
        String hint = lang.equals("en")
                ? "If you didn’t request this, you can safely ignore this email."
                : "Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.";
        String footer = lang.equals("en")
                ? "This email was sent by %s.".formatted(brand)
                : "Email này được gửi bởi %s.".formatted(brand);

        return """
        <html>
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <title>%s · %s</title>
          </head>
          <body style="margin:0;background:#f6f8fb">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f6f8fb;padding:24px 12px">
              <tr>
                <td align="center">
                  <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:16px;box-shadow:0 8px 28px rgba(0,0,0,0.06);overflow:hidden">
                    <tr>
                      <td style="padding:18px 22px;border-bottom:1px solid #eef2f7" align="left">
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="font-family:Inter,Arial,sans-serif;font-size:16px;font-weight:700;color:#0f172a">
                              <span style="display:inline-flex;gap:10px;align-items:center">
                                %s
                                <span>%s</span>
                              </span>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:26px 26px 8px 26px">
                        <h1 style="margin:0 0 10px 0;font-family:Inter,Arial,sans-serif;font-size:22px;color:#0f172a">%s</h1>
                        <p style="margin:0 0 14px 0;font-family:Inter,Arial,sans-serif;font-size:15px;color:#334155">%s</p>
                        <div style="margin:16px 0;padding:16px;border-radius:12px;background:#f8fafc;border:1px dashed #e2e8f0;text-align:center">
                          <div style="font-family:ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
                                      font-size:28px;letter-spacing:6px;font-weight:800;color:#0f172a">
                            %s
                          </div>
                        </div>
                        <p style="margin:14px 0 0 0;font-family:Inter,Arial,sans-serif;font-size:13px;color:#6b7280">%s</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:16px 26px;border-top:1px solid #eef2f7">
                        <p style="margin:0;font-family:Inter,Arial,sans-serif;font-size:12px;color:#94a3b8">%s</p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(
                heading, brand,
                (logo == null || logo.isBlank())
                        ? "<span style='width:26px;height:26px;border-radius:8px;background:"+color+";display:inline-block'></span>"
                        : "<img src='cid:logo' alt='"+brand+"' style='height:26px'>",
                brand,
                heading,
                intro,
                otp,
                hint,
                footer
        );
    }
}