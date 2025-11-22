package com.example.pricingvalidator.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSummary {
    private Instant generatedAt;

    // totals
    private long totalPricingRecords;   // valid (main table)
    private long totalStagingRecords;   // invalid (staging table)
    private long totalDistinctIngested; // pricing + staging total rows we currently have

    // breakdowns
    private Map<String, Long> errorsCount;         // error -> count (from staging.errors)
    private Map<String, Long> byExchange;         // exchange -> count (pricing)
    private Map<String, Long> byProductType;      // productType -> count (pricing)
}

