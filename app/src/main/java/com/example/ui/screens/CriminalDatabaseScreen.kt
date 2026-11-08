package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Jurisdiction
import com.example.model.Suspect
import com.example.model.SuspectStatus
import com.example.model.ThreatLevel
import com.example.ui.AegisViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CriminalDatabaseScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val filteredSuspects by viewModel.filteredSuspects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedThreat by viewModel.threatFilter.collectAsState()
    val selectedJurisdiction by viewModel.jurisdictionFilter.collectAsState()
    val selectedSuspect by viewModel.selectedSuspect.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var activeSubSection by remember { mutableStateOf(0) }

    if (activeSubSection == 1) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { activeSubSection = 0 },
                    label = { Text("DOSSIERS DIRECTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = true,
                    onClick = { activeSubSection = 1 },
                    label = { Text("FEDERATED CROSS-REF", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AlertViolet.copy(alpha = 0.2f),
                        selectedLabelColor = AlertViolet
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            GlobalSearchScreen(viewModel = viewModel, modifier = Modifier.weight(1f))
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CRIMINAL INTELLIGENCE DATABASE",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Federated Suspect Dossiers & Surveillance Traces",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    FilledTonalButton(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CyanSubtle,
                            contentColor = CyanGlow
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_suspect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NEW ENTRY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Sub-Section Tab: Dossiers vs Global Cross-Ref
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeSubSection == 0,
                        onClick = { activeSubSection = 0 },
                        label = { Text("DOSSIERS DIRECTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                            selectedLabelColor = CyanGlow
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = activeSubSection == 1,
                        onClick = { activeSubSection = 1 },
                        label = { Text("FEDERATED CROSS-REF", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AlertViolet.copy(alpha = 0.2f),
                            selectedLabelColor = AlertViolet
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("suspect_search_input"),
                    placeholder = {
                        Text("Search name, alias, crypto hash, or offense...", color = TextMuted, fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyanGlow)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = TacticalBorder,
                        focusedContainerColor = TacticalSurface,
                        unfocusedContainerColor = TacticalSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            // Threat Filter Chips Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "FILTER BY THREAT RATING:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedThreat == null,
                                onClick = { viewModel.setThreatFilter(null) },
                                label = { Text("ALL THREATS", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                                    selectedLabelColor = CyanGlow
                                )
                            )
                        }
                        items(ThreatLevel.entries) { threat ->
                            FilterChip(
                                selected = selectedThreat == threat,
                                onClick = { viewModel.setThreatFilter(if (selectedThreat == threat) null else threat) },
                                label = { Text(threat.label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = threat.color.copy(alpha = 0.2f),
                                    selectedLabelColor = threat.color
                                )
                            )
                        }
                    }
                }
            }

            // Jurisdiction Filter Chips Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "FILTER BY JURISDICTION:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedJurisdiction == null,
                                onClick = { viewModel.setJurisdictionFilter(null) },
                                label = { Text("ALL JURISDICTIONS", fontSize = 10.sp) }
                            )
                        }
                        items(Jurisdiction.entries) { jur ->
                            FilterChip(
                                selected = selectedJurisdiction == jur,
                                onClick = { viewModel.setJurisdictionFilter(if (selectedJurisdiction == jur) null else jur) },
                                label = { Text(jur.code, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }

            // Results count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE DOSSIERS: ${filteredSuspects.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "TAP ENTRY FOR FULL DOSSIER",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanGlow,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
            }

            if (filteredSuspects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "NO DOSSIERS MATCH CURRENT FILTERS",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            } else {
                items(filteredSuspects, key = { it.id }) { suspect ->
                    SuspectCard(
                        suspect = suspect,
                        onClick = { viewModel.selectSuspect(suspect) },
                        onToggleFlag = { viewModel.toggleSuspectFlag(suspect.id) }
                    )
                }
            }
        }
    }

    // Detail Dossier Dialog
    selectedSuspect?.let { suspect ->
        SuspectDetailDialog(
            suspect = suspect,
            onDismiss = { viewModel.selectSuspect(null) },
            onToggleFlag = { id -> viewModel.toggleSuspectFlag(id) },
            onUpdateStatus = { id, status -> viewModel.updateSuspectStatus(id, status) },
            onDeployGeofence = {
                viewModel.dispatchActionableAlert(
                    "GEO-NEW",
                    "Geofence Beacon Deployed around ${suspect.name}"
                )
                viewModel.selectSuspect(null)
            }
        )
    }

    // Add New Suspect Entry Dialog
    if (showAddDialog) {
        AddNewSuspectDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newSuspect ->
            }
        )
    }
    }
}

@Composable
private fun SuspectCard(
    suspect: Suspect,
    onClick: () -> Unit,
    onToggleFlag: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(
                1.dp,
                if (suspect.isFlagged) AlertCrimson.copy(alpha = 0.8f) else TacticalBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("suspect_card_${suspect.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Biometric Lock Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (suspect.biometricConfidence > 90) TacticalEmerald else TacticalAmber,
                                CircleShape
                            )
                    )
                    Text(
                        text = suspect.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Flag Button
                IconButton(
                    onClick = onToggleFlag,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (suspect.isFlagged) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                        contentDescription = "Flag Target",
                        tint = if (suspect.isFlagged) AlertCrimson else TextMuted
                    )
                }
            }

            if (suspect.aliases.isNotEmpty()) {
                Text(
                    text = "A.K.A. " + suspect.aliases.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThreatBadge(threat = suspect.threatLevel)
                SuspectStatusBadge(status = suspect.status)
                JurisdictionBadge(jurisdiction = suspect.jurisdiction)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = suspect.primaryOffense,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOC: ${suspect.lastKnownLocation}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "MATCH: ${suspect.biometricConfidence}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddNewSuspectDialog(
    onDismiss: () -> Unit,
    onAdd: (Suspect) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var offense by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedThreat by remember { mutableStateOf(ThreatLevel.BRAVO_HIGH) }
    var selectedJurisdiction by remember { mutableStateOf(Jurisdiction.INTERPOL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "AUTHORIZE NEW DOSSIER",
                style = MaterialTheme.typography.titleMedium,
                color = CyanGlow,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Suspect Full Legal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Known Aliases (Comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = offense,
                    onValueChange = { offense = it },
                    label = { Text("Primary Offense / Criminal Activity") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Last Known Sector / Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newSuspect = Suspect(
                            id = "SUSP-${(7000..9999).random()}",
                            name = name,
                            aliases = alias.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            nationality = "Unassigned",
                            age = 35,
                            threatLevel = selectedThreat,
                            status = SuspectStatus.AT_LARGE,
                            primaryOffense = if (offense.isNotBlank()) offense else "Under Multi-Agency Review",
                            jurisdiction = selectedJurisdiction,
                            activeWarrants = listOf("Pending Review Notice"),
                            lastKnownLocation = if (location.isNotBlank()) location else "Sector Unspecified",
                            sensorTraceId = "TRACE-NODE-${(10..99).random()}",
                            associatedCaseNumber = "CASE-2026-UNASSIGNED",
                            isFlagged = true,
                            biometricConfidence = 85,
                            notes = "Authorized by Executive Commander via mobile interface.",
                            cryptoHash = "0x" + (1000..9999).random().toString(16) + "...unverified",
                            knownAssociates = emptyList()
                        )
                        onAdd(newSuspect)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow)
            ) {
                Text("ENROLL TARGET", color = TacticalObsidian, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        },
        containerColor = TacticalSurface
    )
}
