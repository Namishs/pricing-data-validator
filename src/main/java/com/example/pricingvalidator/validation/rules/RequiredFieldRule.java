package com.example.pricingvalidator.validation.rules;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.validation.ValidationResult;
import com.example.pricingvalidator.validation.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class RequiredFieldRule implements ValidationRule {

    @Override
    public String ruleKey() {
        return "required-fields";
    }

    @Override
    public ValidationResult validate(PricingRecord r) {
        ValidationResult res = new ValidationResult();
        if (r == null) {
            res.addError("record is null");
            return res;
        }

        if (r.getInstrumentGuid() == null || r.getInstrumentGuid().isBlank()) {
            res.addError("instrument_guid missing");
        }
        if (r.getTradeDate() == null) {
            res.addError("trade_date missing or invalid format");
        }
        if (r.getExchange() == null || r.getExchange().isBlank()) {
            res.addError("exchange missing");
        }
        if (r.getPrice() == null || r.getPrice().isBlank()) {
            res.addError("price missing");
        }

        return res;
    }
}
