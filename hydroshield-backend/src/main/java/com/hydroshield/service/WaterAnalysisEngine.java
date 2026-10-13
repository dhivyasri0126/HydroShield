package com.hydroshield.service;

import com.hydroshield.dto.WaterSuitabilityResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WaterAnalysisEngine {

    public WaterSuitabilityResponse analyze(Double ph, Double turbidity, Double tds, boolean hasHeavyMetals) {
        String phStatus = evaluatePh(ph);
        String turbStatus = evaluateTurbidity(turbidity);
        String tdsStatus = evaluateTds(tds);

        Map<String, String> suitability = new HashMap<>();
        Map<String, String> reasons = new HashMap<>();

        // 1. Drinking Assessment
        if (!hasHeavyMetals) {
            suitability.put("Drinking", "INSUFFICIENT_DATA");
            reasons.put("Drinking", "Only pH, turbidity and TDS were tested by physical IoT sensors. A complete drinking-water assessment requires additional laboratory heavy-metal and microbiological parameters.");
            
            suitability.put("Cooking", "INSUFFICIENT_DATA");
            reasons.put("Cooking", "Cooking water requires verified heavy-metal and chemical safety testing beyond basic physical sensors.");
        } else {
            boolean drinkingOk = ph >= 6.5 && ph <= 8.5 && turbidity <= 5.0 && tds <= 1000.0;
            suitability.put("Drinking", drinkingOk ? "SUITABLE" : "NOT_SUITABLE");
            reasons.put("Drinking", drinkingOk 
                ? "Physical parameters and laboratory heavy-metal measurements meet standard drinking water criteria."
                : "Parameters exceed standard drinking limits (pH 6.5-8.5, Turbidity <= 5 NTU, TDS <= 1000 mg/L).");
            
            suitability.put("Cooking", drinkingOk ? "SUITABLE" : "NOT_SUITABLE");
            reasons.put("Cooking", drinkingOk ? "Suitable for food preparation." : "Water quality parameters exceed limits for culinary use.");
        }

        // 2. Bathing Assessment
        boolean bathingOk = ph >= 6.0 && ph <= 9.0 && turbidity <= 10.0;
        suitability.put("Bathing", bathingOk ? "SUITABLE" : "NOT_SUITABLE");
        reasons.put("Bathing", bathingOk 
            ? "Assessment based on available physical parameters (pH 6.0-9.0, Turbidity <= 10 NTU)."
            : "pH or turbidity levels may cause skin or eye irritation.");

        // 3. Washing & Household Use
        boolean washingOk = ph >= 6.0 && ph <= 9.0 && tds <= 1500.0;
        suitability.put("Washing", washingOk ? "SUITABLE" : "NOT_SUITABLE");
        reasons.put("Washing", washingOk 
            ? "Suitable for clothes washing and general cleaning based on physical parameters."
            : "High TDS or extreme pH may cause scaling or staining.");

        suitability.put("Household", washingOk ? "SUITABLE" : "NOT_SUITABLE");
        reasons.put("Household", washingOk ? "Suitable for non-ingestion domestic tasks." : "Elevated mineral content may damage household appliances.");

        // 4. Irrigation Assessment
        boolean irrigationOk = ph >= 6.5 && ph <= 8.5 && tds <= 2000.0;
        suitability.put("Irrigation", irrigationOk ? "SUITABLE" : "NOT_SUITABLE");
        reasons.put("Irrigation", irrigationOk 
            ? "Salinity and pH levels are suitable for crop irrigation and gardening."
            : "High TDS (> 2000 mg/L) causes soil salinity hazard.");

        String sourcesStr = hasHeavyMetals 
            ? "Hardware ESP32 (pH, Turbidity, TDS) + Certified Laboratory (Heavy Metals)" 
            : "Hardware ESP32 Sensor (pH, Turbidity, TDS)";

        return WaterSuitabilityResponse.builder()
                .ph(ph)
                .turbidity(turbidity)
                .tds(tds)
                .phStatus(phStatus)
                .turbidityStatus(turbStatus)
                .tdsStatus(tdsStatus)
                .suitability(suitability)
                .reasons(reasons)
                .hasHeavyMetals(hasHeavyMetals)
                .dataSources(sourcesStr)
                .build();
    }

    private String evaluatePh(Double ph) {
        if (ph < 6.5) return "Acidic (Below 6.5 reference limit)";
        if (ph > 8.5) return "Alkaline (Above 8.5 reference limit)";
        return "Optimal (6.5 - 8.5 standard reference range)";
    }

    private String evaluateTurbidity(Double turbidity) {
        if (turbidity <= 1.0) return "Desirable Clear (<= 1.0 NTU)";
        if (turbidity <= 5.0) return "Acceptable (<= 5.0 NTU standard limit)";
        return "Elevated Cloudiness (> 5.0 NTU limit exceeded)";
    }

    private String evaluateTds(Double tds) {
        if (tds <= 500.0) return "Desirable (< 500 mg/L)";
        if (tds <= 1000.0) return "Acceptable (< 1000 mg/L max limit)";
        return "High Total Dissolved Solids (> 1000 mg/L limit exceeded)";
    }
}
