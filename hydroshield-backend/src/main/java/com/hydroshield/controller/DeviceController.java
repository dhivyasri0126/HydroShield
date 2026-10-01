package com.hydroshield.controller;

import com.hydroshield.entity.Device;
import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.repository.DeviceRepository;
import com.hydroshield.repository.MonitoringLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MonitoringLocationRepository locationRepository;

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> registerDevice(@RequestBody Device device) {
        if (device.getDeviceId() == null || device.getDeviceId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Device ID is required");
        }
        if (deviceRepository.findByDeviceId(device.getDeviceId()).isPresent()) {
            return ResponseEntity.badRequest().body("Device ID already exists");
        }

        if (device.getLocation() != null && device.getLocation().getId() != null) {
            Optional<MonitoringLocation> loc = locationRepository.findById(device.getLocation().getId());
            loc.ifPresent(device::setLocation);
        }

        device.setStatus("ONLINE");
        device.setLastSeen(LocalDateTime.now());
        Device saved = deviceRepository.save(device);

        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateDeviceStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Device> devOpt = deviceRepository.findById(id);
        if (devOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Device dev = devOpt.get();
        dev.setStatus(status.toUpperCase());
        deviceRepository.save(dev);

        return ResponseEntity.ok(dev);
    }
}
