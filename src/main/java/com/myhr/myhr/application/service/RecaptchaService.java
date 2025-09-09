package com.myhr.myhr.application.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.minScore:0.5}")
    private double minScore;

    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";


    public boolean verifyScore(String token, String expectedAction) {
        RestTemplate rt = new RestTemplate();
        RecaptchaResponse resp = rt.postForObject(
                String.format(VERIFY_URL, secret, token), null, RecaptchaResponse.class);

        if (resp == null || !resp.success) return false;
        if (expectedAction != null && resp.action != null && !expectedAction.equals(resp.action)) return false;
        return resp.score >= minScore;
    }

    @Data
    static class RecaptchaResponse {
        private boolean success;
        private double score;
        private String action;
        private String challenge_ts;
        private String hostname;
        private String[] errorCodes;
    }
}
