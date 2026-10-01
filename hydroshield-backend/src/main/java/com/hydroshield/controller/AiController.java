package com.hydroshield.controller;

import com.hydroshield.service.AiConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private AiConnectorService aiConnectorService;

    @PostMapping("/explain")
    public ResponseEntity<Map<String, Object>> getPublicAiExplanation(@RequestBody Map<String, Object> req) {
        Double ph = req.get("ph") != null ? Double.parseDouble(req.get("ph").toString()) : 7.0;
        Double turbidity = req.get("turbidity") != null ? Double.parseDouble(req.get("turbidity").toString()) : 3.0;
        Double tds = req.get("tds") != null ? Double.parseDouble(req.get("tds").toString()) : 300.0;
        String locationName = (String) req.get("locationName");
        String waterSource = (String) req.get("waterSource");
        Boolean hasHeavyMetals = req.get("hasHeavyMetals") != null ? (Boolean) req.get("hasHeavyMetals") : false;

        Map<String, Object> result = aiConnectorService.getPublicExplanation(ph, turbidity, tds, locationName, waterSource, hasHeavyMetals);
        return ResponseEntity.ok(result);
    }
}
