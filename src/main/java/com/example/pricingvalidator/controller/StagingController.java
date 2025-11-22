package com.example.pricingvalidator.controller;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.model.StagingRecord;
import com.example.pricingvalidator.service.StagingService;
import com.example.pricingvalidator.validation.ValidatorService;
import com.example.pricingvalidator.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing/staging")
@RequiredArgsConstructor
@Slf4j
public class StagingController {

    private final StagingService stagingService;
    private final ValidatorService validatorService;

    // GET all staging rows
    @GetMapping
    public ResponseEntity<List<StagingRecord>> list() {
        log.debug("GET /api/pricing/staging");
        return ResponseEntity.ok(stagingService.findAll());
    }

    // GET one staging row by ID
    @GetMapping("/{id}")
    public ResponseEntity<StagingRecord> get(@PathVariable Long id) {
        log.debug("GET /api/pricing/staging/{}", id);
        return stagingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH update a staging record (partial update)
    @PatchMapping("/{id}")
    public ResponseEntity<StagingRecord> patch(
            @PathVariable Long id,
            @RequestBody StagingRecord update
    ) {
        log.info("PATCH /api/pricing/staging/{} requested", id);
        return stagingService.findById(id).map(existing -> {

            // apply only non-null fields (simple merge)
            if (update.getInstrumentGuid() != null) existing.setInstrumentGuid(update.getInstrumentGuid());
            if (update.getTradeDate() != null) existing.setTradeDate(update.getTradeDate());
            if (update.getPrice() != null) existing.setPrice(update.getPrice());
            if (update.getExchange() != null) existing.setExchange(update.getExchange());
            if (update.getProductType() != null) existing.setProductType(update.getProductType());

            // allow clearing or setting new error state
            if (update.getErrors() != null) existing.setErrors(update.getErrors());

            // call service using the correct signature
            StagingRecord saved = stagingService.update(id, existing);
            log.info("Patched staging record id={}", saved.getId());
            return ResponseEntity.ok(saved);

        }).orElse(ResponseEntity.notFound().build());
    }

    // Revalidate and move to main pricing table
    @PostMapping("/{id}/revalidate")
    public ResponseEntity<?> revalidateAndMove(@PathVariable Long id) {

        log.info("POST /api/pricing/staging/{}/revalidate called", id);

        return stagingService.findById(id).map(existing -> {

            // Map StagingRecord -> PricingRecord manually for validation
            PricingRecord candidate = new PricingRecord();
            candidate.setInstrumentGuid(existing.getInstrumentGuid());
            candidate.setTradeDate(existing.getTradeDate());
            candidate.setPrice(existing.getPrice());
            candidate.setExchange(existing.getExchange());
            candidate.setProductType(existing.getProductType());

            // Validate candidate using existing validator pipeline
            ValidationResult validationResult = validatorService.validate(candidate);

            if (!validationResult.isValid()) {
                log.warn("Revalidation failed for staging id={} errors={}", id, validationResult.getErrors());
                // return the errors back to caller so they can adjust via PATCH
                return ResponseEntity.badRequest().body(validationResult.getErrors());
            }

            // If valid, move to pricing and delete staging
            try {
                stagingService.moveToPricingAndDeleteStaging(existing);
                log.info("Moved staging id={} instrumentGuid={} to pricing", id, existing.getInstrumentGuid());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Failed to move staging id={} to pricing: {}", id, e.getMessage(), e);
                return ResponseEntity.internalServerError().body("Failed to move to pricing: " + e.getMessage());
            }

        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE a staging record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/pricing/staging/{} called", id);
        stagingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
