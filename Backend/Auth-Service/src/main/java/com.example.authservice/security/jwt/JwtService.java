package com.example.authservice.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    private SecretKey key;

    // Load secret key from application properties
    @Value("${jwt.secret}")
    private String secret;

    // Load token expiration time in milliseconds (default: 1 hour)
    @Value("${jwt.expiration:3600000}")
    private long expiration;

    // Initialize the secret key after bean creation
    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token using user details
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername()) // Set username as the subject
                .claim("userId", extractUserId(user)) // Optional claim: user ID
                .issuedAt(new Date()) // Token creation time
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Token expiration
                .signWith(key, Jwts.SIG.HS256) // Sign using HS256 algorithm
                .compact();
    }

    // Extract username (subject) from the token
    public String extractUsername(String token) {
        return parser().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate token by comparing username and checking expiration
    public boolean isTokenValid(String token, UserDetails user) {
        return user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        Date exp = parser().parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return exp.before(new Date());
    }

    // Build and return the JWT parser with the secret key
    private JwtParser parser() {
        return Jwts.parser()
                .verifyWith(key)
                .build();
    }

    // Try to extract user ID using reflection (calls getId() if exists)
    private Object extractUserId(UserDetails user) {
        try {
            return user.getClass().getMethod("getId").invoke(user);
        } catch (Exception e) {
            return null; // Return null if method doesn't exist or fails
        }
    }
}
