package com.hydroshield.controller;

import com.hydroshield.dto.HeavyMetalInputDTO;
import com.hydroshield.dto.HmpiCalculationResponse;
import com.hydroshield.entity.HeavyMetalReading;
import com.hydroshield.entity.HmpiRecord;
import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.repository.HeavyMetalReadingRepository;
import com.hydroshield.repository.HmpiRecordRepository;
import com.hydroshield.repository.MonitoringLocationRepository;
import com.hydroshield.service.HmpiEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/heavy-metals")
@CrossOrigin(origins = "*")
public class HeavyMetalController {

    @Autowired
    private HeavyMetalReadingRepository heavyMetalReadingRepository;

    @Autowired
    private MonitoringLocationRepository locationRepository;

    @Autowired
    private HmpiRecordRepository hmpiRecordRepository;

    @Autowired
    private HmpiEngine hmpiEngine;

    @PostMapping
    public ResponseEntity<?> addHeavyMetalReading(@RequestBody HeavyMetalInputDTO dto) {
        Optional<MonitoringLocation> locOpt = locationRepository.findById(dto.getLocationId());
        if (locOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Location ID not found");
        }

        LocalDateTime testDate = dto.getTestDate() != null 
            ? LocalDateTime.parse(dto.getTestDate()) 
            : LocalDateTime.now();

        HeavyMetalReading reading = HeavyMetalReading.builder()
                .location(locOpt.get())
                .testDate(testDate)
                .leadPb(dto.getLeadPb())
                .cadmiumCd(dto.getCadmiumCd())
                .ironFe(dto.getIronFe())
                .arsenicAs(dto.getArsenicAs())
                .mercuryHg(dto.getMercuryHg())
                .dataSource(dto.getDataSource() != null ? dto.getDataSource() : "LABORATORY")
                .build();

        reading = heavyMetalReadingRepository.save(reading);

        // Auto calculate & store HMPI for this new reading
        HmpiCalculationResponse calc = hmpiEngine.calculateHmpi(reading);
        if (!"NO_DATA".equals(calc.getPollutionCategory())) {
            HmpiRecord record = HmpiRecord.builder()
                    .location(locOpt.get())
                    .calculatedAt(LocalDateTime.now())
                    .hmpiValue(calc.getHmpiValue())
                    .pollutionCategory(calc.getPollutionCategory())
                    .build();
            hmpiRecordRepository.save(record);
        }

        return ResponseEntity.ok(reading);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<HeavyMetalReading>> getHeavyMetalsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(heavyMetalReadingRepository.findByLocationIdOrderByTestDateDesc(locationId));
    }

    @PostMapping("/calculate-hmpi/{locationId}")
    public ResponseEntity<?> calculateHmpi(@PathVariable Long locationId) {
        Optional<HeavyMetalReading> latestOpt = heavyMetalReadingRepository.findFirstByLocationIdOrderByTestDateDesc(locationId);
        if (latestOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No validated heavy metal data exists for this location to calculate HMPI.");
        }

        HeavyMetalReading reading = latestOpt.get();
        HmpiCalculationResponse response = hmpiEngine.calculateHmpi(reading);

        // Save HMPI calculation record
        HmpiRecord record = HmpiRecord.builder()
                .location(reading.getLocation())
                .calculatedAt(LocalDateTime.now())
                .hmpiValue(response.getHmpiValue())
                .pollutionCategory(response.getPollutionCategory())
                .build();
        hmpiRecordRepository.save(record);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hmpi/{locationId}")
    public ResponseEntity<?> getLatestHmpi(@PathVariable Long locationId) {
        Optional<HmpiRecord> latest = hmpiRecordRepository.findFirstByLocationIdOrderByCalculatedAtDesc(locationId);
        if (latest.isPresent()) {
            return ResponseEntity.ok(latest.get());
        }
        return ResponseEntity.ok().body("No HMPI record calculated yet.");
    }
}
