package com.example.userservice.config;

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

    // Define the CORS filter bean with the highest priority
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Allow requests from this frontend origin (adjust as needed)
        config.setAllowedOrigins(List.of("http://localhost:80", "https://maps.cloudbase.website", "http://localhost:3000")); // Allow all origins

        // Allow these HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers in the request
        config.setAllowedHeaders(List.of("*"));

        // Cache the CORS preflight response for 3600 seconds (1 hour)
        config.setMaxAge(3600L);

        // Register the CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Return the configured CORS filter
        return new CorsWebFilter(source);
    }
}
