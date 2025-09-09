package com.myhr.myhr.api.controller;

import com.myhr.myhr.api.dto.CompanyApplyRequest;
import com.myhr.myhr.application.service.CompanyService;
import com.myhr.myhr.application.service.RecaptchaService;
import com.myhr.myhr.domain.exception.ApiException;
import com.myhr.myhr.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyApplyPageController {

    private final RecaptchaService recaptchaService;
    private final CompanyService companyService;

    @Value("${recaptcha.site}")
    private String recaptchaSiteKey;


    @GetMapping("/apply-form")
    public String showApplyForm() {
        return """
                <!doctype html>
                <html lang="tr">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Şirket Başvurusu</title>
                    <script src="https://www.google.com/recaptcha/api.js?render=%s"></script>
                    <style>
                      :root{--b:#e5e7eb;--muted:#6b7280;--primary:#2563eb;}
                      body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Inter,Arial,sans-serif;max-width:640px;margin:40px auto;padding:0 16px;}
                      h2{margin-bottom:6px}
                      .muted{color:var(--muted);font-size:14px;margin-top:0}
                      .card{margin-top:14px;padding:24px;border:1px solid var(--b);border-radius:14px;box-shadow:0 4px 16px rgba(0,0,0,.06)}
                      label{display:block;margin:12px 0 6px;font-weight:600}
                      input{width:100%%;padding:11px;border:1px solid var(--b);border-radius:10px}
                      small{color:var(--muted)}
                      button{margin-top:16px;padding:12px 16px;border:0;border-radius:10px;background:var(--primary);color:#fff;cursor:pointer}
                      .ok{color:#065f46}
                      .err{color:#991b1b}
                    </style>
                  </head>
                  <body>
                    <h2>Şirket Başvurusu</h2>
                    <p class="muted">Lütfen bilgileri eksiksiz doldurun.</p>

                    <div class="card">
                      <form id="applyForm" method="post" action="/api/companies/apply-form">
                        <label>Şirket Adı</label>
                        <input type="text" name="name" required minlength="2" maxlength="100" />

                        <label>E-posta</label>
                        <input type="email" name="email" required maxlength="254" />

                        <label>Telefon <small>(E.164: +905xxxxxxxxx)</small></label>
                        <input type="tel" name="phone" required
                               pattern="^\\+[1-9]\\d{7,14}$"
                               title="Telefon E.164 formatında olmalı, örn: +905xxxxxxxxx" />

                        <label>Çalışan Sayısı</label>
                        <input type="number" name="employeeCount" required min="1" max="100000" />

                        <input type="hidden" name="recaptchaToken" id="recaptchaToken" />
                        <button type="submit">Başvuruyu Gönder</button>
                      </form>
                    </div>

                    <script>
                      document.getElementById("applyForm").addEventListener("submit", function(e) {
                        e.preventDefault();
                        grecaptcha.ready(function () {
                          grecaptcha.execute("%s", { action: "apply" }).then(function (token) {
                            document.getElementById("recaptchaToken").value = token;
                            e.target.submit();
                          });
                        });
                      });
                    </script>
                  </body>
                </html>
                """.formatted(recaptchaSiteKey, recaptchaSiteKey);
    }


    @PostMapping(value = "/apply-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> handleApplyForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam Integer employeeCount,
            @RequestParam("recaptchaToken") String recaptchaToken
    ) {
        boolean ok = recaptchaService.verifyScore(recaptchaToken, "apply");
        if (!ok) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "reCAPTCHA doğrulaması başarısız!");
        }

        var req = new CompanyApplyRequest(name, email, phone, employeeCount);
        var resp = companyService.apply(req);

        String html = """
<!doctype html>
<html lang="tr">
  <head>
    <meta charset="UTF-8">
    <title>Başvuru Alındı</title>
    <style>
      body {
        margin:0; height:100%%;
        display:flex; align-items:center; justify-content:center;
        background:#f9fafb;
        font-family:system-ui,-apple-system,Segoe UI,Roboto,Inter,Arial,sans-serif;
      }
      .card {
        background:#fff;
        padding:48px;
        border-radius:18px;
        box-shadow:0 14px 32px rgba(0,0,0,.12);
        text-align:center;
        max-width:560px;
        width:100%%;
      }
      h2 {
        color:#065f46;
        font-size:28px;
        margin-bottom:16px;
      }
      p {
        font-size:20px;
        color:#374151;
        margin:8px 0;
      }
    </style>
  </head>
  <body>
    <div class="card">
      <h2>Başvurunuz alındı ✅</h2>
      <p>Teşekkürler, <b>%s</b>.</p>
      <p>Başvuru e-postası: <b>%s</b></p>
      <p>En kısa sürede sizinle iletişime geçilecek.</p>
    </div>
  </body>
</html>
""".formatted(resp.name(), resp.email());
        return ResponseEntity.ok(html);

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
        font-family:system-ui,-apple-system,Segoe UI,Roboto,Inter,Arial,sans-serif;
      }
      .card {
        background:#fff;
        padding:48px;
        border-radius:18px;
        box-shadow:0 14px 32px rgba(0,0,0,.12);
        text-align:center;
        max-width:560px;
        width:100%%;
      }
      h2 {
        color:#991b1b;
        font-size:28px;
        margin-bottom:16px;
      }
      p {
        font-size:20px;
        color:#374151;
        margin:8px 0 20px;
      }
      a.button {
        display:inline-block;
        padding:14px 24px;
        border-radius:12px;
        background:#1e3a8a;
        color:#fff;
        font-size:16px;
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
      <a class="button" href="http://localhost:8080/api/companies/apply-form">Giriş sayfasına dön</a>
    </div>
  </body>
</html>
""".formatted(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(html);


    }
}
