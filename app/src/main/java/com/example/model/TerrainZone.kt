package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class TerrainType(
    val title: String,
    val iconName: String,
    val color: Color,
    val environmentalRisk: String,
    val speedModifier: String
) {
    CITY_STREETS(
        title = "City & Streets",
        iconName = "location_city",
        color = CyanGlow,
        environmentalRisk = "High Optical Density • Signal Reflection • Dense Traffic",
        speedModifier = "50-80 km/h (Paved)"
    ),
    VILLAGE_SETTLEMENT(
        title = "Villages & Hamlets",
        iconName = "holiday_village",
        color = TacticalAmber,
        environmentalRisk = "Unpaved Outposts • Local Informants • Limited Cover",
        speedModifier = "25-40 km/h (Gravel)"
    ),
    FOREST_WOODS(
        title = "Forests & Woods",
        iconName = "forest",
        color = TacticalEmerald,
        environmentalRisk = "Dense Canopy • Thermal Occlusion • Acoustic Baffles",
        speedModifier = "4-12 km/h (Off-Road)"
    ),
    MOUNTAIN_RANGE(
        title = "High Mountains & Peaks",
        iconName = "terrain",
        color = Color(0xFF67E8F9),
        environmentalRisk = "Severe Altitude • Snowpack • Radar Canyon Shadows",
        speedModifier = "2-8 km/h (Alpine Trail)"
    )
}

enum class MapDisplayMode(val label: String) {
    SATELLITE_HYBRID("Satellite Hybrid"),
    TACTICAL_HUD("Tactical Grid"),
    THERMAL_INFRARED("Thermal FLIR"),
    ELEVATION_CONTOUR("Topographic Elevation")
}

data class TerrainZone(
    val id: String,
    val name: String,
    val type: TerrainType,
    val sectorCode: String,
    val coordinates: String,
    val elevationMeters: Int,
    val signalAttenuationDb: Int,
    val concealmentRating: Int, // 0 - 100%
    val activeSensors: List<String>,
    val suspectedTargets: List<String>,
    val tacticalGuidance: String,
    val xRatio: Float, // 0.0 - 1.0 position on map
    val yRatio: Float
)
