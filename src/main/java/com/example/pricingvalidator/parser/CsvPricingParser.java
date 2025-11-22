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

    
    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE; // "yyyy-MM-dd"

    
    public List<PricingRecord> parse(InputStream in) throws Exception {
        log.info("Starting CSV parse");
        List<PricingRecord> out = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {
            String[] line;
            int row = 0;
            while ((line = reader.readNext()) != null) {
                row++;

                
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

               
                if (row == 1) {
                    String firstCell = line.length > 0 && line[0] != null ? line[0].toLowerCase(Locale.ROOT) : "";
                    if (firstCell.contains("instrument") || firstCell.contains("instrument_guid") || firstCell.contains("instrument guid")) {
                        log.debug("Skipping header row");
                        continue;
                    }
                }

                try {
                    PricingRecord r = new PricingRecord();

                    
                    String instrumentGuid = getCell(line, 0);
                    r.setInstrumentGuid(emptyToNull(instrumentGuid));

                    
                    String tradeDateRaw = getCell(line, 1);
                    if (tradeDateRaw == null || tradeDateRaw.isBlank()) {
                        r.setTradeDate(null);
                    } else {
                        try {
                            LocalDate ld = LocalDate.parse(tradeDateRaw.trim(), DATE_FMT);
                            r.setTradeDate(ld);
                        } catch (DateTimeParseException dtpe) {
                            
                            log.warn("Failed to parse tradeDate on row {} value='{}' — setting to null", row, tradeDateRaw);
                            r.setTradeDate(null);
                        }
                    }

                    
                    String price = getCell(line, 2);
                    r.setPrice(emptyToNull(price));

                    
                    String exchange = getCell(line, 3);
                    r.setExchange(emptyToNull(exchange));

                    
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

    
    private String getCell(String[] line, int idx) {
        if (line == null || idx < 0 || idx >= line.length) return null;
        return line[idx] != null ? line[idx].trim() : null;
    }

    
    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
