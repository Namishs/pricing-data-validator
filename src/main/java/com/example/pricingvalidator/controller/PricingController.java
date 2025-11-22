package com.example.pricingvalidator.controller;

import com.example.pricingvalidator.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
public class PricingController {

    private final PricingService pricingService;

    @Operation(
            summary = "Upload CSV file and ingest pricing data",
            description = "Upload a CSV file using multipart/form-data."
    )
    @PostMapping(
            value = "/ingest",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> ingest(
            @Parameter(
                    description = "CSV file to upload",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("Ingest called with missing file");
                return ResponseEntity.badRequest().body("File is missing");
            }

            log.info("Ingest called: filename={} size={}", file.getOriginalFilename(), file.getSize());
            var result = pricingService.ingestCsv(file.getInputStream());

            log.info("Ingest completed: parsed={} valid={} invalid={}",
                    result.get("parsed"), result.get("valid"), result.get("invalid"));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to ingest file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to ingest file: " + e.getMessage());
        }
    }

    @Operation(summary = "Get all valid pricing records")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        log.debug("GET /api/pricing/all");
        return ResponseEntity.ok(pricingService.getAll());
    }
}
