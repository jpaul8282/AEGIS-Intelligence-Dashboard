package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AegisRepository
import com.example.data.EndpointTestResult
import com.example.data.SystemAnalytics
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AegisTab(val label: String, val iconName: String) {
    DASHBOARD("Command", "dashboard"),
    TACTICAL_MAP("Maps", "map"),
    SUSPECTS("Dossiers", "badge"),
    SENSORS("Sensors", "sensors"),
    API_GATEWAY("API & RBAC", "api")
}

class AegisViewModel : ViewModel() {

    private val repository = AegisRepository(viewModelScope)

    val currentUser: StateFlow<UserProfile> = repository.currentUser
    val suspects: StateFlow<List<Suspect>> = repository.suspects
    val cases: StateFlow<List<InvestigationCase>> = repository.cases
    val sensors: StateFlow<List<SensorNode>> = repository.sensors
    val alerts: StateFlow<List<SystemAlert>> = repository.alerts
    val apiEndpoints: StateFlow<List<ApiEndpoint>> = repository.apiEndpoints
    val apiTestLogs: StateFlow<List<EndpointTestResult>> = repository.apiTestLogs
    val analytics: StateFlow<SystemAnalytics> = repository.analytics
    val terrainZones: StateFlow<List<TerrainZone>> = repository.terrainZones
    val mapMarkers: StateFlow<List<MapMarker>> = repository.mapMarkers
    val incidentScenarios: StateFlow<List<IncidentScenario>> = repository.incidentScenarios
    val activeWorkflowScenario: StateFlow<IncidentScenario> = repository.activeWorkflowScenario
    val currentWorkflowPhase: StateFlow<WorkflowPhase> = repository.currentWorkflowPhase

    private val _currentTab = MutableStateFlow(AegisTab.DASHBOARD)
    val currentTab: StateFlow<AegisTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _jurisdictionFilter = MutableStateFlow<Jurisdiction?>(null)
    val jurisdictionFilter: StateFlow<Jurisdiction?> = _jurisdictionFilter.asStateFlow()

    private val _threatFilter = MutableStateFlow<ThreatLevel?>(null)
    val threatFilter: StateFlow<ThreatLevel?> = _threatFilter.asStateFlow()

    private val _selectedTerrainFilter = MutableStateFlow<TerrainType?>(null)
    val selectedTerrainFilter: StateFlow<TerrainType?> = _selectedTerrainFilter.asStateFlow()

    private val _selectedTerrainZone = MutableStateFlow<TerrainZone?>(null)
    val selectedTerrainZone: StateFlow<TerrainZone?> = _selectedTerrainZone.asStateFlow()

    private val _mapDisplayMode = MutableStateFlow(MapDisplayMode.SATELLITE_HYBRID)
    val mapDisplayMode: StateFlow<MapDisplayMode> = _mapDisplayMode.asStateFlow()

    private val _selectedMarkerCategory = MutableStateFlow<LocationCategory?>(null)
    val selectedMarkerCategory: StateFlow<LocationCategory?> = _selectedMarkerCategory.asStateFlow()

    private val _selectedMarker = MutableStateFlow<MapMarker?>(null)
    val selectedMarker: StateFlow<MapMarker?> = _selectedMarker.asStateFlow()

    private val _selectedSuspect = MutableStateFlow<Suspect?>(null)
    val selectedSuspect: StateFlow<Suspect?> = _selectedSuspect.asStateFlow()

