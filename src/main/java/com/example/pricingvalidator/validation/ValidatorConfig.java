package com.example.pricingvalidator.validation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pricing.validator")
public class ValidatorConfig {
    private List<String> rules;
    private List<String> allowedExchanges; // bound from application.yml
}
