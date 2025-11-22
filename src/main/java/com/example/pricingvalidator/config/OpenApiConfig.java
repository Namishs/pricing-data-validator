package com.example.pricingvalidator.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pricingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pricing Validator API")
                        .version("0.1.0")
                        .description("CSV pricing validation utility — upload a CSV, get validation summary, fix invalid rows via staging APIs.")
                        .contact(new Contact().name("Your Team").email("team@example.com"))
                );
    }
}
