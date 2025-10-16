package com.myhr.myhr.application.service;

import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @PostConstruct
    void checkConfig() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("app.base-url is not configured");
        }
    }

    public void sendSetPasswordMail(String to, String token) {
        if (!mailEnabled) {
            log.info("[MAIL] Disabled by config, skipping. to={}", to);
            return;
        }

        String link = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/api/companies/set-password")
                .queryParam("token", token)
                .toUriString();

        String subject = "Şifre Belirleme";
        String plain = """
                Merhaba,

                Hesabınızı aktifleştirmek için aşağıdaki bağlantıya tıklayın:
                %s

                Eğer bu işlemi siz başlatmadıysanız bu maili yok sayabilirsiniz.
                """.formatted(link);

        String html = """
                Merhaba,<br><br>
                Hesabınızı aktifleştirmek için aşağıdaki bağlantıya tıklayın:<br>
                <a href="%s">%s</a><br><br>
                Eğer bu işlemi siz başlatmadıysanız bu maili yok sayabilirsiniz.
                """.formatted(link, link);  

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress, "MyHR");
            }
            helper.setTo(to.trim());
            helper.setSubject(subject);
            helper.setText(plain, html);

            mailSender.send(msg);
            log.info("[MAIL] Password-set mail sent. to={}", to);
        } catch (Exception e) {
            String detail = rootCauseMessage(e);
            log.error("[MAIL] send failed: {}", detail, e);
            throw new ApiException(ErrorCode.MAIL_NOT_SEND, "E-posta gönderilemedi: " + detail, e);
        }
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null) r = r.getCause();
        return r.getClass().getSimpleName() + ": " + String.valueOf(r.getMessage());
    }
}
