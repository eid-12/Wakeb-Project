package com.example.apigateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Data // Lombok: generates getters/setters
@Component // Marks this class as a Spring-managed component (bean)
public class JwtService {

    @Value("${jwt.secret}") // Injects the JWT secret from application properties
    private String secret;

    private SecretKey key; // Will be used to verify JWT signature

    // Called after the bean is constructed — initializes the key
    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Extracts the username (subject) from the JWT token
    public String extractUsername(String token) {
        return parser().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Returns a configured JWT parser that uses the secret key
    private JwtParser parser() {
        return Jwts.parser()
                .verifyWith(key) // Verifies the token using the key
                .build();
    }
}
