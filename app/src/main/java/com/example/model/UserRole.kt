package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class UserRole(
    val title: String,
    val clearanceLevel: Int,
    val badgeColor: Color,
    val description: String,
    val allowedActions: List<String>
) {
    COMMANDER(
        title = "Executive Commander",
        clearanceLevel = 5,
        badgeColor = AlertViolet,
        description = "Unrestricted Executive Oversight, Global Dispatch, RBAC Authorization & Emergency Override.",
        allowedActions = listOf(
            "Global Jurisdiction Query",
            "Emergency Lockdown Protocol",
            "Tactical Unit & Drone Dispatch",
            "Modify Suspect Threat Matrix",
            "RBAC Role Escalation & Audit",
            "Raw API Gateway Telemetry Access"
        )
    ),
    LEAD_INVESTIGATOR(
        title = "Lead Investigator",
        clearanceLevel = 4,
        badgeColor = CyanGlow,
        description = "Case Dossier Authoring, Inter-Agency Evidence Graph, Suspect Warrants & Geofence Tracing.",
        allowedActions = listOf(
            "Global Jurisdiction Query",
            "Create & Edit Criminal Dossiers",
            "Issue Red Notices & Intercepts",
            "Geofence Surveillance Dispatch",
            "Evidence Chain Linkage"
        )
    ),
    FIELD_AGENT(
        title = "Field Operative",
        clearanceLevel = 3,
        badgeColor = TacticalAmber,
        description = "Tactical Sensor Deployment, Biometric Check-ins, Live Telemetry Stream & Local Warrants.",
        allowedActions = listOf(
            "View Local Assigned Dossiers",
            "Deploy Acoustic & Geofence Beacons",
            "Report Biometric Scan Hit",
            "Live Telemetry Ping Feed"
        )
    ),
    CYBER_ADMIN(
        title = "Systems & Cyber Architect",
        clearanceLevel = 4,
        badgeColor = TacticalEmerald,
        description = "Secure API Endpoints, JWT & HMAC Verification, Network Health & Intrusion Detection.",
        allowedActions = listOf(
            "API Gateway Sandbox & Testing",
            "Audit Token Revocations",
            "Perimeter Sensor Telemetry",
            "Inspect Latency & Rate Limits",
            "Cryptographic Key Management"
        )
    )
}

data class UserProfile(
    val id: String,
    val name: String,
    val callsign: String,
    val role: UserRole,
    val department: String,
    val sessionToken: String,
    val activeSince: String
)
