// com.myhr.myhr.application.service.RecaptchaService.java

package com.myhr.myhr.application.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // WebClient importu

@Service
@RequiredArgsConstructor
public class RecaptchaService {


    private final WebClient webClient;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.minScore:0.5}")
    private double minScore;


    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";


    public boolean verifyScore(String token, String expectedAction) {


        RecaptchaResponse resp = webClient.post()
                .uri(VERIFY_URL, uriBuilder -> uriBuilder

                        .queryParam("secret", secret)
                        .queryParam("response", token)
                        .build())
                .retrieve()
                .bodyToMono(RecaptchaResponse.class)
                .block();



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