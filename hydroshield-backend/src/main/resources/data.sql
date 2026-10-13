-- Seed Demo Users
-- Password for all demo accounts: password123
INSERT INTO users (name, email, password_hash, phone, role, status, organization, designation, research_area, locality, district, state, pin_code, water_source) VALUES
('Alex Johnson', 'public@hydroshield.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeF5b4xI.uB8g4G0c4xX4.YjS1yW.Yx6S', '9876543210', 'PUBLIC_USER', 'APPROVED', NULL, NULL, NULL, 'Peelamedu', 'Coimbatore', 'Tamil Nadu', '641004', 'Borewell'),
('Dr. Sarah Lin', 'researcher@hydroshield.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeF5b4xI.uB8g4G0c4xX4.YjS1yW.Yx6S', '9876543211', 'RESEARCHER', 'APPROVED', 'Tamil Nadu Water Institute', 'Senior Hydrogeologist', 'Heavy Metal Speciation & IoT Ingestion', 'Gandhipuram', 'Coimbatore', 'Tamil Nadu', '641012', 'Municipal/Tap'),
('Prof. Marcus Vance', 'pending@hydroshield.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeF5b4xI.uB8g4G0c4xX4.YjS1yW.Yx6S', '9876543212', 'RESEARCHER', 'PENDING', 'Anna University Tech Lab', 'Assistant Researcher', 'Groundwater Contaminants', 'Guindy', 'Chennai', 'Tamil Nadu', '600025', 'Open Well');

-- Seed Monitoring Locations
INSERT INTO monitoring_locations (location_code, name, district, area, state, latitude, longitude, risk_level) VALUES
('LOC-CBE-01', 'Coimbatore Industrial Aquifer Station', 'Coimbatore', 'Peelamedu', 'Tamil Nadu', 11.0283, 77.0007, 'LOW'),
('LOC-MAA-02', 'Chennai Subsurface Basin Station', 'Chennai', 'Guindy', 'Tamil Nadu', 13.0067, 80.2020, 'MODERATE'),
('LOC-MDU-03', 'Madurai Vaigai River Monitoring Node', 'Madurai', 'Anna Nagar', 'Tamil Nadu', 9.9252, 78.1198, 'HIGH');

-- Seed Hardware ESP32 Devices
INSERT INTO devices (device_id, device_name, location_id, sensor_config, status, last_seen) VALUES
('ESP32-001', 'Coimbatore Telemetry Node A', 1, 'pH + Turbidity + TDS', 'ONLINE', CURRENT_TIMESTAMP),
('ESP32-002', 'Chennai Coastal Monitoring Node B', 2, 'pH + Turbidity + TDS', 'ONLINE', CURRENT_TIMESTAMP),
('ESP32-003', 'Madurai Subsurface Telemetry Node C', 3, 'pH + Turbidity + TDS', 'OFFLINE', CURRENT_TIMESTAMP);

-- Seed Historical ESP32 Sensor Telemetry (Physical parameters ONLY)
INSERT INTO sensor_readings (device_id, location_id, timestamp, ph, turbidity, tds, source) VALUES
('ESP32-001', 1, '2026-09-30 18:00:00', 7.20, 3.40, 340.0, 'ESP32_SENSOR'),
('ESP32-001', 1, '2026-09-30 19:00:00', 7.25, 3.50, 345.0, 'ESP32_SENSOR'),
('ESP32-001', 1, '2026-09-30 20:00:00', 7.18, 3.20, 338.0, 'ESP32_SENSOR'),
('ESP32-001', 1, '2026-09-30 21:00:00', 7.22, 3.35, 342.0, 'ESP32_SENSOR'),

('ESP32-002', 2, '2026-09-30 18:00:00', 6.75, 6.10, 580.0, 'ESP32_SENSOR'),
('ESP32-002', 2, '2026-09-30 19:00:00', 6.80, 6.30, 595.0, 'ESP32_SENSOR'),
('ESP32-002', 2, '2026-09-30 20:00:00', 6.70, 5.95, 575.0, 'ESP32_SENSOR'),
('ESP32-002', 2, '2026-09-30 21:00:00', 6.72, 6.20, 588.0, 'ESP32_SENSOR'),

('ESP32-003', 3, '2026-09-30 18:00:00', 8.40, 8.80, 890.0, 'ESP32_SENSOR'),
('ESP32-003', 3, '2026-09-30 19:00:00', 8.55, 9.20, 920.0, 'ESP32_SENSOR');

-- Seed Validated Laboratory Heavy Metal Records (Separated from Physical Sensors)
INSERT INTO heavy_metal_readings (location_id, test_date, lead_pb, cadmium_cd, iron_fe, arsenic_as, mercury_hg, data_source) VALUES
(1, '2026-09-25 10:00:00', 0.002, 0.0005, 0.15, 0.001, 0.0001, 'LABORATORY'),
(2, '2026-09-20 11:30:00', 0.015, 0.0040, 0.45, 0.012, 0.0012, 'MANUAL_RESEARCHER_ENTRY'),
(3, '2026-09-15 09:15:00', 0.045, 0.0090, 1.20, 0.035, 0.0035, 'IMPORTED_DATASET');

-- Seed Calculated HMPI Records
INSERT INTO hmpi_records (location_id, calculated_at, hmpi_value, pollution_category) VALUES
(1, '2026-09-25 10:05:00', 18.4, 'CLEAN'),
(2, '2026-09-20 11:35:00', 62.8, 'MODERATELY_POLLUTED'),
(3, '2026-09-15 09:20:00', 178.5, 'HEAVILY_POLLUTED');

-- Seed Sample Active Alerts
INSERT INTO alerts (location_id, device_id, alert_type, severity, message, status, timestamp) VALUES
(2, 'ESP32-002', 'TURBIDITY_EXCEEDED', 'MEDIUM', 'Turbidity reading (6.2 NTU) exceeded drinking reference limit (5.0 NTU)', 'ACTIVE', '2026-09-30 21:00:00'),
(3, 'ESP32-003', 'HIGH_TDS_WARNING', 'HIGH', 'TDS reading (920 mg/L) approaching maximum permissible threshold (1000 mg/L)', 'ACTIVE', '2026-09-30 19:00:00');
