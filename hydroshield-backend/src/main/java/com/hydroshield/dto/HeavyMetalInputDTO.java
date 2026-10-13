package com.hydroshield.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeavyMetalInputDTO {
    private Long locationId;
    private String testDate;
    private Double leadPb; // mg/L
    private Double cadmiumCd; // mg/L
    private Double ironFe; // mg/L
    private Double arsenicAs; // mg/L
    private Double mercuryHg; // mg/L
    private String dataSource; // LABORATORY, MANUAL_RESEARCHER_ENTRY, IMPORTED_DATASET
}
