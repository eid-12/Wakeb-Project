package com.example.savedplaceservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    // Configure Swagger OpenAPI documentation for the Saved Place Service
    @Bean
    public OpenAPI savedPlaceServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Saved Place Service API") // Title of the API
                        .description("REST API for managing user's saved places") // Short description
                        .version("v1.0.0") // Version of the API
                        .license(new License().name("Apache 2.0"))) // License info
                .externalDocs(new ExternalDocumentation()
                        .description("Saved Place Service Documentation") // Link description
                        .url("https://saved-place-service.docs.example.com")); // Replace with real docs URL
    }
}
