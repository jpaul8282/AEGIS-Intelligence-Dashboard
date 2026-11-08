package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ThreatLevel(val label: String, val color: Color, val priorityRank: Int) {
    ALPHA_EXTREME("Alpha - Extreme", AlertCrimson, 4),
    BRAVO_HIGH("Bravo - High", TacticalAmber, 3),
    CHARLIE_MODERATE("Charlie - Moderate", CyanGlow, 2),
    DELTA_LOW("Delta - Low", TacticalEmerald, 1)
}

enum class SuspectStatus(val label: String, val color: Color) {
    AT_LARGE("At Large", AlertCrimson),
    SURVEILLANCE_ACTIVE("Active Surveillance", CyanGlow),
    INTERCEPT_INBOUND("Intercept Inbound", TacticalAmber),
    APPREHENDED("In Custody", TacticalEmerald)
}

enum class Jurisdiction(val code: String, val fullName: String, val region: String) {
    INTERPOL("INTERPOL", "International Criminal Police Org", "Global"),
    EUROPOL("EUROPOL", "European Union Agency for Law Enforcement", "Europe"),
    FBI_NCIC("FBI / NCIC", "Federal National Crime Information Center", "North America"),
    CSTF_CYBER("CSTF", "Cyber Security Task Force", "Decentralized"),
    BORDER_MARITIME("BMS", "Border & Maritime Surveillance", "Atlantic-Pacific"),
    TOKYO_MPD("TMPD", "Metropolitan Police Special Division", "Asia-Pacific")
}

data class Suspect(
    val id: String,
    val name: String,
    val aliases: List<String>,
    val nationality: String,
    val age: Int,
    val threatLevel: ThreatLevel,
    val status: SuspectStatus,
    val primaryOffense: String,
    val jurisdiction: Jurisdiction,
    val activeWarrants: List<String>,
    val lastKnownLocation: String,
    val sensorTraceId: String,
    val associatedCaseNumber: String,
    val isFlagged: Boolean,
    val biometricConfidence: Int,
    val notes: String,
    val cryptoHash: String,
    val knownAssociates: List<String>
)
