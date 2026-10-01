package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId; // ESP32-001

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private MonitoringLocation location;

    @Column(name = "sensor_config")
    private String sensorConfig; // pH + Turbidity + TDS

    private String status; // ONLINE, OFFLINE, INACTIVE

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
}
