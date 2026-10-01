package com.hydroshield.controller;

import com.hydroshield.entity.Alert;
import com.hydroshield.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        return ResponseEntity.ok(alertRepository.findByStatusOrderByTimestampDesc("ACTIVE"));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Alert>> getAlertsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(alertRepository.findByLocationIdOrderByTimestampDesc(locationId));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long id) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Alert alert = alertOpt.get();
        alert.setStatus("RESOLVED");
        alertRepository.save(alert);

        return ResponseEntity.ok(alert);
    }
}
