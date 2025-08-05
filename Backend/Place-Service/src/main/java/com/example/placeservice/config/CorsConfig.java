package com.example.placeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Marks this class as a Spring configuration class
public class CorsConfig {

    // Defines a CORS filter with the highest priority
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Allow cookies and authorization headers
        config.setAllowedOriginPatterns(List.of("http://localhost:8080", "http://192.168.110.211:8080","https://maps.up.railway.app")); // Allow requests from frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Allowed HTTP methods
        config.setAllowedHeaders(List.of("*")); // Allow all headers
        config.setMaxAge(3600L); // Cache the preflight response for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS config to all endpoints

        return new CorsWebFilter(source);
    }
}
