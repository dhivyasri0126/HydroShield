package com.hydroshield.repository;

import com.hydroshield.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
    List<Device> findByLocationId(Long locationId);
    List<Device> findByStatus(String status);
    long countByStatus(String status);
}
