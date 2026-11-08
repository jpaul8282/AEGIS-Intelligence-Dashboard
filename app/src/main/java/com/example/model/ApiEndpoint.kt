package com.example.model

enum class ApiMethod { GET, POST, PUT, DELETE }

data class ApiEndpoint(
    val id: String,
    val method: ApiMethod,
    val path: String,
    val name: String,
    val description: String,
    val category: String, // Telemetry, Criminal DB, Alerts, Security
    val requiredClearance: Int,
    val avgLatencyMs: Int,
    val statusResponseCode: Int,
    val requestPayloadSnippet: String,
    val responsePayloadSnippet: String,
    val headers: Map<String, String> = mapOf(
        "Authorization" to "Bearer aegis_sec_jwt_v4.2",
        "X-Aegis-Signature" to "sha256=9b71d224bd62f3785d96d46ad3ea3d7331ec",
        "Content-Type" to "application/json",
        "X-Aegis-Node" to "node-eu-west1-prod"
    )
)
