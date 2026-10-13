package com.hydroshield.service;

import com.hydroshield.entity.HeavyMetalReading;
import com.hydroshield.entity.HmpiRecord;
import com.hydroshield.entity.MonitoringLocation;
import com.hydroshield.entity.SensorReading;
import com.hydroshield.repository.HeavyMetalReadingRepository;
import com.hydroshield.repository.HmpiRecordRepository;
import com.hydroshield.repository.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportReportService {

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Autowired
    private HeavyMetalReadingRepository heavyMetalReadingRepository;

    @Autowired
    private HmpiRecordRepository hmpiRecordRepository;

    public String generateSensorCsv(Long locationId) {
        List<SensorReading> readings = sensorReadingRepository.findByLocationIdOrderByTimestampDesc(locationId);
        StringBuilder csv = new StringBuilder();
        csv.append("Reading ID,Device ID,Timestamp,pH,Turbidity (NTU),TDS (mg/L),Source\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (SensorReading r : readings) {
            csv.append(String.format("%d,%s,%s,%.2f,%.2f,%.1f,%s\n",
                    r.getId(),
                    r.getDeviceId(),
                    r.getTimestamp().format(fmt),
                    r.getPh(),
                    r.getTurbidity(),
                    r.getTds(),
                    r.getSource() != null ? r.getSource() : "ESP32_SENSOR"
            ));
        }

        return csv.toString();
    }

    public String generateHmpiReportText(MonitoringLocation loc) {
        List<HeavyMetalReading> heavyMetals = heavyMetalReadingRepository.findByLocationIdOrderByTestDateDesc(loc.getId());
        List<HmpiRecord> hmpiRecords = hmpiRecordRepository.findByLocationIdOrderByCalculatedAtDesc(loc.getId());

        StringBuilder report = new StringBuilder();
        report.append("=========================================================\n");
        report.append("             HYDROSHIELD ENVIRONMENTAL REPORT            \n");
        report.append("       Heavy Metal Pollution Index & IoT Telemetry      \n");
        report.append("=========================================================\n\n");

        report.append("LOCATION SUMMARY:\n");
        report.append("Station Code: ").append(loc.getLocationCode()).append("\n");
        report.append("Station Name: ").append(loc.getName()).append("\n");
        report.append("District/State: ").append(loc.getDistrict()).append(", ").append(loc.getState()).append("\n");
        report.append("Coordinates: ").append(loc.getLatitude()).append(", ").append(loc.getLongitude()).append("\n");
        report.append("Risk Classification: ").append(loc.getRiskLevel()).append("\n\n");

        report.append("HEAVY METAL POLLUTION INDEX (HMPI):\n");
        if (!hmpiRecords.isEmpty()) {
            HmpiRecord latest = hmpiRecords.get(0);
            report.append("Latest Calculated HMPI: ").append(latest.getHmpiValue()).append("\n");
            report.append("Pollution Category: ").append(latest.getPollutionCategory()).append("\n");
            report.append("Calculation Date: ").append(latest.getCalculatedAt()).append("\n");
        } else {
            report.append("No HMPI record available (Requires laboratory heavy-metal dataset).\n");
        }
        report.append("\n");

        report.append("VALIDATED LABORATORY HEAVY METAL DATA:\n");
        for (HeavyMetalReading hm : heavyMetals) {
            report.append(String.format("Date: %s | Lead: %s | Cadmium: %s | Iron: %s | Arsenic: %s | Source: %s\n",
                    hm.getTestDate(),
                    hm.getLeadPb() != null ? hm.getLeadPb() + " mg/L" : "N/A",
                    hm.getCadmiumCd() != null ? hm.getCadmiumCd() + " mg/L" : "N/A",
                    hm.getIronFe() != null ? hm.getIronFe() + " mg/L" : "N/A",
                    hm.getArsenicAs() != null ? hm.getArsenicAs() + " mg/L" : "N/A",
                    hm.getDataSource()
            ));
        }
        report.append("\n=========================================================\n");

        return report.toString();
    }
}
