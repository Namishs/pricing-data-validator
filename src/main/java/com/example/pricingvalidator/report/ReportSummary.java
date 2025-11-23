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

    
    private long totalPricingRecords;   
    private long totalStagingRecords;   
    private long totalDistinctIngested; 

    
    private Map<String, Long> errorsCount;         
    private Map<String, Long> byExchange;         
    private Map<String, Long> byProductType;      
}

