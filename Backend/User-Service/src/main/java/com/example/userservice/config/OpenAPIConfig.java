package com.example.userservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    // Define the OpenAPI configuration bean for Swagger UI
    @Bean
    public OpenAPI userServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API") // Title shown in Swagger UI
                        .description("This is the REST API documentation for the User Service") // Short API description
                        .version("v0.0.1") // Version number
                        .license(new License().name("Apache 2.0"))) // License info
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the User Service Wiki Documentation") // Additional external docs
                        .url("https://user-service-docs.example.com")); // URL of external docs (replace with real one)
    }
}
