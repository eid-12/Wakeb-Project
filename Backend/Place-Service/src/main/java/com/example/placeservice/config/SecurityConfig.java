package com.example.placeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                // تعطيل CSRF للسماح بحذف الصور
                .csrf(csrf -> csrf.disable()) 
                
                // تفعيل الـ CORS باستخدام الإعدادات التي وضعناها في CorsConfig
                .cors(withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/api/auth/**", "/api/place/**", "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }
}
