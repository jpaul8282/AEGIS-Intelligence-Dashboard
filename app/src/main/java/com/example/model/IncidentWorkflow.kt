package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class WorkflowPhase(
    val phaseNumber: Int,
    val phaseName: String,
    val description: String,
    val themeColor: Color
) {
    DETECTION(
        phaseNumber = 1,
        phaseName = "AI ANOMALY DETECTION",
        description = "Neural heuristic telemetry scanners ingest multi-modal sensor streams and flag signature divergences.",
        themeColor = AlertCrimson
    ),
    INVESTIGATION(
        phaseNumber = 2,
        phaseName = "FEDERATED AI INVESTIGATION",
        description = "Automated cross-referencing across global law enforcement databases, cryptographic hashes, and satellite passes.",
        themeColor = CyanGlow
    ),
    CONTAINMENT(
        phaseNumber = 3,
        phaseName = "AUTONOMOUS CONTAINMENT",
        description = "Instantaneous non-destructive isolation: zero-trust network quarantine, RF jamming, and geofence perimeter lock.",
        themeColor = TacticalAmber
    ),
    TACTICAL_RESPONSE(
        phaseNumber = 4,
        phaseName = "EXECUTIVE TACTICAL DISPATCH",
        description = "Commander-authorized deployment of autonomous drones, intercept cutters, or rapid field response units.",
        themeColor = AlertViolet
    ),
    FORENSIC_AUDIT(
        phaseNumber = 5,
        phaseName = "IMMUTABLE FORENSIC AUDIT",
        description = "Cryptographic chain of custody sealed with SHA-256 tamper-proof evidence logs and post-incident analysis.",
        themeColor = TacticalEmerald
    )
}

data class IncidentScenario(
    val id: String,
    val title: String,
    val threatVector: String,
    val targetSuspect: String,
    val targetSuspectId: String,
    val sector: String,
    val sensorSource: String,
    val aiConfidencePercent: Int,
    val detectionTelemetry: String,
    val federatedIntelCorrelation: String,
    val autonomousCountermeasures: List<String>,
    val commanderAuthorizationAction: String,
    val neutralizationOutcome: String,
    val forensicLedgerHash: String
)
