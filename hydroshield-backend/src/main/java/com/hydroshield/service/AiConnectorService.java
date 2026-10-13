package com.hydroshield.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiConnectorService {

    @Value("${ai.service.url:http://localhost:8000/api/v1/ai}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getPublicExplanation(Double ph, Double turbidity, Double tds, String locationName, String waterSource, boolean hasHeavyMetals) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("ph", ph);
            req.put("turbidity", turbidity);
            req.put("tds", tds);
            req.put("locationName", locationName != null ? locationName : "Selected Monitoring Station");
            req.put("waterSource", waterSource != null ? waterSource : "Borewell");
            req.put("hasHeavyMetals", hasHeavyMetals);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl + "/explain-public", entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("AI Service call failed: " + e.getMessage());
        }

        // Fallback explanation if AI service unreachable
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("source", "HydroShield-Local-Rule-Engine");
        fallback.put("explanation", String.format(
            "### Water Quality Breakdown\n\n- **pH (%.2f)**: %s\n- **Turbidity (%.2f NTU)**: %s\n- **TDS (%.1f mg/L)**: %s\n\n" +
            "⚠️ **Hardware Note**: Physical ESP32 hardware measures ONLY pH, Turbidity, and TDS. " +
            "Drinking assessment requires laboratory heavy-metal validation.",
            ph, ph >= 6.5 && ph <= 8.5 ? "Normal" : "Abnormal",
            turbidity, turbidity <= 5.0 ? "Acceptable" : "High Cloudiness",
            tds, tds <= 1000.0 ? "Within limits" : "Elevated TDS"
        ));
        fallback.put("drinkingSuitability", hasHeavyMetals ? "EVALUATED" : "INSUFFICIENT_DATA");
        return fallback;
    }
}
