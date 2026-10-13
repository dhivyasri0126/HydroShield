package com.hydroshield.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HmpiCalculationResponse {
    private Long locationId;
    private String locationName;
    private String calculatedAt;
    private Double hmpiValue;
    private String pollutionCategory; // CLEAN, SLIGHTLY_POLLUTED, MODERATELY_POLLUTED, HEAVILY_POLLUTED
    private Map<String, Double> parameterConcentrations; // { "Lead (Pb)": 0.002, "Cadmium (Cd)": 0.0005, ... }
    private String dataSource;
    private String description;
}
