package com.hydroshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String phone;

    @Column(nullable = false)
    private String role; // PUBLIC_USER, RESEARCHER

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    private String organization;
    private String designation;

    @Column(name = "research_area")
    private String researchArea;

    private String locality;
    private String district;
    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "water_source")
    private String waterSource;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PUBLIC_USER".equalsIgnoreCase(role) ? "APPROVED" : "PENDING";
    }
}
