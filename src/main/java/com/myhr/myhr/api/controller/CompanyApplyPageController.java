package com.myhr.myhr.api.controller;

import com.myhr.myhr.api.dto.CompanyApplyRequest;
import com.myhr.myhr.application.service.CompanyService;
import com.myhr.myhr.application.service.RecaptchaService;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyApplyPageController {

    private final RecaptchaService recaptchaService;
    private final CompanyService companyService;

    @Value("${recaptcha.site}")
    private String recaptchaSiteKey;

    @GetMapping("/apply-form")
    public String showApplyForm(Model model) {
        model.addAttribute("siteKey", recaptchaSiteKey);
        return "apply-form";
    }

    @PostMapping("/apply-form")
    public String handleApplyForm(@RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String phone,
                                  @RequestParam Integer employeeCount,
                                  @RequestParam("recaptchaToken") String recaptchaToken,
                                  Model model) {
        boolean ok = recaptchaService.verifyScore(recaptchaToken, "apply");
        if (!ok) throw new ApiException(ErrorCode.UNAUTHORIZED, "reCAPTCHA doğrulaması başarısız!");

        var req  = new CompanyApplyRequest(name, email, phone, employeeCount);
        var resp = companyService.apply(req);

        model.addAttribute("name",  resp.name());
        model.addAttribute("email", resp.email());
        return "apply-success";
    }

    @ExceptionHandler(ApiException.class)
    public String handleApi(ApiException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "apply-error";
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBad(RuntimeException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "apply-error";
    }
}
