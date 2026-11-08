package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class AlertSeverity(val label: String, val color: Color) {
    CRITICAL("CRITICAL", AlertCrimson),
    HIGH("HIGH", TacticalAmber),
    MEDIUM("ELEVATED", CyanGlow),
    LOW("ADVISORY", TacticalEmerald)
}

data class SystemAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: AlertSeverity,
    val timestamp: String,
    val sector: String,
    val sourceSensorId: String,
    val targetSuspectId: String?,
    val isResolved: Boolean,
    val actionTaken: String? = null
)
