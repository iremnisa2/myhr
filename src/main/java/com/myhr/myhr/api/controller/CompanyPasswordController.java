package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.service.CompanyPasswordService;
import com.myhr.myhr.domain.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyPasswordController {

    private final CompanyPasswordService companyPasswordService;

    @GetMapping("/set-password")
    @ResponseBody
    public String showSetPasswordForm() {
        return """
                <!doctype html>
                <html lang="tr">
                  <head>
                    <meta charset="UTF-8">
                    <title>Şifre Belirle</title>
                  </head>
                  <body>
                    <h2>Şifre Belirleme</h2>
                    <form method="post" action="/api/companies/set-password">
                
                      <label>Maildeki Token:</label><br>
                      <input type="text" name="token" required /><br><br>
                
                      <label>Şifre:</label><br>
                      <input type="password" name="password"
                             required minlength="8"
                             pattern="^(?=.*[A-Za-z])(?=.*\\d).{8,}$"
                             title="Şifre en az 8 karakter, 1 harf ve 1 rakam içermeli" /><br><br>
                
                      <button type="submit">Gönder</button>
                    </form>
                  </body>
                </html>
                
                """;
    }

    @PostMapping("/set-password")
    @ResponseBody
    public String handleSetPasswordForm(@RequestParam String token,
                                        @RequestParam String password) {
        companyPasswordService.setPassword(token, password);
        return """
                    <!doctype html>
                    <html lang="tr">
                      <head>
                        <meta charset="UTF-8">
                        <title>Başarılı</title>
                      </head>
                      <body>
                        <h2>Şifre başarıyla oluşturuldu ✅</h2>
                        <p>Artık hesabınıza giriş yapabilirsiniz.</p>
                      </body>
                    </html>
                """;

    }


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApi(ApiException ex) {
        String html = """
                <!doctype html>
                <html lang="tr">
                  <head><meta charset="UTF-8"><title>Hata</title></head>
                  <body>
                    <h2>İşlem Başarısız ❌</h2>
                    <p>%s</p>
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
                  <head><meta charset="UTF-8"><title>Hata</title></head>
                  <body>
                    <h2>İşlem Başarısız ❌</h2>
                    <p>%s</p>
                  </body>
                </html>
                """.formatted(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(html);
    }
}
