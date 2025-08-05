package com.example.searchservice.config;

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
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection (useful for stateless APIs)
                .cors(withDefaults()) // Enable default CORS configuration
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (pre-flight requests for CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Publicly allow access to Swagger UI and search/auth endpoints
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/auth/**",
                                "/api/search/**"
                        ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Enable basic HTTP authentication
                .httpBasic(withDefaults())
                .build();
    }
}
