package com.example.placeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Marks this class as a Spring configuration class
public class SecurityConfig {

    // Defines the security filter chain for this service
    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (suitable for stateless REST APIs)
                .csrf(csrf -> csrf.disable())

                // Enable default CORS settings (allows frontend apps to communicate)
                .cors(Customizer.withDefaults())

                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all preflight (OPTIONS) requests — required for CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Allow unauthenticated access to Swagger docs and specific public endpoints
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/api/auth/**", "/api/place/**" // <-- likely temporary during dev
                        ).permitAll()

                        // Require authentication for any other request
                        .anyRequest().authenticated()
                )

                // Build and return the configured security filter chain
                .build();
    }
}
