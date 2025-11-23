package com.example.pricingvalidator.service;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.parser.CsvPricingParser;
import com.example.pricingvalidator.repo.PricingRecordRepository;
import com.example.pricingvalidator.validation.ValidationResult;
import com.example.pricingvalidator.validation.ValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.pricingvalidator.model.StagingRecord;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PricingRecordRepository repo;
    private final CsvPricingParser parser;
    private final ValidatorService validator;
    private final StagingService stagingService;

    public List<PricingRecord> getAll() {
        log.debug("Fetching all pricing records from DB");
        return repo.findAll();
    }

    
    public Map<String, Object> ingestCsv(InputStream csvStream) throws Exception {
        log.info("Starting CSV ingest");
        List<PricingRecord> rows = parser.parse(csvStream);
        log.info("Parsed {} rows from CSV", rows.size());

        int valid = 0;
        int invalid = 0;
        List<Map<String, Object>> invalidSamples = new ArrayList<>();

        for (PricingRecord r : rows) {
            log.debug("Validating record: instrumentGuid={} tradeDate={}", r.getInstrumentGuid(), r.getTradeDate());
            ValidationResult vr = validator.validate(r);

            if (!vr.isValid()) {
                invalid++;
                invalidSamples.add(Map.of("record", r, "errors", vr.getErrors()));
                log.warn("Record invalid: instrumentGuid={} errors={}", r.getInstrumentGuid(), vr.getErrors());

                
                try {
                    StagingRecord saved = stagingService.saveInvalidRecord(r, vr.getErrors());
                    log.info("Saved invalid record to staging id={} instrumentGuid={}", saved.getId(), saved.getInstrumentGuid());
                } catch (Exception e) {
                    log.error("Failed to save invalid record to staging for instrumentGuid={}: {}", r.getInstrumentGuid(), e.getMessage(), e);
                }
                continue;
            }

           
            try {
                repo.save(r);
                valid++;
                log.info("Saved valid pricing record: instrumentGuid={} tradeDate={}", r.getInstrumentGuid(), r.getTradeDate());
            } catch (Exception e) {
                log.error("Failed to save pricing record instrumentGuid={}: {}", r.getInstrumentGuid(), e.getMessage(), e);
                
                invalid++;
                invalidSamples.add(Map.of("record", r, "errors", List.of("persist-failed")));
            }
        }

        log.info("Completed CSV ingest: parsed={}, valid={}, invalid={}", rows.size(), valid, invalid);
        return Map.of(
                "parsed", rows.size(),
                "valid", valid,
                "invalid", invalid,
                "invalidSamples", invalidSamples
        );
    }
}
