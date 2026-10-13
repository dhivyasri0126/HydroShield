package com.hydroshield.repository;

import com.hydroshield.entity.HmpiRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HmpiRecordRepository extends JpaRepository<HmpiRecord, Long> {
    List<HmpiRecord> findByLocationIdOrderByCalculatedAtDesc(Long locationId);
    Optional<HmpiRecord> findFirstByLocationIdOrderByCalculatedAtDesc(Long locationId);
}
