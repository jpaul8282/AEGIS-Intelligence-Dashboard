package com.example.data

import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class EndpointTestResult(
    val endpointId: String,
    val statusCode: Int,
    val latencyMs: Long,
    val timestamp: String,
    val responseBody: String,
    val verifiedHeaders: Map<String, String>
)

data class SystemAnalytics(
    val threatMatrixScore: Int, // 0 - 100
    val activeThreatsCount: Int,
    val activeSensorsOnline: Int,
    val totalSensors: Int,
    val networkThroughputMbps: Float,
    val globalQueriesToday: Int,
    val systemIntegrityPercent: Int,
    val activeJurisdictionsCount: Int
)

class AegisRepository(private val scope: CoroutineScope) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    // Current Active User / RBAC profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "USR-001-EXEC",
            name = "Vanguard Chief J. Westerveld",
            callsign = "AEGIS-COMMAND-01",
            role = UserRole.COMMANDER,
            department = "Executive Tactical Directorate",
            sessionToken = "aegis_jwt_sec_9934afbc829e",
            activeSince = "06:00:00 UTC"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Suspects Criminal Database
    private val _suspects = MutableStateFlow<List<Suspect>>(initialSuspects())
    val suspects: StateFlow<List<Suspect>> = _suspects.asStateFlow()

    // Cases
    private val _cases = MutableStateFlow<List<InvestigationCase>>(initialCases())
    val cases: StateFlow<List<InvestigationCase>> = _cases.asStateFlow()

    // Sensor Nodes
    private val _sensors = MutableStateFlow<List<SensorNode>>(initialSensors())
    val sensors: StateFlow<List<SensorNode>> = _sensors.asStateFlow()

    // Alerts
    private val _alerts = MutableStateFlow<List<SystemAlert>>(initialAlerts())
    val alerts: StateFlow<List<SystemAlert>> = _alerts.asStateFlow()

    // API Endpoints
    private val _apiEndpoints = MutableStateFlow<List<ApiEndpoint>>(initialEndpoints())
    val apiEndpoints: StateFlow<List<ApiEndpoint>> = _apiEndpoints.asStateFlow()

    // Test logs for API Sandbox
    private val _apiTestLogs = MutableStateFlow<List<EndpointTestResult>>(emptyList())
    val apiTestLogs: StateFlow<List<EndpointTestResult>> = _apiTestLogs.asStateFlow()

    // Terrain Intelligence Zones (City Streets, Villages, Forests/Woods, Mountains)
    private val _terrainZones = MutableStateFlow<List<TerrainZone>>(initialTerrainZones())
    val terrainZones: StateFlow<List<TerrainZone>> = _terrainZones.asStateFlow()

    // Map Markers for categorized locations (Forest, Mountains, Urban, Village)
    private val _mapMarkers = MutableStateFlow<List<MapMarker>>(initialMapMarkers())
    val mapMarkers: StateFlow<List<MapMarker>> = _mapMarkers.asStateFlow()

    // Autonomous Incident Response Workflow
    private val _incidentScenarios = MutableStateFlow<List<IncidentScenario>>(initialScenarios())
    val incidentScenarios: StateFlow<List<IncidentScenario>> = _incidentScenarios.asStateFlow()

    private val _activeWorkflowScenario = MutableStateFlow(_incidentScenarios.value.first())
    val activeWorkflowScenario: StateFlow<IncidentScenario> = _activeWorkflowScenario.asStateFlow()

    private val _currentWorkflowPhase = MutableStateFlow(WorkflowPhase.DETECTION)
    val currentWorkflowPhase: StateFlow<WorkflowPhase> = _currentWorkflowPhase.asStateFlow()

    // System Analytics
    private val _analytics = MutableStateFlow(
        SystemAnalytics(
            threatMatrixScore = 78,
            activeThreatsCount = 4,
            activeSensorsOnline = 11,
            totalSensors = 12,
            networkThroughputMbps = 342.8f,
            globalQueriesToday = 1429,
            systemIntegrityPercent = 99,
            activeJurisdictionsCount = 6
        )
    )
    val analytics: StateFlow<SystemAnalytics> = _analytics.asStateFlow()

    init {
        startTelemetrySimulation()
    }

    private fun startTelemetrySimulation() {
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                // Pulse sensor telemetry
                _sensors.update { currentList ->
                    currentList.map { sensor ->
                        val randomDelta = (-3..4).random()
                        val newSignal = (sensor.signalStrength + randomDelta).coerceIn(40, 100)
                        val newPackets = (sensor.encryptedPacketsPerSec + (-12..15).random()).coerceAtLeast(10)
                        val newPing = if (sensor.status == SensorStatus.ONLINE) (1..4).random() else sensor.lastPingSecondsAgo + 3
                        sensor.copy(
                            signalStrength = newSignal,
                            encryptedPacketsPerSec = newPackets,
                            lastPingSecondsAgo = newPing
                        )
                    }
                }

                // Random small analytics throughput variation
                _analytics.update { old ->
                    val jitter = (-5..8).random() * 0.4f
                    val newThroughput = (old.networkThroughputMbps + jitter).coerceIn(280.0f, 490.0f)
                    val onlineCount = _sensors.value.count { it.status == SensorStatus.ONLINE }
                    val activeThreats = _suspects.value.count { it.threatLevel == ThreatLevel.ALPHA_EXTREME && it.status == SuspectStatus.AT_LARGE }
                    old.copy(
                        networkThroughputMbps = (Math.round(newThroughput * 10) / 10.0).toFloat(),
                        activeSensorsOnline = onlineCount,
                        activeThreatsCount = activeThreats,
                        globalQueriesToday = old.globalQueriesToday + (1..3).random()
                    )
                }
            }
        }
    }

    // Role-Based Access Control Actions
    fun switchRole(newRole: UserRole) {
        val (name, callsign, dept) = when (newRole) {
            UserRole.COMMANDER -> Triple("Vanguard Chief J. Westerveld", "AEGIS-COMMAND-01", "Executive Tactical Directorate")
            UserRole.LEAD_INVESTIGATOR -> Triple("Senior Inspector M. Vance", "INV-SPECTRE-09", "Special Criminal Investigations Unit")
            UserRole.FIELD_AGENT -> Triple("Field Operative T. Drake", "AGENT-GHOST-42", "Tactical Rapid Intercept Squad")
            UserRole.CYBER_ADMIN -> Triple("Architect K. Sorenson", "SYS-SENTINEL-00", "Cyber Defense & API Infrastructure")
        }
        _currentUser.value = UserProfile(
            id = "USR-${newRole.clearanceLevel}-${newRole.name.take(4)}",
            name = name,
            callsign = callsign,
            role = newRole,
            department = dept,
            sessionToken = "aegis_jwt_sec_${UUID.randomUUID().toString().take(12)}",
            activeSince = timeFormat.format(Date()) + " UTC"
        )
    }

    // Suspect Actions
    fun toggleSuspectFlag(suspectId: String) {
        _suspects.update { list ->
            list.map {
                if (it.id == suspectId) it.copy(isFlagged = !it.isFlagged) else it
            }
        }
    }

    fun updateSuspectStatus(suspectId: String, newStatus: SuspectStatus) {
        _suspects.update { list ->
            list.map {
                if (it.id == suspectId) it.copy(status = newStatus) else it
            }
        }
    }

    fun addSuspect(suspect: Suspect) {
        _suspects.update { listOf(suspect) + it }
    }

    // Alert Actions
    fun resolveAlert(alertId: String, actionTaken: String) {
        _alerts.update { list ->
            list.map {
                if (it.id == alertId) it.copy(isResolved = true, actionTaken = actionTaken) else it
            }
        }
    }

    fun dismissAlert(alertId: String) {
        _alerts.update { it.filterNot { alert -> alert.id == alertId } }
    }

    fun executeActionableDispatch(alertId: String, action: String) {
        resolveAlert(alertId, "DISPATCHED: $action at ${timeFormat.format(Date())}")
        // Also simulate an active sensor response
        _sensors.update { list ->
            list.map {
                if (it.status == SensorStatus.ALERTING) it.copy(status = SensorStatus.ONLINE) else it
            }
        }
    }

    fun pingSensor(sensorId: String) {
        _sensors.update { list ->
            list.map {
                if (it.id == sensorId) it.copy(lastPingSecondsAgo = 0, status = SensorStatus.ONLINE) else it
            }
        }
    }

    // API Testing Sandbox
    fun testApiEndpoint(endpoint: ApiEndpoint, customBody: String?): EndpointTestResult {
        val latency = (endpoint.avgLatencyMs + (-14..22).random()).coerceAtLeast(12).toLong()
        val isPermitted = _currentUser.value.role.clearanceLevel >= endpoint.requiredClearance
        val code = if (isPermitted) endpoint.statusResponseCode else 403

        val responseBody = if (isPermitted) {
            endpoint.responsePayloadSnippet
        } else {
            """{"error": "FORBIDDEN_CLEARANCE_INSUFFICIENT", "required_level": ${endpoint.requiredClearance}, "current_level": ${_currentUser.value.role.clearanceLevel}}"""
        }

        val result = EndpointTestResult(
            endpointId = endpoint.id,
            statusCode = code,
            latencyMs = latency,
            timestamp = timeFormat.format(Date()),
            responseBody = responseBody,
            verifiedHeaders = mapOf(
                "X-Aegis-Gateway" to "TLS 1.3 / ChaCha20-Poly1305",
                "X-Aegis-Signature" to "sha256=verified",
                "X-RateLimit-Remaining" to "998/1000",
                "X-Clearance-Level" to "${_currentUser.value.role.clearanceLevel}"
            )
        )
        _apiTestLogs.update { listOf(result) + it.take(19) }
        return result
    }

    fun updateCaseProgress(caseId: String, delta: Int) {
        _cases.update { list ->
            list.map {
                if (it.id == caseId) {
                    val newProgress = (it.progressPercentage + delta).coerceIn(0, 100)
                    it.copy(
                        progressPercentage = newProgress,
                        status = if (newProgress == 100) "Closed" else if (newProgress > 70) "Escalated" else "Active"
                    )
                } else it
            }
        }
    }

    fun deployOverwatchToZone(zoneId: String, sensorName: String) {
        _terrainZones.update { list ->
            list.map {
                if (it.id == zoneId) it.copy(activeSensors = it.activeSensors + sensorName) else it
            }
        }
    }

    fun deployDroneToMarker(markerId: String) {
        _mapMarkers.update { list ->
            list.map {
                if (it.id == markerId) it.copy(
                    status = MarkerOperationalStatus.SURVEILLANCE_ACTIVE,
                    notes = it.notes + " [FLIR Recon Drone En Route]"
                ) else it
            }
        }
    }

    fun deploySensorToMarker(markerId: String, sensorName: String) {
        _mapMarkers.update { list ->
            list.map {
                if (it.id == markerId) it.copy(sensorDevices = it.sensorDevices + sensorName) else it
            }
        }
    }

    fun setWorkflowScenario(scenario: IncidentScenario) {
        _activeWorkflowScenario.value = scenario
        _currentWorkflowPhase.value = WorkflowPhase.DETECTION
    }

    fun setWorkflowPhase(phase: WorkflowPhase) {
        _currentWorkflowPhase.value = phase
    }

    fun advanceWorkflowPhase() {
        val nextOrdinal = (_currentWorkflowPhase.value.ordinal + 1).coerceAtMost(WorkflowPhase.entries.size - 1)
        _currentWorkflowPhase.value = WorkflowPhase.entries[nextOrdinal]
    }

    fun resetWorkflow() {
        _currentWorkflowPhase.value = WorkflowPhase.DETECTION
    }

    companion object {
        private fun initialSuspects(): List<Suspect> = listOf(
            Suspect(
                id = "SUSP-7749",
                name = "Viktor 'Zero-Day' Sterling",
                aliases = listOf("SpectreByte", "The Glitch", "Baron V"),
                nationality = "Estonia / Stateless",
                age = 39,
                threatLevel = ThreatLevel.ALPHA_EXTREME,
                status = SuspectStatus.AT_LARGE,
                primaryOffense = "Critical Power Grid Cyber Sabotage & Ransom Extortion",
                jurisdiction = Jurisdiction.CSTF_CYBER,
                activeWarrants = listOf("INTERPOL Red Notice #A-8912/4", "Europol High Priority Cyber W-402"),
                lastKnownLocation = "Sector Omega-4, Rotterdam Port Facility",
                sensorTraceId = "TRACE-NODE-CYBER-09",
                associatedCaseNumber = "CASE-2026-CHIMERA",
                isFlagged = true,
                biometricConfidence = 96,
                notes = "Known master of zero-day exploits targeting SCADA hardware. Traveled on forged diplomatic passport.",
                cryptoHash = "0x89f4b...32e1 (Tether Monitored)",
                knownAssociates = listOf("Elena Rostova", "Klaus Brandt")
            ),
            Suspect(
                id = "SUSP-8821",
                name = "Elena 'Cobra' Rostova",
                aliases = listOf("Black Lotus", "Helena Fox"),
                nationality = "Russia",
                age = 34,
                threatLevel = ThreatLevel.BRAVO_HIGH,
                status = SuspectStatus.SURVEILLANCE_ACTIVE,
                primaryOffense = "Illicit Arms Brokerage & Quantum Encryption Theft",
                jurisdiction = Jurisdiction.INTERPOL,
                activeWarrants = listOf("INTERPOL Red Notice #B-4410", "TMPD International Arrest Order"),
                lastKnownLocation = "Tokyo Shinjuku Special Ward, 35.6895° N, 139.6917° E",
                sensorTraceId = "TRACE-BIO-TOKYO-03",
                associatedCaseNumber = "CASE-2026-CHIMERA",
                isFlagged = true,
                biometricConfidence = 91,
                notes = "Monitored moving through terminal via biometric facial recognition camera #12. Discrete tail active.",
                cryptoHash = "bc1qar...980f (Multi-sig cold storage)",
                knownAssociates = listOf("Viktor Sterling", "Darius Thorne")
            ),
            Suspect(
                id = "SUSP-9043",
                name = "Darius 'Apex' Thorne",
                aliases = listOf("Ghost Investor", "The Broker"),
                nationality = "United Kingdom",
                age = 48,
                threatLevel = ThreatLevel.BRAVO_HIGH,
                status = SuspectStatus.INTERCEPT_INBOUND,
                primaryOffense = "Sovereign Bond Laundering & Maritime Smuggling Syndicate",
                jurisdiction = Jurisdiction.BORDER_MARITIME,
                activeWarrants = listOf("Europol EIS Warrant E-9912", "BMS Maritime Seizure Authorization"),
                lastKnownLocation = "North Sea Corridor, Vessel M/V Poseidon Horizon",
                sensorTraceId = "TRACE-SAT-NORTH-01",
                associatedCaseNumber = "CASE-2026-GHOST-CARGO",
                isFlagged = false,
                biometricConfidence = 88,
                notes = "Satellite radar tracking cargo vessel carrying flagged radio beacons. Rapid intercept cutter inbound.",
                cryptoHash = "0x77ab...ff91 (Decentralized Liquidity Pool)",
                knownAssociates = listOf("Elena Rostova")
            ),
            Suspect(
                id = "SUSP-6120",
                name = "Malik 'Cipher' Al-Mansoor",
                aliases = listOf("Phantom Wire", "Al-Khatib"),
                nationality = "UAE",
                age = 42,
                threatLevel = ThreatLevel.CHARLIE_MODERATE,
                status = SuspectStatus.SURVEILLANCE_ACTIVE,
                primaryOffense = "Darknet Signal Interception & Corporate Espionage",
                jurisdiction = Jurisdiction.FBI_NCIC,
                activeWarrants = listOf("FBI Wanted Circular #W-2940"),
                lastKnownLocation = "Manhattan Financial District, New York",
                sensorTraceId = "TRACE-ACOUSTIC-NYC-07",
                associatedCaseNumber = "CASE-2026-QUANTUM",
                isFlagged = false,
                biometricConfidence = 84,
                notes = "Acoustic sensor picked up encrypted microwave relay transmission matching his signature.",
                cryptoHash = "0x3344...bb78 (Lightning Network)",
                knownAssociates = listOf("Klaus Brandt")
            ),
            Suspect(
                id = "SUSP-3312",
                name = "Klaus 'Hydra' Brandt",
                aliases = listOf("The Chemist", "Dr. B"),
                nationality = "Germany",
                age = 51,
                threatLevel = ThreatLevel.ALPHA_EXTREME,
                status = SuspectStatus.AT_LARGE,
                primaryOffense = "Synthetic Precursor Trafficking & Port Authority Bribery",
                jurisdiction = Jurisdiction.EUROPOL,
                activeWarrants = listOf("Europol Priority Warrant #E-1004", "BKA Federal Notice"),
                lastKnownLocation = "Hamburg Industrial Free Trade Zone",
                sensorTraceId = "TRACE-DRONE-HAMBURG-02",
                associatedCaseNumber = "CASE-2026-GHOST-CARGO",
                isFlagged = true,
                biometricConfidence = 94,
                notes = "Patrol drone spotted vehicle linked to suspect's shell company near Warehouse B-14.",
                cryptoHash = "0x1122...cc99 (Tornado Cash Mixer Output)",
                knownAssociates = listOf("Viktor Sterling", "Malik Al-Mansoor")
            ),
            Suspect(
                id = "SUSP-1099",
                name = "Renata 'Viper' Silva",
                aliases = listOf("Queenpin", "La Sombra"),
                nationality = "Brazil",
                age = 36,
                threatLevel = ThreatLevel.DELTA_LOW,
                status = SuspectStatus.APPREHENDED,
                primaryOffense = "International Identity Forgery & Forged Credentials Ring",
                jurisdiction = Jurisdiction.INTERPOL,
                activeWarrants = listOf("Extradition Pending - INTERPOL Red Notice #A-1102"),
                lastKnownLocation = "Federal High-Security Detention Center Lisbon",
                sensorTraceId = "TRACE-DETENTION-01",
                associatedCaseNumber = "CASE-2025-FORGERY",
                isFlagged = false,
                biometricConfidence = 99,
                notes = "Captured in Lisbon airport. Currently providing intelligence on European passport forging pipeline.",
                cryptoHash = "0x5566...dd00 (Frozen by Asset Forfeiture)",
                knownAssociates = listOf("Viktor Sterling")
            )
        )

        private fun initialCases(): List<InvestigationCase> = listOf(
            InvestigationCase(
                id = "CASE-01",
                caseNumber = "CASE-2026-CHIMERA",
                title = "Operation Chimera: Continental Grid Sabotage",
                leadInvestigator = "Senior Inspector M. Vance",
                jurisdiction = Jurisdiction.CSTF_CYBER,
                status = "Active",
                progressPercentage = 68,
                suspectIds = listOf("SUSP-7749", "SUSP-8821"),
                evidenceCount = 27,
                synopsis = "Cross-border investigation into state-sponsored SCADA penetrations and energy sub-station breaches.",
                openedDate = "2026-08-14",
                crossAgencyCooperation = listOf("INTERPOL", "CSTF", "BKA Germany", "CISA")
            ),
            InvestigationCase(
                id = "CASE-02",
                caseNumber = "CASE-2026-GHOST-CARGO",
                title = "Operation Ghost Horizon: Maritime Syndicate",
                leadInvestigator = "Captain R. Thorne",
                jurisdiction = Jurisdiction.BORDER_MARITIME,
                status = "Escalated",
                progressPercentage = 84,
                suspectIds = listOf("SUSP-9043", "SUSP-3312"),
                evidenceCount = 42,
                synopsis = "Tracking automated decoy cargo freighters ferrying undeclared technological precursors through international waters.",
                openedDate = "2026-09-02",
                crossAgencyCooperation = listOf("BMS", "Europol", "UK Border Force")
            ),
            InvestigationCase(
                id = "CASE-03",
                caseNumber = "CASE-2026-QUANTUM",
                title = "Project Quantum Shield: Financial Subversion",
                leadInvestigator = "Special Agent Sarah Lin",
                jurisdiction = Jurisdiction.FBI_NCIC,
                status = "Active",
                progressPercentage = 45,
                suspectIds = listOf("SUSP-6120"),
                evidenceCount = 18,
                synopsis = "Forensic analysis of high-frequency quantum relay spoofing attacking central banking settlement networks.",
                openedDate = "2026-10-10",
                crossAgencyCooperation = listOf("FBI", "Secret Service", "Monetary Authority")
            )
        )

        private fun initialSensors(): List<SensorNode> = listOf(
            SensorNode(
                id = "SN-BIO-01",
                name = "Terminal Bio-Cam 01",
                sector = "Sector Alpha - Airport Gate 4",
                type = SensorType.BIOMETRIC_SCANNER,
                status = SensorStatus.ONLINE,
                valueMetric = "Facial Vector Match 98.4%",
                signalStrength = 92,
                batteryPercent = 100,
                lastPingSecondsAgo = 2,
                xPosRatio = 0.25f,
                yPosRatio = 0.35f,
                encryptedPacketsPerSec = 148
            ),
            SensorNode(
                id = "SN-CYBER-09",
                name = "Gateway Firewall Node 09",
                sector = "Sector Omega - Core Substation",
                type = SensorType.CYBER_PERIMETER,
                status = SensorStatus.ALERTING,
                valueMetric = "SYN Flood & Zero-Day Probe",
                signalStrength = 88,
                batteryPercent = 98,
                lastPingSecondsAgo = 1,
                xPosRatio = 0.62f,
                yPosRatio = 0.28f,
                encryptedPacketsPerSec = 520
            ),
            SensorNode(
                id = "SN-SAT-02",
                name = "Orbital Recon Sentinel-4",
                sector = "Sector Maritime - North Sea",
                type = SensorType.SATELLITE_THERMAL,
                status = SensorStatus.ONLINE,
                valueMetric = "Thermal Wake 24.2 Knots",
                signalStrength = 96,
                batteryPercent = 94,
                lastPingSecondsAgo = 3,
                xPosRatio = 0.48f,
                yPosRatio = 0.22f,
                encryptedPacketsPerSec = 310
            ),
            SensorNode(
                id = "SN-DRONE-03",
                name = "Patrol Hawk Drone Alpha",
                sector = "Sector Beta - Port Perimeter",
                type = SensorType.DRONE_RECON,
                status = SensorStatus.ONLINE,
                valueMetric = "FLIR Optical Lock Active",
                signalStrength = 84,
                batteryPercent = 76,
                lastPingSecondsAgo = 1,
                xPosRatio = 0.38f,
                yPosRatio = 0.58f,
                encryptedPacketsPerSec = 220
            ),
            SensorNode(
                id = "SN-ACOU-07",
                name = "Seismic Perimeter Array",
                sector = "Sector Delta - Vault Facility",
                type = SensorType.ACOUSTIC_ARRAY,
                status = SensorStatus.ONLINE,
                valueMetric = "Acoustic Decibel Baseline: Normal",
                signalStrength = 79,
                batteryPercent = 89,
                lastPingSecondsAgo = 4,
                xPosRatio = 0.72f,
                yPosRatio = 0.65f,
                encryptedPacketsPerSec = 95
            ),
            SensorNode(
                id = "SN-GEO-12",
                name = "RF Geofence Transponder 12",
                sector = "Sector Gamma - Diplomatic Enclave",
                type = SensorType.GEOFENCE_BEACON,
                status = SensorStatus.ONLINE,
                valueMetric = "Boundary Integrity: Sealed",
                signalStrength = 95,
                batteryPercent = 85,
                lastPingSecondsAgo = 2,
                xPosRatio = 0.82f,
                yPosRatio = 0.45f,
                encryptedPacketsPerSec = 175
            )
        )

        private fun initialAlerts(): List<SystemAlert> = listOf(
            SystemAlert(
                id = "ALT-9041",
                title = "SCADA Intrusion Attempt Flagged",
                description = "Zero-day exploit buffer overflow detected on Substation Node 09. Source matches suspect Viktor Sterling's signature.",
                severity = AlertSeverity.CRITICAL,
                timestamp = "10:48:12",
                sector = "Sector Omega - Core Substation",
                sourceSensorId = "SN-CYBER-09",
                targetSuspectId = "SUSP-7749",
                isResolved = false
            ),
            SystemAlert(
                id = "ALT-8832",
                title = "Biometric Match: Elena Rostova",
                description = "Facial recognition hit at Terminal Gate 4 (Confidence: 91.2%). Cross-referenced with INTERPOL Red Notice.",
                severity = AlertSeverity.HIGH,
                timestamp = "10:35:40",
                sector = "Sector Alpha - Airport Gate 4",
                sourceSensorId = "SN-BIO-01",
                targetSuspectId = "SUSP-8821",
                isResolved = false
            ),
            SystemAlert(
                id = "ALT-7619",
                title = "Vessel Deviation in Maritime Corridor",
                description = "M/V Poseidon Horizon altered course without clearance. Suspicion of transshipment rendezvous.",
                severity = AlertSeverity.MEDIUM,
                timestamp = "10:12:05",
                sector = "Sector Maritime - North Sea",
                sourceSensorId = "SN-SAT-02",
                targetSuspectId = "SUSP-9043",
                isResolved = true,
                actionTaken = "Coast Guard Cutter dispatched for boarding inspection."
            )
        )

        private fun initialEndpoints(): List<ApiEndpoint> = listOf(
            ApiEndpoint(
                id = "EP-01",
                method = ApiMethod.GET,
                path = "/api/v1/telemetry/sensors",
                name = "Live Sensor Telemetry Stream",
                description = "Streams real-time sensor packets, RSSI signal metrics, battery health, and geo-coordinates across deployed grid sectors.",
                category = "Telemetry",
                requiredClearance = 3,
                avgLatencyMs = 28,
                statusResponseCode = 200,
                requestPayloadSnippet = "{\n  \"sector_filter\": \"all\",\n  \"sample_rate_hz\": 10,\n  \"include_encrypted_hash\": true\n}",
                responsePayloadSnippet = "{\n  \"status\": \"SUCCESS\",\n  \"active_nodes\": 6,\n  \"throughput_mbps\": 342.8,\n  \"timestamp\": \"2026-11-04T10:50:00Z\",\n  \"telemetry_batch\": [\n    {\"id\": \"SN-BIO-01\", \"type\": \"BIOMETRIC\", \"status\": \"ONLINE\", \"rssi\": -54},\n    {\"id\": \"SN-CYBER-09\", \"type\": \"CYBER_NODE\", \"status\": \"ALERTING\", \"threat\": \"ZERO_DAY\"}\n  ]\n}"
            ),
            ApiEndpoint(
                id = "EP-02",
                method = ApiMethod.POST,
                path = "/api/v1/intelligence/suspects/query",
                name = "Multi-Jurisdiction Criminal Query",
                description = "Performs high-speed federated query across INTERPOL, Europol, FBI/NCIC, and CSTF with fuzzy biometric and alias matching.",
                category = "Criminal DB",
                requiredClearance = 4,
                avgLatencyMs = 64,
                statusResponseCode = 200,
                requestPayloadSnippet = "{\n  \"query\": \"Viktor Sterling\",\n  \"jurisdictions\": [\"INTERPOL\", \"EUROPOL\", \"CSTF\"],\n  \"biometric_vector\": \"[0.892, 0.114, -0.452...]\",\n  \"threat_threshold\": \"BRAVO_HIGH\"\n}",
                responsePayloadSnippet = "{\n  \"matches\": 1,\n  \"records\": [\n    {\n      \"suspect_id\": \"SUSP-7749\",\n      \"name\": \"Viktor Sterling\",\n      \"status\": \"AT_LARGE\",\n      \"warrants\": [\"INTERPOL Red Notice #A-8912/4\"],\n      \"confidence\": 0.96\n    }\n  ],\n  \"query_time_ms\": 62\n}"
            ),
            ApiEndpoint(
                id = "EP-03",
                method = ApiMethod.GET,
                path = "/api/v1/cases/{caseId}/dossier",
                name = "Investigation Case Dossier",
                description = "Retrieves complete investigation evidence chain, forensic timeline, cross-agency reports, and associated syndicate graphs.",
                category = "Criminal DB",
                requiredClearance = 4,
                avgLatencyMs = 45,
                statusResponseCode = 200,
                requestPayloadSnippet = "{\n  \"case_id\": \"CASE-2026-CHIMERA\",\n  \"include_evidence_vault\": true\n}",
                responsePayloadSnippet = "{\n  \"case_number\": \"CASE-2026-CHIMERA\",\n  \"title\": \"Operation Chimera: Continental Grid Sabotage\",\n  \"evidence_count\": 27,\n  \"progress\": \"68%\",\n  \"lead_agency\": \"CSTF\",\n  \"integrity_status\": \"VERIFIED_CHAIN_OF_CUSTODY\"\n}"
            ),
            ApiEndpoint(
                id = "EP-04",
                method = ApiMethod.POST,
                path = "/api/v1/alerts/dispatch",
                name = "Tactical Unit & Drone Dispatch",
                description = "Dispatches immediate physical/drone tactical response, initiates sector lockdown, or issues emergency broadcast to field agents.",
                category = "Alerts",
                requiredClearance = 5,
                avgLatencyMs = 82,
                statusResponseCode = 201,
                requestPayloadSnippet = "{\n  \"alert_id\": \"ALT-9041\",\n  \"dispatch_type\": \"AUTONOMOUS_DRONE_INTERCEPT\",\n  \"sector\": \"Sector Omega-4\",\n  \"rules_of_engagement\": \"DEFENSIVE_CONTAINMENT\",\n  \"commander_override\": true\n}",
                responsePayloadSnippet = "{\n  \"dispatch_id\": \"DISP-9921\",\n  \"status\": \"DEPLOYED\",\n  \"eta_seconds\": 45,\n  \"assigned_unit\": \"Drone Alpha & Rapid Strike Team 3\",\n  \"timestamp\": \"2026-11-04T10:52:10Z\"\n}"
            ),
            ApiEndpoint(
                id = "EP-05",
                method = ApiMethod.POST,
                path = "/api/v1/auth/token",
                name = "Secure Token Auth & RBAC Exchange",
                description = "Authenticates executive credentials via hardware security key, mints ephemeral HMAC-SHA256 bearer tokens with strict TTL.",
                category = "Security",
                requiredClearance = 3,
                avgLatencyMs = 35,
                statusResponseCode = 200,
                requestPayloadSnippet = "{\n  \"clearance_claim\": \"LEVEL_5\",\n  \"hardware_id\": \"AEGIS-HSM-902\",\n  \"mfa_totp\": \"892104\"\n}",
                responsePayloadSnippet = "{\n  \"token_type\": \"Bearer\",\n  \"access_token\": \"aegis_jwt_sec_9934afbc829e\",\n  \"expires_in\": 3600,\n  \"clearance_granted\": \"LEVEL_5_COMMANDER\",\n  \"tls_cipher\": \"TLS_AES_256_GCM_SHA384\"\n}"
            ),
            ApiEndpoint(
                id = "EP-06",
                method = ApiMethod.GET,
                path = "/api/v1/analytics/system",
                name = "Executive System & Threat Matrix Analytics",
                description = "Aggregates global threat index, server-side CPU/memory metrics, API gateway latency, and real-time incident velocities.",
                category = "Telemetry",
                requiredClearance = 3,
                avgLatencyMs = 19,
                statusResponseCode = 200,
                requestPayloadSnippet = "{\n  \"window\": \"1h\",\n  \"resolution\": \"10s\"\n}",
                responsePayloadSnippet = "{\n  \"threat_matrix_score\": 78,\n  \"active_threats\": 4,\n  \"system_health\": \"OPTIMAL\",\n  \"gateway_uptime\": \"99.998%\",\n  \"jurisdictions_federated\": 6\n}"
            )
        )

        private fun initialTerrainZones(): List<TerrainZone> = listOf(
            TerrainZone(
                id = "TZ-URBAN-07",
                name = "Sector-7 Metropolitan Grid & Streets",
                type = TerrainType.CITY_STREETS,
                sectorCode = "SEC-07-URBAN",
                coordinates = "46.123°N, 10.456°E",
                elevationMeters = 140,
                signalAttenuationDb = 4,
                concealmentRating = 35,
                activeSensors = listOf("Traffic Bio-Cam 101", "Subway Terminal Gate 4", "Gateway Cyber Node 09"),
                suspectedTargets = listOf("Elena Rostova (SUSP-8821)"),
                tacticalGuidance = "High-density street and alleyway intersections. Multi-path reflections present. Subterranean transit subway network offers rapid escape vector; recommend choke-point monitoring at transit exits.",
                xRatio = 0.28f,
                yRatio = 0.65f
            ),
            TerrainZone(
                id = "TZ-VILLAGE-A",
                name = "Village Zone Alpha & Rural Hamlets",
                type = TerrainType.VILLAGE_SETTLEMENT,
                sectorCode = "SEC-VILLAGE-A",
                coordinates = "46.155°N, 10.490°E",
                elevationMeters = 420,
                signalAttenuationDb = 8,
                concealmentRating = 55,
                activeSensors = listOf("RF Geofence Transponder 12", "Border Outpost Unit 3"),
                suspectedTargets = listOf("Darius Thorne (SUSP-9043)"),
                tacticalGuidance = "Rural agrarian settlements with gravel access roads. Suspect vehicles easily identified against sparse background traffic. Deploy perimeter vehicle checkpoints along secondary farm arteries.",
                xRatio = 0.38f,
                yRatio = 0.30f
            ),
            TerrainZone(
                id = "TZ-FOREST-GW",
                name = "Greenwood Forest Reserve & Deep Woods",
                type = TerrainType.FOREST_WOODS,
                sectorCode = "SEC-FOREST-GW",
                coordinates = "46.172°N, 10.518°E",
                elevationMeters = 890,
                signalAttenuationDb = 22,
                concealmentRating = 85,
                activeSensors = listOf("Seismic Acoustic Array", "Patrol Hawk Drone Alpha (FLIR Thermal)"),
                suspectedTargets = listOf("Viktor Sterling (SUSP-7749)"),
                tacticalGuidance = "Dense spruce and deciduous tree canopy impedes standard optical daylight cameras. Target leverages clandestine logging trails. Prioritize forward-looking infrared (FLIR) thermal scans and acoustic tripwires.",
                xRatio = 0.58f,
                yRatio = 0.52f
            ),
            TerrainZone(
                id = "TZ-MTN-KAISER",
                name = "Kaiser Mountain Alpine Ridge & Peaks",
                type = TerrainType.MOUNTAIN_RANGE,
                sectorCode = "SEC-MTN-KAISER",
                coordinates = "46.210°N, 10.590°E",
                elevationMeters = 2450,
                signalAttenuationDb = 14,
                concealmentRating = 70,
                activeSensors = listOf("Orbital Recon Sentinel-4 (SAR)", "Alpine Ridge Observation Post WP-3"),
                suspectedTargets = listOf("Klaus Brandt (SUSP-3312)"),
                tacticalGuidance = "Severe alpine terrain with snow-covered crevasses and sub-zero temperatures. Steep mountain ridges create radar shadows in eastern valleys. Synthetic Aperture Radar (SAR) overwatch deployed.",
                xRatio = 0.72f,
                yRatio = 0.38f
            )
        )

        private fun initialMapMarkers(): List<MapMarker> = listOf(
            // Forest & Woods Markers
            MapMarker(
                id = "MK-FOR-01",
                name = "Greenwood Old Pines (WP-1)",
                category = LocationCategory.FOREST,
                coordinates = "46.172°N, 10.518°E",
                latitude = 46.172,
                longitude = 10.518,
                elevationMeters = 890,
                xRatio = 0.43f,
                yRatio = 0.53f,
                status = MarkerOperationalStatus.THREAT_DETECTED,
                threatRating = ThreatLevel.ALPHA_EXTREME,
                coverageRadiusKm = 3.5f,
                telemetryReadout = "Canopy: 88% • FLIR Target Lock",
                keySuspectTracked = "Viktor Sterling (SUSP-7749)",
                sensorDevices = listOf("Seismic Acoustic Array", "Drone Hawk-01"),
                notes = "Clandestine logging trail rendezvous point. Optical daylight blocked by dense evergreen canopy; thermal tracking active."
            ),
            MapMarker(
                id = "MK-FOR-02",
                name = "Whispering Woods Ravine",
                category = LocationCategory.FOREST,
                coordinates = "46.165°N, 10.540°E",
                latitude = 46.165,
                longitude = 10.540,
                elevationMeters = 720,
                xRatio = 0.62f,
                yRatio = 0.58f,
                status = MarkerOperationalStatus.SURVEILLANCE_ACTIVE,
                threatRating = ThreatLevel.BRAVO_HIGH,
                coverageRadiusKm = 2.8f,
                telemetryReadout = "Acoustic Decibel: 16dB • Silent",
                keySuspectTracked = null,
                sensorDevices = listOf("RF Geofence Node 11"),
                notes = "Deep creek bed canyon offering natural thermal shielding. Tripwire sensors armed."
            ),

            // Mountains & Peaks Markers
            MapMarker(
                id = "MK-MTN-01",
                name = "Kaiser Alpine Summit (WP-3)",
                category = LocationCategory.MOUNTAINS,
                coordinates = "46.210°N, 10.590°E",
                latitude = 46.210,
                longitude = 10.590,
                elevationMeters = 2450,
                xRatio = 0.58f,
                yRatio = 0.38f,
                status = MarkerOperationalStatus.THREAT_DETECTED,
                threatRating = ThreatLevel.ALPHA_EXTREME,
                coverageRadiusKm = 8.0f,
                telemetryReadout = "Elevation: 2,450m • Snowpack: 40cm",
                keySuspectTracked = "Klaus Brandt (SUSP-3312)",
                sensorDevices = listOf("Orbital Recon Sentinel-4 (SAR)", "High-Altitude Relay"),
                notes = "Alpine observation post overlooking northern glacier pass. Suspect vehicle track found near cabin shelter."
            ),
            MapMarker(
                id = "MK-MTN-02",
                name = "Glacier Ridge Lookout",
                category = LocationCategory.MOUNTAINS,
                coordinates = "46.230°N, 10.630°E",
                latitude = 46.230,
                longitude = 10.630,
                elevationMeters = 2780,
                xRatio = 0.76f,
                yRatio = 0.26f,
                status = MarkerOperationalStatus.SURVEILLANCE_ACTIVE,
                threatRating = ThreatLevel.CHARLIE_MODERATE,
                coverageRadiusKm = 12.0f,
                telemetryReadout = "Wind: 42 kts • Radar LOS: 96%",
                keySuspectTracked = null,
                sensorDevices = listOf("Autonomous Weather Relay"),
                notes = "High-altitude crest providing unobstructed synthetic aperture radar coverage across eastern frontier."
            ),

            // Urban & City Streets Markers
            MapMarker(
                id = "MK-URB-01",
                name = "Sector-7 Downtown Plaza & Streets",
                category = LocationCategory.URBAN,
                coordinates = "46.123°N, 10.456°E",
                latitude = 46.123,
                longitude = 10.456,
                elevationMeters = 140,
                xRatio = 0.24f,
                yRatio = 0.70f,
                status = MarkerOperationalStatus.SURVEILLANCE_ACTIVE,
                threatRating = ThreatLevel.BRAVO_HIGH,
                coverageRadiusKm = 1.5f,
                telemetryReadout = "Pedestrian Flow: 420/min • 4K Optical",
                keySuspectTracked = "Elena Rostova (SUSP-8821)",
                sensorDevices = listOf("Traffic Bio-Cam 101", "ANPR Street Net"),
                notes = "High-density street intersection with multi-camera facial vector tracking. Discrete field agent tail active."
            ),
            MapMarker(
                id = "MK-URB-02",
                name = "Subway Terminal Gate 4",
                category = LocationCategory.URBAN,
                coordinates = "46.120°N, 10.468°E",
                latitude = 46.120,
                longitude = 10.468,
                elevationMeters = 125,
                xRatio = 0.28f,
                yRatio = 0.78f,
                status = MarkerOperationalStatus.SECURE,
                threatRating = ThreatLevel.DELTA_LOW,
                coverageRadiusKm = 0.8f,
                telemetryReadout = "Subterranean Network: 100% Online",
                keySuspectTracked = null,
                sensorDevices = listOf("Terminal Bio-Cam 01"),
                notes = "Automated transit hub turnstiles cross-referencing INTERPOL Red Notices."
            ),

            // Villages & Hamlets Markers
            MapMarker(
                id = "MK-VIL-01",
                name = "Village Zone Alpha Outpost",
                category = LocationCategory.VILLAGE,
                coordinates = "46.155°N, 10.490°E",
                latitude = 46.155,
                longitude = 10.490,
                elevationMeters = 420,
                xRatio = 0.34f,
                yRatio = 0.30f,
                status = MarkerOperationalStatus.SURVEILLANCE_ACTIVE,
                threatRating = ThreatLevel.BRAVO_HIGH,
                coverageRadiusKm = 2.2f,
                telemetryReadout = "Vehicle Speed: 34 km/h • Gravel Road",
                keySuspectTracked = "Darius Thorne (SUSP-9043)",
                sensorDevices = listOf("RF Geofence Transponder 12"),
                notes = "Rural agrarian outpost near bridge crossing. Suspect transponder pinged 12 minutes ago."
            ),
            MapMarker(
                id = "MK-VIL-02",
                name = "South Hamlet Agricultural Checkpoint",
                category = LocationCategory.VILLAGE,
                coordinates = "46.142°N, 10.510°E",
                latitude = 46.142,
                longitude = 10.510,
                elevationMeters = 380,
                xRatio = 0.56f,
                yRatio = 0.72f,
                status = MarkerOperationalStatus.SECURE,
                threatRating = ThreatLevel.DELTA_LOW,
                coverageRadiusKm = 1.8f,
                telemetryReadout = "Checkpoint Gate: Operational",
                keySuspectTracked = null,
                sensorDevices = listOf("Border Outpost Unit 3"),
                notes = "Agricultural perimeter barrier with automated plate recognition and local informant liaison."
            )
        )

        private fun initialScenarios(): List<IncidentScenario> = listOf(
            IncidentScenario(
                id = "SCEN-01-SCADA",
                title = "Critical Grid SCADA Zero-Day Exploit",
                threatVector = "Distributed SYN Flood + PLC Buffer Overflow targeting Continental Substation",
                targetSuspect = "Viktor 'Zero-Day' Sterling",
                targetSuspectId = "SUSP-7749",
                sector = "Sector Omega - Core Substation",
                sensorSource = "Gateway Firewall Node 09 (SN-CYBER-09)",
                aiConfidencePercent = 98,
                detectionTelemetry = "Anomaly Engine flagged 520 encrypted packets/sec with zero-day exploit payload signatures targeting port 502 (Modbus). Anomaly delta +420% above 30-day baseline.",
                federatedIntelCorrelation = "Inter-Agency AI query correlated attacker IP subnet with CSTF Cyber Dossier #7749 and Tornado Cash transaction hash 0x89f4b...32e1. Linked associate of Klaus Brandt.",
                autonomousCountermeasures = listOf(
                    "Autonomous Air-Gap: Isolated Substation 09 from primary WAN in 38ms",
                    "Dynamic Honeypot: Diverted hostile traffic to deceptive virtualized PLC container",
                    "Session Invalidation: Revoked all active TLS 1.3 cryptographic session tokens",
                    "Geofence Lockdown: Dispatched RF Jamming Beacon to physical control facility perimeter"
                ),
                commanderAuthorizationAction = "Authorize FLIR Recon Patrol Hawk Drone Alpha & Sector Containment Rapid Intercept",
                neutralizationOutcome = "Hostile intrusion neutralised within 410ms. Zero electrical grid power disruption. Suspect terminal IP blocked globally across all 6 federated jurisdictions.",
                forensicLedgerHash = "SHA256: 7b4a8e29d3f1c8402b9e6610a34c991c01eef729a4"
            ),
            IncidentScenario(
                id = "SCEN-02-ALPINE",
                title = "Alpine Pass Hostile Infiltration",
                threatVector = "Boundary Geofence Evasion & Clandestine Precursor Transport in Snowpack",
                targetSuspect = "Klaus 'Hydra' Brandt",
                targetSuspectId = "SUSP-3312",
                sector = "Kaiser Mountain Alpine Ridge (Elev: 2,450m)",
                sensorSource = "Orbital Recon Sentinel-4 (SAR) & Alpine OP-3",
                aiConfidencePercent = 94,
                detectionTelemetry = "Satellite Synthetic Aperture Radar (SAR) detected anomalous 42°C thermal heat bloom in sub-zero alpine snow ravine. Movement vector 4.2 km/h heading toward crevasse shelter.",
                federatedIntelCorrelation = "Cross-referenced with Europol Priority Warrant #E-1004. Route matches historic clandestine smuggling path documented in Operation Ghost Horizon case files.",
                autonomousCountermeasures = listOf(
                    "Satellite SAR Lock: Re-tasked Sentinel-4 for 30-second continuous orbital telemetry passes",
                    "Acoustic Geophone Array: Triangulated exact footsteps and snow vehicle engine signature",
                    "Automated Checkpoint Seal: Locked downward mountain avalanche safety gates",
                    "Field Operative Broadcast: Pushed encrypted target vector to alpine patrol agents"
                ),
                commanderAuthorizationAction = "Authorize Alpine Rapid Strike Team Deployment & Helicopter Intercept",
                neutralizationOutcome = "Suspect vehicle trapped before eastern valley descent. 40kg of undeclared chemical precursors confiscated. Suspect taken into high-security custody.",
                forensicLedgerHash = "SHA256: 3c9901ef871b223049da412e650c821a9904d4128e"
            ),
            IncidentScenario(
                id = "SCEN-03-BIOMETRIC",
                title = "Airport Terminal Biometric Disguise Breach",
                threatVector = "Forged Diplomatic Credentials & Deepfake Silicon Mask Attempt",
                targetSuspect = "Elena 'Cobra' Rostova",
                targetSuspectId = "SUSP-8821",
                sector = "Sector Alpha - Airport Gate 4",
                sensorSource = "Terminal Bio-Cam 01 (SN-BIO-01)",
                aiConfidencePercent = 96,
                detectionTelemetry = "Biometric Vector Scanner identified 91.2% nodal bone structure match despite altered hair, tinted contact lenses, and forged diplomatic passport #D-8912/4.",
                federatedIntelCorrelation = "Instantaneous query across INTERPOL Red Notice database #B-4410. Associated flight ticket booked using decentralized crypto liquidity pool linked to Darius Thorne.",
                autonomousCountermeasures = listOf(
                    "Silent E-Gate Delay: Extended automated passport inspection processing cycle by 45s",
                    "Multi-Angle Optical Lock: Assigned 4 high-speed PTZ cameras to track suspect trajectory",
                    "Discreet Alert Dispatch: Silent vibration ping sent to airport plainclothes security unit",
                    "Baggage Hold: Automated carousel redirection of suspect checked luggage for radiological scan"
                ),
                commanderAuthorizationAction = "Issue Sovereign Red Notice Detention & Discrete Apprehension Order",
                neutralizationOutcome = "Target apprehended quietly in boarding corridor without public alarm. Encrypted quantum flash drive and forged diplomatic credentials impounded.",
                forensicLedgerHash = "SHA256: 91dab27038eec91204859a01f721bc34901ee5402a"
            )
        )
    }
}
