package com.myhr.myhr.application.security;

import com.myhr.myhr.infrastructure.repository.UserAccountJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserAccountJpaRepository users;



    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {

                Jws<Claims> jws = Jwts.parserBuilder()
                        .build()
                        .parseClaimsJws(token);

                Claims claims = jws.getBody();
                String email = claims.getSubject();
                String role  = (String) claims.get("role");

                var u = users.findByEmail(email);
                if (u.isPresent() && u.get().isActive()) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                }
            } catch (Exception ignored) {

            }
        }
        chain.doFilter(req, res);
    }
}
