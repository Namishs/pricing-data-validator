package com.example.pricingvalidator.validation.rules;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.validation.ValidationResult;
import com.example.pricingvalidator.validation.ValidationRule;
import com.example.pricingvalidator.validation.ValidatorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AllowedExchangeRule implements ValidationRule {

    private final ValidatorConfig config;

    @Override
    public String ruleKey() {
        return "allowed-exchange";
    }

    @Override
    public ValidationResult validate(PricingRecord r) {
        ValidationResult res = new ValidationResult();
        if (r == null || r.getExchange() == null || r.getExchange().isBlank()) {
            // required-fields will flag missing exchange
            return res;
        }

        List<String> allowed = config.getAllowedExchanges();
        if (allowed == null || allowed.isEmpty()) {
            // nothing to check against (considered permissive)
            return res;
        }

        boolean ok = allowed.stream()
                .anyMatch(a -> a.equalsIgnoreCase(r.getExchange().trim()));

        if (!ok) {
            res.addError("exchange not allowed: " + r.getExchange());
        }
        return res;
    }
}
