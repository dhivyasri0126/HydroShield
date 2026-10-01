package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_water_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeWaterTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "test_date", nullable = false)
    private LocalDateTime testDate;

    @Column(name = "water_source", nullable = false)
    private String waterSource; // Borewell, Open Well, Municipal/Tap, Other

    @Column(nullable = false)
    private Double ph;

    @Column(nullable = false)
    private Double turbidity;

    @Column(nullable = false)
    private Double tds;

    @Column(name = "optional_lead")
    private Double optionalLead;

    @Column(name = "optional_cadmium")
    private Double optionalCadmium;

    @Column(name = "optional_iron")
    private Double optionalIron;

    @Column(name = "optional_arsenic")
    private Double optionalArsenic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (testDate == null) testDate = LocalDateTime.now();
    }
}
