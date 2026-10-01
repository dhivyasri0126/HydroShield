package com.hydroshield.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicWaterTestDTO {
    private Long id;
    private Long userId;
    private String testDate;
    private String waterSource; // Borewell, Open Well, Municipal/Tap, Other
    private Double ph;
    private Double turbidity;
    private Double tds;
    
    // Optional laboratory fields
    private Double optionalLead;
    private Double optionalCadmium;
    private Double optionalIron;
    private Double optionalArsenic;
}
