package com.example.authservice.security;

import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignBasicAuthConfig {

    @Bean
    public RequestInterceptor basicAuth() {
        return new BasicAuthRequestInterceptor("internal", "secret");
    }
}
