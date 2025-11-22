package com.example.pricingvalidator.validation;

import com.example.pricingvalidator.model.PricingRecord;

public interface ValidationRule {
    String ruleKey();
    ValidationResult validate(PricingRecord record);
}
