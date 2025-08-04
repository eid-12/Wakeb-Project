package com.example.apigateway.routes.config;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@AllArgsConstructor // Lombok: generates constructor for required fields
@Configuration // Marks this class as a Spring configuration
public class GatewaySecurityConfig {

    private final JwtRelayFilter jwtRelayFilter; // Custom filter to process and forward JWTs

    // Define the main Spring Security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Use stateless session policy (no session storage)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF protection (since this is a stateless API)
                .csrf(csrf -> csrf.disable())

                // Enable CORS using a custom configuration
                .cors(withDefaults())

                // Add JWT relay filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRelayFilter, UsernamePasswordAuthenticationFilter.class)

                // Authorization rules for incoming requests
                .authorizeHttpRequests(auth -> auth
                        // Allow all OPTIONS requests (used for preflight in CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Allow public access to authentication endpoints
                        .requestMatchers("/auth/**").permitAll()

                        // Require authentication for any /api/place/** endpoint
                        .requestMatchers("/api/place/** " ).authenticated()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // Define global CORS settings for the gateway
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Allow cookies and authorization headers

        // Allow requests from any localhost port (e.g., Vue/React running on :5173, :8080, etc.)
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://192.168.110.211:8080"));

        config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        config.setAllowedMethods(Arrays.asList("GET", "PATCH", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all routes

        return source;
    }

}
