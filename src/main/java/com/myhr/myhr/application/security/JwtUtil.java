package com.myhr.myhr.application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final long expiresMillis;

    public Key getKey() { return this.key; }

    public JwtUtil(
            @Value("${app.jwt.secret:}") String secretBase64,
            @Value("${app.jwt.expires-min:30}") long expiresMin
    ) {
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalStateException("app.jwt.secret is not configured (Base64-encoded, >=32 bytes required).");
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("app.jwt.secret must be Base64-encoded.", e);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret (decoded) must be at least 32 bytes for HS256.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiresMillis = Duration.ofMinutes(expiresMin).toMillis();
    }

    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiresMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return parse(token).getBody().getSubject();
    }

    public Claims getAllClaims(String token) {
        return parse(token).getBody();
    }


    public String stripBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        return t.startsWith("Bearer ") ? t.substring(7) : t;
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(stripBearer(token));
    }
}
