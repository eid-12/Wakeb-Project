package com.example.placeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // This bean defines a CORS filter to allow requests from the frontend (e.g. localhost:8080)
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Ensures this filter has the highest priority
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Allow cookies and credentials in cross-origin requests

        // Allow frontend running on localhost:8080
        config.setAllowedOriginPatterns(List.of("http://192.168.110.211:8080"));

        // Allow common HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers (like Authorization, Content-Type)
        config.setAllowedHeaders(List.of("*"));

        // Cache CORS config for 1 hour
        config.setMaxAge(3600L);

        // Apply the above config to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
