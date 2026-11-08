package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class LocationCategory(
    val displayName: String,
    val iconName: String,
    val themeColor: Color,
    val backgroundTint: Color,
    val badgeLabel: String,
    val environmentalType: String
) {
    FOREST(
        displayName = "Forests & Woods",
        iconName = "forest",
        themeColor = TacticalEmerald,
        backgroundTint = Color(0xFF064E3B),
        badgeLabel = "FOREST",
        environmentalType = "Dense Canopy & Off-Grid Woodland"
    ),
    MOUNTAINS(
        displayName = "Mountains & Peaks",
        iconName = "terrain",
        themeColor = Color(0xFF38BDF8),
        backgroundTint = Color(0xFF0C4A6E),
        badgeLabel = "MOUNTAIN",
        environmentalType = "Alpine Ridge & High Elevation"
    ),
    URBAN(
        displayName = "Urban & City Streets",
        iconName = "location_city",
        themeColor = CyanGlow,
        backgroundTint = Color(0xFF164E63),
        badgeLabel = "URBAN",
        environmentalType = "Metropolitan Grid & Streets"
    ),
    VILLAGE(
        displayName = "Villages & Hamlets",
        iconName = "holiday_village",
        themeColor = TacticalAmber,
        backgroundTint = Color(0xFF78350F),
        badgeLabel = "VILLAGE",
        environmentalType = "Rural Settlement & Outpost"
    )
}

enum class MarkerOperationalStatus(val label: String, val color: Color) {
    SURVEILLANCE_ACTIVE("Active Surveillance", CyanGlow),
    THREAT_DETECTED("Hostile / Threat Alert", AlertCrimson),
    SECURE("Secured Perimeter", TacticalEmerald),
    UNMONITORED("Unmonitored Zone", TextMuted)
}

data class MapMarker(
    val id: String,
    val name: String,
    val category: LocationCategory,
    val coordinates: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Int,
    val xRatio: Float, // 0.0 - 1.0 relative horizontal coordinate on map
    val yRatio: Float, // 0.0 - 1.0 relative vertical coordinate on map
    val status: MarkerOperationalStatus,
    val threatRating: ThreatLevel,
    val coverageRadiusKm: Float,
    val telemetryReadout: String,
    val keySuspectTracked: String? = null,
    val sensorDevices: List<String> = emptyList(),
    val notes: String = ""
)
