package com.example.pricingvalidator.validation;

import com.example.pricingvalidator.model.PricingRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidatorService {

    private final ValidatorConfig config;
    private final List<ValidationRule> allRules;

    public ValidationResult validate(PricingRecord record) {
        ValidationResult result = new ValidationResult();

        Map<String, ValidationRule> ruleMap = new HashMap<>();
        for (ValidationRule rule : allRules) {
            ruleMap.put(rule.ruleKey(), rule);
        }

        for (String ruleKey : config.getRules()) {
            ValidationRule rule = ruleMap.get(ruleKey);
            if (rule != null) {
                ValidationResult r = rule.validate(record);
                if (!r.isValid()) {
                    result.getErrors().addAll(r.getErrors());
                    result.setValid(false);
                }
            }
        }

        return result;
    }
}
