package com.example.pricingvalidator.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    
    @GetMapping
    public ResponseEntity<ReportSummary> getReport() {
        ReportSummary summary = reportService.generateSummary();
        return ResponseEntity.ok(summary);
    }

    
    @GetMapping(value = "/staging.csv", produces = "text/csv")
    public ResponseEntity<String> downloadStagingCsv() {
        String csv = reportService.stagingInvalidCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"staging-invalid.csv\"");
        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    
    @GetMapping(params = "format=csv", produces = "text/csv")
    public ResponseEntity<String> downloadStagingCsvParam() {
        String csv = reportService.stagingInvalidCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"staging-invalid.csv\"");
        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
