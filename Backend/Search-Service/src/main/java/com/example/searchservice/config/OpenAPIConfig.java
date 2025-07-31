package com.example.searchservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    // This bean defines the OpenAPI documentation configuration
    @Bean
    public OpenAPI searchServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search Service API") // Updated title to match the service
                        .description("This is the REST API for Search Service") // Description of the API
                        .version("v1.0.0") // API version
                        .license(new License().name("Apache 2.0"))) // License info
                .externalDocs(new ExternalDocumentation()
                        .description("Search Service Wiki Documentation") // External documentation info
                        .url("https://search-service-docs.com/docs")); // Replace with your real URL
    }
}
