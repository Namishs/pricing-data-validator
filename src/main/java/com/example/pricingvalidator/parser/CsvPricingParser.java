package com.example.pricingvalidator.parser;

import com.example.pricingvalidator.model.PricingRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
public class CsvPricingParser {

    // change this if your CSV uses a different date format
    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE; // "yyyy-MM-dd"

    /**
     * Parse CSV into list of PricingRecord.
     * - safely handles header row (first row containing 'instrument' text)
     * - trims values, maps missing/blank to null
     * - parses trade date string into LocalDate
     */
    public List<PricingRecord> parse(InputStream in) throws Exception {
        log.info("Starting CSV parse");
        List<PricingRecord> out = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {
            String[] line;
            int row = 0;
            while ((line = reader.readNext()) != null) {
                row++;

                // skip entirely-empty rows
                boolean allEmpty = true;
                for (String c : line) {
                    if (c != null && !c.trim().isEmpty()) {
                        allEmpty = false;
                        break;
                    }
                }
                if (allEmpty) {
                    log.debug("Skipping empty CSV row {}", row);
                    continue;
                }

                // heuristic: skip header if first row contains a header label
                if (row == 1) {
                    String firstCell = line.length > 0 && line[0] != null ? line[0].toLowerCase(Locale.ROOT) : "";
                    if (firstCell.contains("instrument") || firstCell.contains("instrument_guid") || firstCell.contains("instrument guid")) {
                        log.debug("Skipping header row");
                        continue;
                    }
                }

                try {
                    PricingRecord r = new PricingRecord();

                    // instrumentGuid
                    String instrumentGuid = getCell(line, 0);
                    r.setInstrumentGuid(emptyToNull(instrumentGuid));

                    // tradeDate -> parse to LocalDate (model expects LocalDate)
                    String tradeDateRaw = getCell(line, 1);
                    if (tradeDateRaw == null || tradeDateRaw.isBlank()) {
                        r.setTradeDate(null);
                    } else {
                        try {
                            LocalDate ld = LocalDate.parse(tradeDateRaw.trim(), DATE_FMT);
                            r.setTradeDate(ld);
                        } catch (DateTimeParseException dtpe) {
                            // if parsing fails, set null and let validator handle missing/invalid date
                            log.warn("Failed to parse tradeDate on row {} value='{}' — setting to null", row, tradeDateRaw);
                            r.setTradeDate(null);
                        }
                    }

                    // price (store as String in your model if model uses String)
                    String price = getCell(line, 2);
                    r.setPrice(emptyToNull(price));

                    // exchange
                    String exchange = getCell(line, 3);
                    r.setExchange(emptyToNull(exchange));

                    // productType
                    String productType = getCell(line, 4);
                    r.setProductType(emptyToNull(productType));

                    out.add(r);
                    log.debug("Parsed row {} instrumentGuid={} tradeDate={}", row, r.getInstrumentGuid(), r.getTradeDate());
                } catch (Exception e) {
                    log.warn("Failed to parse CSV row {}: {}", row, e.getMessage(), e);
                }
            }
        }

        log.info("Completed CSV parse, {} records parsed", out.size());
        return out;
    }

    // helper to safely get cell value (returns null if index out of bounds)
    private String getCell(String[] line, int idx) {
        if (line == null || idx < 0 || idx >= line.length) return null;
        return line[idx] != null ? line[idx].trim() : null;
    }

    // convert empty string to null for consistent model mapping
    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
