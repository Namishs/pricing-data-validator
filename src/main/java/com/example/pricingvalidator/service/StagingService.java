package com.example.pricingvalidator.service;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.model.StagingRecord;
import com.example.pricingvalidator.repo.StagingRecordRepository;
import com.example.pricingvalidator.repo.PricingRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StagingService {

    private final StagingRecordRepository stagingRepo;
    private final PricingRecordRepository pricingRepo; // used by moveToPricing (optional)

    public StagingRecord save(StagingRecord r) {
        log.debug("Saving staging record instrumentGuid={}", r.getInstrumentGuid());
        return stagingRepo.save(r);
    }

    public List<StagingRecord> findAll() {
        log.debug("Listing all staging records");
        return stagingRepo.findAll();
    }

    public Optional<StagingRecord> findById(Long id) {
        log.debug("Finding staging record id={}", id);
        return stagingRepo.findById(id);
    }

    public StagingRecord update(Long id, StagingRecord patch) {
        log.info("Updating staging record id={}", id);
        return stagingRepo.findById(id).map(existing -> {
            if (patch.getInstrumentGuid() != null) existing.setInstrumentGuid(patch.getInstrumentGuid());
            if (patch.getTradeDate() != null) existing.setTradeDate(patch.getTradeDate());
            if (patch.getPrice() != null) existing.setPrice(patch.getPrice());
            if (patch.getExchange() != null) existing.setExchange(patch.getExchange());
            if (patch.getProductType() != null) existing.setProductType(patch.getProductType());
            if (patch.getErrors() != null) existing.setErrors(patch.getErrors());
            StagingRecord saved = stagingRepo.save(existing);
            log.info("Updated staging record id={} instrumentGuid={}", saved.getId(), saved.getInstrumentGuid());
            return saved;
        }).orElseThrow(() -> {
            log.warn("Tried to update non-existing staging id={}", id);
            return new RuntimeException("staging record not found");
        });
    }

    public void delete(Long id) {
        log.info("Deleting staging record id={}", id);
        stagingRepo.deleteById(id);
    }

    public StagingRecord saveInvalidRecord(PricingRecord r, List<String> errors) {
        log.debug("Saving invalid record to staging: instrumentGuid={} errors={}", r.getInstrumentGuid(), errors);
        StagingRecord s = new StagingRecord();
        s.setInstrumentGuid(r.getInstrumentGuid());
        s.setTradeDate(r.getTradeDate());
        s.setPrice(r.getPrice());
        s.setExchange(r.getExchange());
        s.setProductType(r.getProductType());
        if (errors != null && !errors.isEmpty()) {
            s.setErrors(String.join(", ", errors));
        } else {
            s.setErrors("");
        }
        StagingRecord saved = stagingRepo.save(s);
        log.info("Saved to staging id={} instrumentGuid={}", saved.getId(), saved.getInstrumentGuid());
        return saved;
    }

    @Transactional
    public void moveToPricingAndDeleteStaging(StagingRecord staging) {
        log.info("Moving staging id={} instrumentGuid={} to pricing", staging.getId(), staging.getInstrumentGuid());
        var pr = new com.example.pricingvalidator.model.PricingRecord();
        pr.setInstrumentGuid(staging.getInstrumentGuid());
        pr.setTradeDate(staging.getTradeDate());
        pr.setPrice(staging.getPrice());
        pr.setExchange(staging.getExchange());
        pr.setProductType(staging.getProductType());

        pricingRepo.save(pr);
        stagingRepo.deleteById(staging.getId());
        log.info("Moved staging id={} to pricing id (new) instrumentGuid={}", staging.getId(), staging.getInstrumentGuid());
    }
}
