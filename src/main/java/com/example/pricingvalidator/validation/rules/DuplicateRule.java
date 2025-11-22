package com.example.pricingvalidator.validation.rules;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.repo.PricingRecordRepository;
import com.example.pricingvalidator.validation.ValidationResult;
import com.example.pricingvalidator.validation.ValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DuplicateRule implements ValidationRule {

    private final PricingRecordRepository repo;

    @Override
    public String ruleKey() {
        return "duplicate-check";
    }

    @Override
    public ValidationResult validate(PricingRecord r) {
        ValidationResult res = new ValidationResult();
        if (r == null) return res;

        // Be defensive: if tradeDate is a LocalDate field in entity, use that
        LocalDate td = r.getTradeDate();

        // repository method should be defined. Example signature:
        // boolean existsByInstrumentGuidAndTradeDateAndExchangeAndPrice(String instrumentGuid, LocalDate tradeDate, String exchange, String price);
        try {
            boolean exists = repo.existsByInstrumentGuidAndTradeDateAndExchangeAndPrice(
                    r.getInstrumentGuid(), td, r.getExchange(), r.getPrice()
            );

            if (exists) {
                res.addError("duplicate");
            }
        } catch (Exception ex) {
            // if repository method differs, we fail safe (do not mark as valid)
            // but avoid stopping the whole pipeline
            res.addError("duplicate-check-failed");
        }

        return res;
    }
}
