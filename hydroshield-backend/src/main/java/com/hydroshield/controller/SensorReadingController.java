package com.hydroshield.controller;

import com.hydroshield.dto.SensorReadingDTO;
import com.hydroshield.entity.SensorReading;
import com.hydroshield.repository.SensorReadingRepository;
import com.hydroshield.service.SensorReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sensor-readings")
@CrossOrigin(origins = "*")
public class SensorReadingController {

    @Autowired
    private SensorReadingService sensorReadingService;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @PostMapping
    public ResponseEntity<?> receiveSensorReading(@RequestBody SensorReadingDTO dto) {
        try {
            SensorReading saved = sensorReadingService.ingestReading(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing telemetry: " + e.getMessage());
        }
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<SensorReading>> getReadingsByLocation(@PathVariable Long locationId) {
        List<SensorReading> readings = sensorReadingRepository.findByLocationIdOrderByTimestampDesc(locationId);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/location/{locationId}/latest")
    public ResponseEntity<?> getLatestReadingByLocation(@PathVariable Long locationId) {
        Optional<SensorReading> latest = sensorReadingRepository.findFirstByLocationIdOrderByTimestampDesc(locationId);
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No recent groundwater monitoring data is available for this location.");
    }
}
