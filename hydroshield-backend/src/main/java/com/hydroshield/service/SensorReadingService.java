package com.hydroshield.service;

import com.hydroshield.dto.SensorReadingDTO;
import com.hydroshield.entity.Alert;
import com.hydroshield.entity.Device;
import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.entity.SensorReading;
import com.hydroshield.repository.AlertRepository;
import com.hydroshield.repository.DeviceRepository;
import com.hydroshield.repository.MonitoringLocationRepository;
import com.hydroshield.repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SensorReadingService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MonitoringLocationRepository locationRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Transactional
    public SensorReading ingestReading(SensorReadingDTO dto) {
        // 1. Find or auto-register device
        Optional<Device> devOpt = deviceRepository.findByDeviceId(dto.getDeviceId());
        Device device;
        MonitoringLocation location;

        if (dto.getLocationId() != null) {
            location = locationRepository.findById(dto.getLocationId())
                    .orElseGet(() -> locationRepository.findAll().stream().findFirst().orElseThrow());
        } else {
            location = locationRepository.findAll().stream().findFirst().orElseThrow();
        }

        if (devOpt.isPresent()) {
            device = devOpt.get();
            device.setStatus("ONLINE");
            device.setLastSeen(LocalDateTime.now());
            deviceRepository.save(device);
        } else {
            device = Device.builder()
                    .deviceId(dto.getDeviceId())
                    .deviceName("Node " + dto.getDeviceId())
                    .location(location)
                    .sensorConfig("pH + Turbidity + TDS")
                    .status("ONLINE")
                    .lastSeen(LocalDateTime.now())
                    .build();
            deviceRepository.save(device);
        }

        // 2. Build and save sensor reading
        LocalDateTime timestamp = dto.getTimestamp() != null 
            ? LocalDateTime.parse(dto.getTimestamp()) 
            : LocalDateTime.now();

        SensorReading reading = SensorReading.builder()
                .deviceId(dto.getDeviceId())
                .location(location)
                .timestamp(timestamp)
                .ph(dto.getPh())
                .turbidity(dto.getTurbidity())
                .tds(dto.getTds())
                .source("ESP32_SENSOR")
                .build();

        reading = sensorReadingRepository.save(reading);

        // 3. Perform automatic threshold alert check
        checkAlertThresholds(location, dto.getDeviceId(), dto.getPh(), dto.getTurbidity(), dto.getTds());

        return reading;
    }

    private void checkAlertThresholds(MonitoringLocation location, String deviceId, Double ph, Double turbidity, Double tds) {
        if (ph < 6.5 || ph > 8.5) {
            alertRepository.save(Alert.builder()
                    .location(location)
                    .deviceId(deviceId)
                    .alertType("PH_THRESHOLD_BREACH")
                    .severity("MEDIUM")
                    .message(String.format("Device %s recorded abnormal pH of %.2f (Standard range: 6.5 - 8.5)", deviceId, ph))
                    .status("ACTIVE")
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        if (turbidity > 5.0) {
            alertRepository.save(Alert.builder()
                    .location(location)
                    .deviceId(deviceId)
                    .alertType("TURBIDITY_EXCEEDED")
                    .severity("HIGH")
                    .message(String.format("Device %s recorded high turbidity of %.2f NTU (Standard limit: 5.0 NTU)", deviceId, turbidity))
                    .status("ACTIVE")
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        if (tds > 1000.0) {
            alertRepository.save(Alert.builder()
                    .location(location)
                    .deviceId(deviceId)
                    .alertType("CRITICAL_TDS_BREACH")
                    .severity("CRITICAL")
                    .message(String.format("Device %s recorded critical TDS of %.1f mg/L (Max limit: 1000 mg/L)", deviceId, tds))
                    .status("ACTIVE")
                    .timestamp(LocalDateTime.now())
                    .build());
            location.setRiskLevel("HIGH");
            locationRepository.save(location);
        }
    }
}
