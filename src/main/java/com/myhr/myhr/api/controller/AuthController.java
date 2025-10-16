package com.myhr.myhr.api.controller;

import com.myhr.myhr.application.security.JwtUtil;
import com.myhr.myhr.api.dto.LoginRequest;
import com.myhr.myhr.domain.Role;
import com.myhr.myhr.infrastructure.repository.UserAccountJpaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAccountJpaRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        String email = request.email();
        String password = request.password();

        var u = users.findByEmail(email).orElse(null);


        if (u == null || !u.isActive() || !encoder.matches(password, u.getPasswordHash())) {

            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }

        String token = jwt.generate(email, Map.of("role", u.getRole().name()));


        int expiresInSeconds = 60 * Integer.parseInt(System.getProperty("JWT_EXPIRES_MIN","30"));

        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", expiresInSeconds
        ));
    }
}