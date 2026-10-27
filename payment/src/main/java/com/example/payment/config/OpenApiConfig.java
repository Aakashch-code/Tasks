package com.example.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Customer Payment API")
                                .version("1.0")
                                .description(
                                        "Spring Boot application for managing " +
                                                "customer payments and sending automated " +
                                                "payment reminders."
                                )
                );
    }
}