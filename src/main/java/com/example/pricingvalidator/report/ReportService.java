package com.example.pricingvalidator.report;

import com.example.pricingvalidator.model.PricingRecord;
import com.example.pricingvalidator.model.StagingRecord;
import com.example.pricingvalidator.repo.PricingRecordRepository;
import com.example.pricingvalidator.repo.StagingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PricingRecordRepository pricingRepo;
    private final StagingRecordRepository stagingRepo;

    public ReportSummary generateSummary() {
        List<PricingRecord> pricing = pricingRepo.findAll();
        List<StagingRecord> staging = stagingRepo.findAll();

        long totalPricing = pricing.size();
        long totalStaging = staging.size();
        long totalDistinct = totalPricing + totalStaging;

       
        Map<String, Long> errorsCount = new HashMap<>();
        for (StagingRecord s : staging) {
            String err = s.getErrors();
            if (err == null || err.isBlank()) continue;
            
            String[] parts = err.split(",");
            for (String p : parts) {
                String k = p.trim().toLowerCase(Locale.ROOT);
                if (k.isEmpty()) continue;
                errorsCount.put(k, errorsCount.getOrDefault(k, 0L) + 1L);
            }
        }

        
        Map<String, Long> byExchange = pricing.stream()
                .map(PricingRecord::getExchange)
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(s -> s.isEmpty() ? "UNKNOWN" : s)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        
        Map<String, Long> byProductType = pricing.stream()
                .map(PricingRecord::getProductType)
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(s -> s.isEmpty() ? "UNKNOWN" : s)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        return ReportSummary.builder()
                .generatedAt(Instant.now())
                .totalPricingRecords(totalPricing)
                .totalStagingRecords(totalStaging)
                .totalDistinctIngested(totalDistinct)
                .errorsCount(errorsCount)
                .byExchange(byExchange)
                .byProductType(byProductType)
                .build();
    }

   
    public String stagingInvalidCsv() {
        List<StagingRecord> staging = stagingRepo.findAllByOrderByIdAsc();
        StringBuilder sb = new StringBuilder();
        sb.append("id,instrumentGuid,tradeDate,price,exchange,productType,errors\n");
        for (StagingRecord s : staging) {
            sb.append(s.getId() == null ? "" : s.getId()).append(",");
            sb.append(csvEscape(s.getInstrumentGuid())).append(",");
            sb.append(s.getTradeDate() == null ? "" : s.getTradeDate()).append(",");
            sb.append(csvEscape(s.getPrice())).append(",");
            sb.append(csvEscape(s.getExchange())).append(",");
            sb.append(csvEscape(s.getProductType())).append(",");
            sb.append(csvEscape(s.getErrors())).append("\n");
        }
        return sb.toString();
    }

    private String csvEscape(String v) {
        if (v == null) return "";
        String out = v.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\"") || out.contains("\n")) {
            return "\"" + out + "\"";
        }
        return out;
    }
}
