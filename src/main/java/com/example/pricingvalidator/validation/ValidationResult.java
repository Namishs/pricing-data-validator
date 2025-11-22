package com.example.pricingvalidator.validation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationResult {
    private boolean valid = true;
    private final List<String> errors = new ArrayList<>();

    public void addError(String e) {
        if (e == null) return;
        valid = false;
        errors.add(e);
    }
}
