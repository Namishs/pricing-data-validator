package com.example.pricingvalidator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "pricing_records",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"instrument_guid", "trade_date", "exchange", "price"}
        )
)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String instrumentGuid;

    private LocalDate tradeDate;

    // keep price as string to capture INVALID or missing values
    private String price;

    private String exchange;

    private String productType;
}