    private val _selectedEndpoint = MutableStateFlow<ApiEndpoint?>(null)
    val selectedEndpoint: StateFlow<ApiEndpoint?> = _selectedEndpoint.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val filteredMarkers: StateFlow<List<MapMarker>> = combine(
        repository.mapMarkers,
        _selectedMarkerCategory
    ) { markers, category ->
        if (category == null) markers else markers.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSuspects: StateFlow<List<Suspect>> = combine(
        repository.suspects,
        _searchQuery,
        _jurisdictionFilter,
        _threatFilter
    ) { suspectsList, query, jurisdiction, threat ->
        suspectsList.filter { suspect ->
            val matchesQuery = query.isBlank() ||
                    suspect.name.contains(query, ignoreCase = true) ||
                    suspect.aliases.any { it.contains(query, ignoreCase = true) } ||
                    suspect.primaryOffense.contains(query, ignoreCase = true) ||
                    suspect.lastKnownLocation.contains(query, ignoreCase = true) ||
                    suspect.cryptoHash.contains(query, ignoreCase = true) ||
                    suspect.associatedCaseNumber.contains(query, ignoreCase = true)

            val matchesJurisdiction = jurisdiction == null || suspect.jurisdiction == jurisdiction
            val matchesThreat = threat == null || suspect.threatLevel == threat

            matchesQuery && matchesJurisdiction && matchesThreat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AegisTab) {
        _currentTab.value = tab
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setJurisdictionFilter(jurisdiction: Jurisdiction?) {
        _jurisdictionFilter.value = jurisdiction
    }

    fun setThreatFilter(threat: ThreatLevel?) {
        _threatFilter.value = threat
    }

    fun selectSuspect(suspect: Suspect?) {
        _selectedSuspect.value = suspect
    }

    fun selectEndpoint(endpoint: ApiEndpoint?) {
        _selectedEndpoint.value = endpoint
    }

    fun toggleSuspectFlag(suspectId: String) {
        repository.toggleSuspectFlag(suspectId)
        val isNowFlagged = repository.suspects.value.find { it.id == suspectId }?.isFlagged ?: false
        _userMessage.value = if (isNowFlagged) "Priority surveillance flag engaged." else "Suspect flag removed."
    }

    fun updateSuspectStatus(suspectId: String, newStatus: SuspectStatus) {
        repository.updateSuspectStatus(suspectId, newStatus)
        _userMessage.value = "Status updated to: ${newStatus.label}"
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
        _userMessage.value = "Switched to ${role.title} (Clearance Level ${role.clearanceLevel})"
    }

    fun dispatchActionableAlert(alertId: String, action: String) {
        repository.executeActionableDispatch(alertId, action)
        _userMessage.value = "Action Executed: $action"
    }

    fun dismissAlert(alertId: String) {
        repository.dismissAlert(alertId)
        _userMessage.value = "Alert acknowledged and cleared."
    }

    fun pingSensor(sensorId: String) {
        repository.pingSensor(sensorId)
        _userMessage.value = "Diagnostic telemetry ping sent to $sensorId."
    }

    fun testEndpoint(endpoint: ApiEndpoint, payload: String? = null) {
        val result = repository.testApiEndpoint(endpoint, payload)
        _userMessage.value = "HTTP ${result.statusCode} from ${endpoint.path} (${result.latencyMs}ms)"
    }

    fun updateCaseProgress(caseId: String, delta: Int) {
        repository.updateCaseProgress(caseId, delta)
        _userMessage.value = "Investigation progress updated."
    }

    fun selectTerrainFilter(type: TerrainType?) {
        _selectedTerrainFilter.value = type
    }

    fun selectTerrainZone(zone: TerrainZone?) {
        _selectedTerrainZone.value = zone
    }

    fun setMapDisplayMode(mode: MapDisplayMode) {
        _mapDisplayMode.value = mode
        _userMessage.value = "Map layer switched to: ${mode.label}"
    }

    fun deployOverwatchToZone(zone: TerrainZone, unitType: String) {
        repository.deployOverwatchToZone(zone.id, unitType)
        _userMessage.value = "Deployed $unitType to ${zone.name}"
    }

    fun selectMarker(marker: MapMarker?) {
        _selectedMarker.value = marker
    }

    fun selectMarkerCategory(category: LocationCategory?) {
        _selectedMarkerCategory.value = category
    }

    fun deployDroneToMarker(marker: MapMarker) {
        repository.deployDroneToMarker(marker.id)
        _userMessage.value = "FLIR Recon Drone deployed to ${marker.name}"
    }

    fun deploySensorToMarker(marker: MapMarker, sensorName: String) {
        repository.deploySensorToMarker(marker.id, sensorName)
        _userMessage.value = "Deployed $sensorName at ${marker.name}"
    }

    fun selectWorkflowScenario(scenario: IncidentScenario) {
        repository.setWorkflowScenario(scenario)
        _userMessage.value = "Loaded Response Workflow: ${scenario.title}"
    }

    fun selectWorkflowPhase(phase: WorkflowPhase) {
        repository.setWorkflowPhase(phase)
    }

    fun advanceWorkflowPhase() {
        repository.advanceWorkflowPhase()
        _userMessage.value = "Advanced to: ${currentWorkflowPhase.value.phaseName}"
    }

    fun resetWorkflow() {
        repository.resetWorkflow()
        _userMessage.value = "Reset Response Workflow to Phase 1: Detection"
    }

    fun authorizeExecutiveAction(actionName: String) {
        repository.advanceWorkflowPhase()
        _userMessage.value = "COMMANDER OVERRIDE AUTHORIZED: $actionName"
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
