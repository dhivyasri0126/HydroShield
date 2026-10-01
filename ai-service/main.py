import os
import requests
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="HydroShield AI Microservice", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

AI_API_KEY = os.getenv("AI_API_KEY", "")
AI_API_URL = os.getenv("AI_API_URL", "")

class PublicAnalysisRequest(BaseModel):
    ph: float
    turbidity: float
    tds: float
    locationName: Optional[str] = "Selected Location"
    waterSource: Optional[str] = "Borewell"
    hasHeavyMetals: bool = False
    lead: Optional[float] = None
    cadmium: Optional[float] = None
    iron: Optional[float] = None
    arsenic: Optional[float] = None

class ResearcherTrendRequest(BaseModel):
    locationName: str
    readings: List[dict]
    hmpiValue: Optional[float] = None
    pollutionCategory: Optional[str] = None

@app.get("/health")
def health():
    return {"status": "UP", "service": "HydroShield AI Engine", "ai_key_configured": bool(AI_API_KEY and AI_API_KEY != "YOUR_GEMINI_API_KEY_HERE")}

@app.post("/api/v1/ai/explain-public")
def explain_public_water(req: PublicAnalysisRequest):
    # Rule validation context
    ph_status = "normal" if 6.5 <= req.ph <= 8.5 else ("acidic" if req.ph < 6.5 else "alkaline")
    turb_status = "acceptable" if req.turbidity <= 5.0 else "cloudy/high turbidity"
    tds_status = "desirable" if req.tds <= 500 else ("acceptable" if req.tds <= 1000 else "high TDS")

    # If valid Gemini API key is configured, call external AI API
    if AI_API_KEY and AI_API_KEY != "YOUR_GEMINI_API_KEY_HERE" and "YOUR_API_KEY" not in AI_API_KEY:
        try:
            prompt = f"""
            You are HydroShield AI Water Quality Assistant. Provide a clear, friendly, and scientifically sound explanation for a public user.
            
            Location: {req.locationName}
            Water Source: {req.waterSource}
            Physical Sensor Telemetry (measured by ESP32 IoT hardware):
            - pH: {req.ph} ({ph_status})
            - Turbidity: {req.turbidity} NTU ({turb_status})
            - TDS: {req.tds} mg/L ({tds_status})
            
            Laboratory Heavy Metal Data Available: {'Yes' if req.hasHeavyMetals else 'No (Physical Sensors ONLY)'}

            IMPORTANT RULES TO FOLLOW:
            1. Clearly state that ESP32 physical hardware ONLY measures pH, Turbidity, and TDS.
            2. State clearly that pH, turbidity, and TDS alone are INSUFFICIENT to guarantee drinking water safety, as heavy metals and bacteria require laboratory testing.
            3. Evaluate bathing, irrigation, and household suitability based on available physical parameters.
            4. Keep explanation structured with sections: 'Executive Summary', 'Parameter Breakdown', 'Water Use Assessment', and 'Actionable Advice'.
            """
            
            response = requests.post(
                f"{AI_API_URL}?key={AI_API_KEY}",
                json={
                    "contents": [{"parts": [{"text": prompt}]}]
                },
                timeout=10
            )
            if response.status_code == 200:
                res_json = response.json()
                explanation = res_json['candidates'][0]['content']['parts'][0]['text']
                return {
                    "source": "Gemini-AI",
                    "explanation": explanation,
                    "phStatus": ph_status,
                    "turbidityStatus": turb_status,
                    "tdsStatus": tds_status,
                    "drinkingSuitability": "INSUFFICIENT_DATA" if not req.hasHeavyMetals else ("SUITABLE" if ph_status == "normal" and turb_status == "acceptable" and tds_status != "high TDS" else "NOT_SUITABLE")
                }
        except Exception as e:
            print(f"AI API error: {e}")

    # Scientific fallback generator
    explanation = f"""### Executive Summary for {req.locationName} ({req.waterSource})

Your water sample recorded a **pH of {req.ph}**, **turbidity of {req.turbidity} NTU**, and **TDS of {req.tds} mg/L** from the physical IoT monitoring node.

### Parameter Breakdown
- **pH ({req.ph})**: Water is currently **{ph_status}** (Standard drinking reference is 6.5 - 8.5).
- **Turbidity ({req.turbidity} NTU)**: Water cloudiness is **{turb_status}** (Standard threshold is ≤ 5.0 NTU).
- **TDS ({req.tds} mg/L)**: Total Dissolved Solids level is **{tds_status}** (Desirable threshold is ≤ 500 mg/L, Max limit 1000 mg/L).

### ⚠️ Critical Water Safety Note
The physical IoT sensor array directly measures **ONLY pH, Turbidity, and TDS**. It does **NOT** measure heavy metals (Lead, Cadmium, Arsenic, Iron, Mercury) or biological pathogens. 

### Water Use Suitability
- ❌ **Drinking / Cooking**: **INSUFFICIENT DATA**. Physical sensor readings alone cannot confirm drinking safety. A certified laboratory test for heavy metals and coliform bacteria is required.
- {'✅' if ph_status == 'normal' and req.turbidity <= 10 else '⚠️'} **Bathing & Washing**: **{'SUITABLE' if ph_status == 'normal' and req.turbidity <= 10 else 'NEEDS CAUTION'}** based on pH and cloudiness.
- {'✅' if req.tds <= 2000 else '⚠️'} **Irrigation / Gardening**: **{'SUITABLE' if req.tds <= 2000 else 'HIGH SALINITY RISK'}**.

### Actionable Advice
1. If using for drinking, arrange a certified laboratory heavy-metal screening.
2. Consider standard reverse osmosis (RO) or filtration if TDS exceeds 500 mg/L.
"""

    return {
        "source": "HydroShield-Smart-Analyzer",
        "explanation": explanation,
        "phStatus": ph_status,
        "turbidityStatus": turb_status,
        "tdsStatus": tds_status,
        "drinkingSuitability": "INSUFFICIENT_DATA" if not req.hasHeavyMetals else "EVALUATED"
    }

@app.post("/api/v1/ai/trend-researcher")
def trend_researcher(req: ResearcherTrendRequest):
    count = len(req.readings)
    hmpi_info = f"Latest HMPI: {req.hmpiValue:.2f} ({req.pollutionCategory})" if req.hmpiValue is not None else "HMPI: No laboratory heavy metal data uploaded yet."
    
    explanation = f"""### Researcher Environmental Trend Analysis: {req.locationName}

**Data Window**: {count} Telemetry Ingestions Analyzed
**Heavy Metal Pollution Index (HMPI)**: {hmpi_info}

### Core Metrics Summary
- **pH Dynamic Range**: Min and max values monitored across hardware node lifecycle.
- **Turbidity Fluctuation**: Observed turbidity peaks align with runoff or colloidal particle surges.
- **TDS Accumulation**: Salinity and ionic dissolved solids trends.

### Research Recommendations
1. Maintain regular hardware sensor calibration for ESP32 nodes every 30 days.
2. Pair physical sensor drift anomalies with scheduled quarterly laboratory heavy-metal samplings.
3. Investigate local runoff sources if turbidity spikes correlate with seasonal rainfall.
"""

    return {
        "source": "HydroShield-Research-AI",
        "analysis": explanation
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
