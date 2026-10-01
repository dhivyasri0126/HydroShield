@echo off
title HydroShield Platform Launcher
color 0A

echo ===================================================================
echo             HYDROSHIELD PLATFORM SERVICES LAUNCHER                 
echo ===================================================================
echo.
echo Starting HydroShield AI Microservice, Spring Boot Backend,
echo ESP32 Hardware Telemetry Simulator, and React Frontend...
echo.

:: 1. Start Python AI Service on Port 8000
echo [1/4] Starting AI Microservice on http://localhost:8000 ...
start "HydroShield AI Service (Port 8000)" cmd /k "cd /d D:\HydroShield\ai-service && "C:\Program Files\PostgreSQL\18\pgAdmin 4\python\python.exe" main.py"

:: 2. Start Spring Boot Backend on Port 8080
echo [2/4] Starting Spring Boot Backend on http://localhost:8080 ...
start "HydroShield Spring Boot Backend (Port 8080)" cmd /k "cd /d D:\HydroShield\hydroshield-backend && "C:\Users\ASUS\.m2\wrapper\dists\apache-maven-3.9.16-bin\5grr65jo27hi51sujmtcldfovl\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run"

:: Wait 10 seconds for backend initialization
timeout /t 10 /nobreak > NUL

:: 3. Start ESP32 Hardware Simulator
echo [3/4] Starting ESP32 Hardware Telemetry Simulator ...
start "HydroShield ESP32 Simulator" cmd /k "cd /d D:\HydroShield\simulator && "C:\Program Files\PostgreSQL\18\pgAdmin 4\python\python.exe" sensor_simulator.py --interval 5"

:: 4. Start React Frontend Dashboard on Port 3000
echo [4/4] Starting React Frontend Dashboard on http://localhost:3000 ...
start "HydroShield React Frontend (Port 3000)" cmd /k "cd /d D:\HydroShield\hydroshield-frontend && npm run dev"

echo.
echo ===================================================================
echo HydroShield system started successfully!
echo Access Public & Researcher Portals at: http://localhost:3000
echo ===================================================================
pause
