package com.hydroshield.repository;

import com.hydroshield.entity.MonitoringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringLocationRepository extends JpaRepository<MonitoringLocation, Long> {
    Optional<MonitoringLocation> findByLocationCode(String locationCode);
    List<MonitoringLocation> findByDistrictIgnoreCase(String district);
    
    @Query("SELECT m FROM MonitoringLocation m WHERE LOWER(m.district) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.area) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MonitoringLocation> searchByLocationQuery(String query);
}
