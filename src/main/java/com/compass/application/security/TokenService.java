package com.compass.application.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.compass.application.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private int expiration;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("auth-api")
                .withSubject(user.getEmail())
                .withIssuedAt(Instant.now())
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
    }

    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getSubject();

    }

    public String recoverToken(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            return null;
        }
        return authToken.replace("Bearer", "").trim();
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
