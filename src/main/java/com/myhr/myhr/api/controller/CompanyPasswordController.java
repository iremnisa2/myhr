package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyPasswordService;
import com.myhr.myhr.application.service.RecaptchaService;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyPasswordController {

    private final CompanyPasswordService companyPasswordService;
    private final RecaptchaService recaptchaService;

    @Value("${recaptcha.site}")
    private String recaptchaSiteKey;
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @GetMapping("/set-password")
    public String showSetPasswordForm(Model model) {
        model.addAttribute("siteKey", recaptchaSiteKey);
        return "set-password";
    }

    @PostMapping("/set-password")
    public String handleSetPasswordForm(@RequestParam String token,
                                        @RequestParam String password,
                                        @RequestParam("recaptchaToken") String recaptchaToken,
                                        Model model) {
        boolean ok = recaptchaService.verifyScore(recaptchaToken, "set_password");
        if (!ok) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "reCAPTCHA doğrulaması başarısız!");
        }

        companyPasswordService.setPassword(token, password);
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
