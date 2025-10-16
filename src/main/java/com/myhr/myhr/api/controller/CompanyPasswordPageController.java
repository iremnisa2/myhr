package com.myhr.myhr.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
public class CompanyPasswordPageController {

    @Value("${recaptcha.site:}")
    private String recaptchaSiteKey;

    @GetMapping("/set-password")
    public String showSetPasswordForm(
            @RequestParam(value = "token", required = false) String token,
            HttpSession session,
            Model model
    ) {
        if (token != null && !token.isBlank()) {
            session.setAttribute("SET_PW_TOKEN", token);
        }
        model.addAttribute("siteKey", recaptchaSiteKey);
        return "set-password";
    }


}
