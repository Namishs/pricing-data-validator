package com.example.pricingvalidator.repo;

import com.example.pricingvalidator.model.StagingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StagingRecordRepository extends JpaRepository<StagingRecord, Long> {
    List<StagingRecord> findAllByOrderByIdAsc();
}
