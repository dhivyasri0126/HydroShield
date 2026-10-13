package com.hydroshield.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReadingDTO {
    private String deviceId; // ESP32-001
    private Long locationId;
    private String timestamp;
    private Double ph;
    private Double turbidity;
    private Double tds;
}
