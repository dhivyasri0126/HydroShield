package com.hydroshield.repository;

import com.hydroshield.entity.HeavyMetalReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HeavyMetalReadingRepository extends JpaRepository<HeavyMetalReading, Long> {
    List<HeavyMetalReading> findByLocationIdOrderByTestDateDesc(Long locationId);
    Optional<HeavyMetalReading> findFirstByLocationIdOrderByTestDateDesc(Long locationId);
}
