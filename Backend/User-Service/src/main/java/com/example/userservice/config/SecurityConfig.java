package com.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    // Defines security rules for HTTP requests
    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection (optional for APIs)
                .cors(withDefaults()) // Enable CORS
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (for CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/api/user/**" , "/api/auth/**").permitAll()
                        // Allow public access to Swagger docs and selected APIs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api/auth/**",
                                "/error",
                                "/api/user/**"
                        ).permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .build(); // Build the security filter chain
    }

    // Create an in-memory user for internal service communication
    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("internal")
                        .password("{noop}secret") // {noop} means no encoding
                        .roles("SERVICE")
                        .build()
        );
    }

    // Define the password encoder (used if you switch to real encoded passwords)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
