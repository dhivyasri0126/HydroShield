package com.hydroshield.dto;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterSuitabilityResponse {
    private Double ph;
    private Double turbidity;
    private Double tds;
    private String phStatus;
    private String turbidityStatus;
    private String tdsStatus;
    
    private Map<String, String> suitability; // { "drinking": "INSUFFICIENT_DATA", "bathing": "SUITABLE", ... }
    private Map<String, String> reasons; // { "drinking": "Only pH, turbidity and TDS were tested. A complete drinking-water assessment requires additional laboratory parameters.", ... }
    
    private boolean hasHeavyMetals;
    private String dataSources;
}
