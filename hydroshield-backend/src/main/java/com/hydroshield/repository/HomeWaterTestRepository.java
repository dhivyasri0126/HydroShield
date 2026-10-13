package com.hydroshield.repository;

import com.hydroshield.entity.HomeWaterTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomeWaterTestRepository extends JpaRepository<HomeWaterTest, Long> {
    List<HomeWaterTest> findByUserIdOrderByTestDateDesc(Long userId);
}
