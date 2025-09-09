package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyApprovalService;
import com.myhr.myhr.application.service.CompanyPasswordService;
import com.myhr.myhr.application.service.RecaptchaService;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyPasswordController {
    private final CompanyApprovalService approvalService;
    private final CompanyPasswordService companyPasswordService;
    private final RecaptchaService recaptchaService;

    @Value("${recaptcha.site}")
    private String recaptchaSiteKey;

    @GetMapping("/set-password")
    public String showSetPasswordForm() {

        return """
        <!doctype html>
        <html lang="tr">
          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Şifre Belirle</title>
            <script src="https://www.google.com/recaptcha/api.js?render=%s"></script>
            <style>
              :root {
                --bg:#0b1220;
                --card:#ffffff;
                --border:#e5e7eb;
                --text:#111827;
                --muted:#6b7280;
                --primary:#1e3a8a; /* lacivert */
                --primary-hover:#172c6c;
              }
              * { box-sizing: border-box; }
              html, body { height: 100%%; margin:0; }
              body {
                background: radial-gradient(1200px 800px at 20%% 0%%, rgba(30,58,138,.12), transparent 50%%) no-repeat,
                            radial-gradient(1000px 600px at 120%% -20%%, rgba(30,58,138,.10), transparent 50%%) no-repeat,
                            #f8fafc;
                font-family: system-ui, -apple-system, Segoe UI, Roboto, Inter, Arial, sans-serif;
                display:flex; align-items:center; justify-content:center; padding:24px;
                color: var(--text);
              }
              .card {
                width: 100%%; max-width: 480px;
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 16px;
                box-shadow: 0 20px 40px rgba(2,6,23,.08);
                padding: 28px;
              }
              h2 { margin: 0 0 4px; font-size: 22px; }
              .sub { margin:0 0 18px; color: var(--muted); font-size: 14px; }
              label { display:block; margin: 14px 0 6px; font-weight: 600; font-size: 14px; }
              input[type="text"],
              input[type="password"] {
                width: 100%%;
                padding: 14px 12px;
                border: 1px solid var(--border);
                border-radius: 12px;
                font-size: 16px;
                outline: none;
                transition: box-shadow .15s ease, border-color .15s ease;
                background: #fff;
              }
              input:focus {
                border-color: var(--primary);
                box-shadow: 0 0 0 4px rgba(30,58,138,.12);
              }
              .hint { color: var(--muted); font-size: 12px; margin-top: 4px; }
              button {
                width: 100%%;
                margin-top: 18px;
                padding: 14px 16px;
                border: 0;
                border-radius: 12px;
                font-size: 16px;
                font-weight: 600;
                letter-spacing:.2px;
                color: #fff;
                background: linear-gradient(180deg, var(--primary), var(--primary-hover));
                cursor: pointer;
                transform: translateZ(0);
                transition: filter .15s ease, transform .02s ease-in-out, box-shadow .15s ease;
                box-shadow: 0 10px 20px rgba(30,58,138,.18), 0 3px 8px rgba(0,0,0,.08);
              }
              button:hover { filter: brightness(1.02); }
              button:active { transform: translateY(1px); }
              .row { display:flex; gap:12px; }
              .row > div { flex:1; }
              @media (max-width: 520px) {
                .row { flex-direction: column; }
              }
            </style>
          </head>
          <body>
            <div class="card">
              <h2>Şifre Belirleme</h2>
              <p class="sub">Lütfen mailinize gelen token ile yeni şifrenizi girin.</p>

              <form id="passwordForm" method="post" action="/api/companies/set-password">
                <label>Maildeki Token</label>
                <input type="text" name="token" required placeholder="Örn: 7b1a2c..."/>

                <label>Şifre</label>
                <input type="password" name="password"
                       required minlength="8"
                       pattern="^(?=.*[A-Za-z])(?=.*\\d).{8,}$"
                       title="Şifre en az 8 karakter, 1 harf ve 1 rakam içermeli"
                       placeholder="En az 8 karakter, 1 harf ve 1 rakam"/>
                <div class="hint">Güvenlik için güçlü bir şifre seçin.</div>

                <input type="hidden" name="recaptchaToken" id="recaptchaToken" />
                <button type="submit">Gönder</button>
              </form>
            </div>

            <script>
              document.getElementById("passwordForm").addEventListener("submit", function(e) {
                e.preventDefault();
                grecaptcha.ready(function () {
                  grecaptcha.execute("%s", { action: "set_password" }).then(function (token) {
                    document.getElementById("recaptchaToken").value = token;
                    document.getElementById("passwordForm").submit();
                  });
                });
              });
            </script>
          </body>
        </html>
        """.formatted(recaptchaSiteKey, recaptchaSiteKey);

    }

    @PostMapping("/set-password")
    public String handleSetPasswordForm(@RequestParam String token,
                                        @RequestParam String password,
                                        @RequestParam("recaptchaToken") String recaptchaToken) {

        boolean ok = recaptchaService.verifyScore(recaptchaToken, "set_password");
        if (!ok) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "reCAPTCHA doğrulaması başarısız!");
        }

        approvalService.setPasswordWithToken(token, password);


        return """
        <!doctype html>
        <html lang="tr">
          <head>
            <meta charset="UTF-8">
            <title>Başarılı</title>
            <style>
              body {
                margin:0; height:100%%;
                display:flex; align-items:center; justify-content:center;
                background:#f9fafb;
                font-family: system-ui, -apple-system, Segoe UI, Roboto, Inter, Arial, sans-serif;
              }
              .card {
                background:#fff;
                padding:40px;
                border-radius:16px;
                box-shadow:0 12px 28px rgba(0,0,0,.12);
                text-align:center;
                max-width:480px;
              }
              h2 {
                color:#065f46; /* yeşil ton */
                font-size:26px;
                margin-bottom:12px;
              }
              p {
                font-size:18px;
                color:#374151;
              }
            </style>
          </head>
          <body>
            <div class="card">
              <h2>Şifre başarıyla oluşturuldu ✅</h2>
              <p>Artık hesabınıza giriş yapabilirsiniz.</p>
            </div>
          </body>
        </html>
        """;

    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApi(ApiException ex) {
        String html = """
        <!doctype html>
        <html lang="tr">
          <head>
            <meta charset="UTF-8">
            <title>Hata</title>
            <style>
              body {
                margin:0; height:100%%;
                display:flex; align-items:center; justify-content:center;
                background:#f9fafb;
                font-family: system-ui, -apple-system, Segoe UI, Roboto, Inter, Arial, sans-serif;
              }
              .card {
                background:#fff;
                padding:40px;
                border-radius:16px;
                box-shadow:0 12px 28px rgba(0,0,0,.12);
                text-align:center;
                max-width:480px;
              }
              h2 {
                color:#991b1b; /* kırmızı ton */
                font-size:26px;
                margin-bottom:12px;
              }
              p {
                font-size:18px;
                color:#374151;
                margin-bottom:20px;
              }
              a.button {
                display:inline-block;
                padding:12px 20px;
                border-radius:10px;
                background:#1e3a8a;
                color:#fff;
                font-weight:600;
                text-decoration:none;
                transition:background .2s;
              }
              a.button:hover {
                background:#172c6c;
              }
            </style>
          </head>
          <body>
            <div class="card">
              <h2>İşlem Başarısız ❌</h2>
              <p>%s</p>
               <a class="button" href="http://localhost:8080/api/companies/set-password">Giriş sayfasına dön</a>
            </div>
          </body>
        </html>
        """.formatted(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(html);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBad(RuntimeException ex) {
        String html = """
        <!doctype html>
        <html lang="tr">
          <head>
            <meta charset="UTF-8">
            <title>Hata</title>
            <style>
              body {
                margin:0; height:100%%;
                display:flex; align-items:center; justify-content:center;
                background:#f9fafb;
                font-family: system-ui, -apple-system, Segoe UI, Roboto, Inter, Arial, sans-serif;
              }
              .card {
                background:#fff;
                padding:40px;
                border-radius:16px;
                box-shadow:0 12px 28px rgba(0,0,0,.12);
                text-align:center;
                max-width:480px;
              }
              h2 {
                color:#991b1b; /* kırmızı ton */
                font-size:26px;
                margin-bottom:12px;
              }
              p {
                font-size:18px;
                color:#374151;
                margin-bottom:20px;
              }
              a.button {
                display:inline-block;
                padding:12px 20px;
                border-radius:10px;
                background:#1e3a8a;
                color:#fff;
                font-weight:600;
                text-decoration:none;
                transition:background .2s;
              }
              a.button:hover {
                background:#172c6c;
              }
            </style>
          </head>
          <body>
            <div class="card">
              <h2>İşlem Başarısız ❌</h2>
              <p>%s</p>
             <a class="button" href="http://localhost:8080/api/companies/set-password">Giriş sayfasına dön</a>
                
            </div>
          </body>
        </html>
        """.formatted(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(html);
    }
}
