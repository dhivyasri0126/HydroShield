package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hmpi_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HmpiRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private MonitoringLocation location;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "hmpi_value", nullable = false)
    private Double hmpiValue;

    @Column(name = "pollution_category", nullable = false)
    private String pollutionCategory; // CLEAN, SLIGHTLY_POLLUTED, MODERATELY_POLLUTED, HEAVILY_POLLUTED
}
