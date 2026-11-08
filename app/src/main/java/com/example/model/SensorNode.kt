package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SensorType(val displayName: String, val iconName: String) {
    BIOMETRIC_SCANNER("Biometric Facial Scanner", "face"),
    ACOUSTIC_ARRAY("Acoustic / Seismic Array", "mic"),
    SATELLITE_THERMAL("Orbital Thermal Recon", "satellite"),
    DRONE_RECON("Autonomous Patrol Drone", "flight"),
    CYBER_PERIMETER("Cyber Intrusion Node", "security"),
    GEOFENCE_BEACON("RF Geofence Transponder", "cell_tower")
}

enum class SensorStatus(val label: String, val color: Color) {
    ONLINE("Operational", TacticalEmerald),
    ALERTING("Threat Alert", AlertCrimson),
    CALIBRATING("Calibrating", TacticalAmber),
    OFFLINE("Offline", TextMuted)
}

data class SensorNode(
    val id: String,
    val name: String,
    val sector: String,
    val type: SensorType,
    val status: SensorStatus,
    val valueMetric: String,
    val signalStrength: Int, // 0 - 100
    val batteryPercent: Int,
    val lastPingSecondsAgo: Int,
    val xPosRatio: Float, // 0.0 - 1.0 on tactical radar
    val yPosRatio: Float,
    val encryptedPacketsPerSec: Int
)
