package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "heavy_metal_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeavyMetalReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private MonitoringLocation location;

    @Column(name = "test_date", nullable = false)
    private LocalDateTime testDate;

    @Column(name = "lead_pb")
    private Double leadPb; // mg/L

    @Column(name = "cadmium_cd")
    private Double cadmiumCd; // mg/L

    @Column(name = "iron_fe")
    private Double ironFe; // mg/L

    @Column(name = "arsenic_as")
    private Double arsenicAs; // mg/L

    @Column(name = "mercury_hg")
    private Double mercuryHg; // mg/L

    @Column(name = "data_source", nullable = false)
    private String dataSource; // LABORATORY, MANUAL_RESEARCHER_ENTRY, IMPORTED_DATASET
}
