package com.hydroshield.controller;

import com.hydroshield.dto.WaterSuitabilityResponse;
import com.hydroshield.entity.HeavyMetalReading;
import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.entity.SensorReading;
import com.hydroshield.repository.HeavyMetalReadingRepository;
import com.hydroshield.repository.MonitoringLocationRepository;
import com.hydroshield.repository.SensorReadingRepository;
import com.hydroshield.service.WaterAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    @Autowired
    private MonitoringLocationRepository locationRepository;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private HeavyMetalReadingRepository heavyMetalReadingRepository;

    @Autowired
    private WaterAnalysisEngine waterAnalysisEngine;

    @GetMapping
    public ResponseEntity<List<MonitoringLocation>> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id) {
        Optional<MonitoringLocation> loc = locationRepository.findById(id);
        if (loc.isPresent()) {
            return ResponseEntity.ok(loc.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MonitoringLocation>> searchLocations(@RequestParam String query) {
        return ResponseEntity.ok(locationRepository.searchByLocationQuery(query));
    }

    @GetMapping("/{id}/suitability")
    public ResponseEntity<?> getWaterSuitability(@PathVariable Long id) {
        Optional<SensorReading> latestReading = sensorReadingRepository.findFirstByLocationIdOrderByTimestampDesc(id);
        if (latestReading.isEmpty()) {
            return ResponseEntity.badRequest().body("No recent groundwater monitoring data is available for this location.");
        }

        Optional<HeavyMetalReading> heavyMetal = heavyMetalReadingRepository.findFirstByLocationIdOrderByTestDateDesc(id);
        boolean hasHeavyMetals = heavyMetal.isPresent();

        SensorReading sr = latestReading.get();
        WaterSuitabilityResponse suitability = waterAnalysisEngine.analyze(sr.getPh(), sr.getTurbidity(), sr.getTds(), hasHeavyMetals);

        return ResponseEntity.ok(suitability);
    }

    @PostMapping
    public ResponseEntity<MonitoringLocation> createLocation(@RequestBody MonitoringLocation location) {
        if (location.getLocationCode() == null) {
            location.setLocationCode("LOC-" + System.currentTimeMillis() % 10000);
        }
        MonitoringLocation saved = locationRepository.save(location);
        return ResponseEntity.ok(saved);
    }
}
