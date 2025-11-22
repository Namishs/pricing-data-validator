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

    
    @Column(name = "errors", length = 1000)
    private String errors;
}
