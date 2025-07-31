package com.example.placeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Define a CORS filter with highest precedence to allow cross-origin requests
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow cookies and credentials (e.g., Authorization headers)
        config.setAllowCredentials(true);

        // Allow requests from localhost:8080 (typically the frontend during development)
        config.setAllowedOriginPatterns(List.of("http://localhost:8080"));

        // Allow common HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers (e.g., Content-Type, Authorization, etc.)
        config.setAllowedHeaders(List.of("*"));

        // Set the max age for pre-flight requests (in seconds)
        config.setMaxAge(3600L); // 1 hour

        // Register the CORS configuration to apply to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Return the CORS web filter to apply the rules
        return new CorsWebFilter(source);
    }
}
