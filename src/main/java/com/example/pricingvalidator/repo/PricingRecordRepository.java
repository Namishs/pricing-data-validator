package com.example.pricingvalidator.repo;

import com.example.pricingvalidator.model.PricingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.pricingvalidator.model.PricingRecord;

import java.time.LocalDate;

@Repository
public interface PricingRecordRepository extends JpaRepository<PricingRecord, Long> {

    boolean existsByInstrumentGuidAndTradeDateAndExchangeAndPrice(
            String instrumentGuid,
            LocalDate tradeDate,
            String exchange,
            String price
    );
}
