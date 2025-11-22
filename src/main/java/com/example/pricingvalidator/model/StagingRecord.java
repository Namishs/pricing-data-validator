package com.example.pricingvalidator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "staging_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StagingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // keep same fields as PricingRecord for easy copy
    @Column(name = "instrument_guid")
    private String instrumentGuid;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "price")
    private String price;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "product_type")
    private String productType;

    // Store the validation error summary (comma-separated or JSON string)
    @Column(name = "errors", length = 1000)
    private String errors;
}
