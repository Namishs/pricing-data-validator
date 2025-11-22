package com.example.pricingvalidator.validation.rules;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.validation.ValidationResult;
import com.example.pricingvalidator.validation.ValidationRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceFormatRule implements ValidationRule {

    @Override
    public String ruleKey() {
        return "price-format";
    }

    @Override
    public ValidationResult validate(PricingRecord r) {
        ValidationResult res = new ValidationResult();
        if (r == null || r.getPrice() == null || r.getPrice().isBlank()) {
            // required-fields should catch this, but be defensive
            return res;
        }

        String p = r.getPrice().trim();
        try {
            // accept numeric prices like 123.45 or 123
            new BigDecimal(p);
        } catch (Exception ex) {
            res.addError("price not a valid number");
        }
        return res;
    }
}
