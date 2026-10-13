package com.hydroshield.controller;

import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.repository.MonitoringLocationRepository;
import com.hydroshield.service.ExportReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ExportReportService exportReportService;

    @Autowired
    private MonitoringLocationRepository locationRepository;

    @GetMapping("/excel/{locationId}")
    public ResponseEntity<String> exportExcelCsv(@PathVariable Long locationId) {
        String csvData = exportReportService.generateSensorCsv(locationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hydroshield_sensor_telemetry_loc_" + locationId + ".csv");

        return ResponseEntity.ok().headers(headers).body(csvData);
    }

    @GetMapping("/pdf/{locationId}")
    public ResponseEntity<String> exportPdfReport(@PathVariable Long locationId) {
        Optional<MonitoringLocation> locOpt = locationRepository.findById(locationId);
        if (locOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String pdfText = exportReportService.generateHmpiReportText(locOpt.get());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hydroshield_environmental_report_loc_" + locationId + ".txt");

        return ResponseEntity.ok().headers(headers).body(pdfText);
    }
}
