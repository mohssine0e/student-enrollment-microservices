package com.example.courseservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Service API")
                        .version("v1")
                        .description("API for managing course catalog data"));
    }
}
