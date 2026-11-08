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
import com.example.ui.AegisViewModel
import com.example.ui.components.JurisdictionBadge
import com.example.ui.components.SuspectDetailDialog
import com.example.ui.components.ThreatBadge
import com.example.ui.theme.*

@Composable
fun GlobalSearchScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    var globalQuery by remember { mutableStateOf("") }
    var selectedJurisdictionTab by remember { mutableStateOf<Jurisdiction?>(null) }
    val suspects by viewModel.suspects.collectAsState()
    val cases by viewModel.cases.collectAsState()
    val selectedSuspect by viewModel.selectedSuspect.collectAsState()

    // Filter results across jurisdictions
    val searchResults = remember(globalQuery, selectedJurisdictionTab, suspects) {
        suspects.filter { suspect ->
            val matchQuery = globalQuery.isBlank() ||
                    suspect.name.contains(globalQuery, ignoreCase = true) ||
                    suspect.aliases.any { it.contains(globalQuery, ignoreCase = true) } ||
                    suspect.primaryOffense.contains(globalQuery, ignoreCase = true) ||
                    suspect.cryptoHash.contains(globalQuery, ignoreCase = true) ||
                    suspect.lastKnownLocation.contains(globalQuery, ignoreCase = true) ||
                    suspect.associatedCaseNumber.contains(globalQuery, ignoreCase = true)

            val matchJurisdiction = selectedJurisdictionTab == null || suspect.jurisdiction == selectedJurisdictionTab
            matchQuery && matchJurisdiction
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Section Header
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "GLOBAL CROSS-REFERENCE ENGINE",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Federated Law Enforcement & Intelligence Multi-Agency Query",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Global Query Input
        item {
            OutlinedTextField(
                value = globalQuery,
                onValueChange = { globalQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_crossref_input"),
                placeholder = {
                    Text("Search cross-agency records, biometric tags, crypto hashes...", color = TextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyanGlow)
                },
                trailingIcon = {
                    if (globalQuery.isNotEmpty()) {
                        IconButton(onClick = { globalQuery = "" }) {
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

        // Federated Agency Status Matrix
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "FEDERATED AGENCIES STATUS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        AgencyStatusPill(
                            name = "ALL AGENCIES",
                            latency = "SYNCHRONIZED",
                            isSelected = selectedJurisdictionTab == null,
                            onClick = { selectedJurisdictionTab = null }
                        )
                    }
                    items(Jurisdiction.entries) { jur ->
                        AgencyStatusPill(
                            name = jur.code,
                            latency = "${(18..45).random()}ms OK",
                            isSelected = selectedJurisdictionTab == jur,
                            onClick = { selectedJurisdictionTab = if (selectedJurisdictionTab == jur) null else jur }
                        )
                    }
                }
            }
        }

        // Cross-Agency Correlation Summary Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(TacticalSurface)
                    .border(1.dp, CyanSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INTER-AGENCY LINK ANALYSIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "HASH CORRELATION: 99.4%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TacticalEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cross-referencing identifies a shared financial conduit between CSTF Cyber suspect Viktor Sterling and Europol suspect Klaus Brandt via Tornado Cash routing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Results Section Header
        item {
            Text(
                text = "CROSS-REFERENCED MATCHES (${searchResults.size})",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        items(searchResults, key = { it.id }) { suspect ->
            CrossReferenceResultCard(
                suspect = suspect,
                onClick = { viewModel.selectSuspect(suspect) }
            )
        }
    }

    // Suspect Detail modal
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
}

@Composable
private fun AgencyStatusPill(
    name: String,
    latency: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                1.dp,
                if (isSelected) CyanGlow else TacticalBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick),
        color = if (isSelected) CyanGlow.copy(alpha = 0.15f) else TacticalSurface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) CyanGlow else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = latency,
                style = MaterialTheme.typography.bodySmall,
                color = TacticalEmerald,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun CrossReferenceResultCard(
    suspect: Suspect,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TacticalSurface)
            .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = suspect.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aliases: " + suspect.aliases.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                JurisdictionBadge(jurisdiction = suspect.jurisdiction)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThreatBadge(threat = suspect.threatLevel)
                Text(
                    text = "CASE: ${suspect.associatedCaseNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Offense: ${suspect.primaryOffense}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Crypto Trace: ${suspect.cryptoHash.take(24)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = TacticalEmerald,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
                Text(
                    text = "VIEW DOSSIER ->",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}
