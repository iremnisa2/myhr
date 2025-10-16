package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyPasswordService;
import com.myhr.myhr.application.service.RecaptchaService;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyPasswordController {

    private final CompanyPasswordService companyPasswordService;
    private final RecaptchaService recaptchaService;

    @Value("${recaptcha.site:}")
    private String recaptchaSiteKey;

    @PostMapping("/set-password")
    public String handleSetPasswordForm(@RequestParam String password,
                                        @RequestParam("recaptchaToken") String recaptchaToken,
                                        HttpSession session) {
        boolean ok = recaptchaService.verifyScore(recaptchaToken, "set_password");
        if (!ok) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "reCAPTCHA doğrulaması başarısız!");
        }

        String token = (String) session.getAttribute("PW_TOKEN");
        if (token == null || token.isBlank()) {
            throw new ApiException(ErrorCode.TOKEN_NOT_FOUND, "Oturumda token bulunamadı veya süresi doldu.");
        }

        companyPasswordService.setPassword(token, password);
        session.removeAttribute("PW_TOKEN");

        return "set-password-success";
    }

    @ExceptionHandler(ApiException.class)
    public String handleApi(ApiException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "set-password-error";
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBad(RuntimeException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "set-password-error";
    }
}
