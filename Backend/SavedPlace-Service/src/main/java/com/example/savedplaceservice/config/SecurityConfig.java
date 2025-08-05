package com.example.savedplaceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (not needed for APIs using tokens)
                .csrf(csrf -> csrf.disable())

                // Enable CORS to allow requests from the frontend (e.g., localhost:8080)
                .cors(withDefaults())

                // Define public and protected endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (used for CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public access to Swagger docs and saved place endpoints
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/auth/**",
                                "/api/saved/**"
                        ).permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Enable basic HTTP authentication (used here mainly for Swagger testing)
                .httpBasic(withDefaults())

                // Build the security configuration
                .build();
    }
}
