package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoring_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_code", nullable = false, unique = true)
    private String locationCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String state;

    private Double latitude;
    private Double longitude;

    @Column(name = "risk_level")
    private String riskLevel; // LOW, MODERATE, HIGH, CRITICAL

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (riskLevel == null) riskLevel = "LOW";
    }
}
