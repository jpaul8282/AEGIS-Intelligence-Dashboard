package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Suspect
import com.example.model.SuspectStatus
import com.example.ui.theme.*

@Composable
fun SuspectDetailDialog(
    suspect: Suspect,
    onDismiss: () -> Unit,
    onToggleFlag: (String) -> Unit,
    onUpdateStatus: (String, SuspectStatus) -> Unit,
    onDeployGeofence: (Suspect) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("suspect_detail_dialog"),
            color = TacticalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(suspect.threatLevel.color, CircleShape)
                        )
                        Text(
                            text = "CRIMINAL DOSSIER // ${suspect.id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dossier_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dossier",
                            tint = TextSecondary
                        )
                    }
                }

                HorizontalDivider(color = TacticalBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Overview Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(TacticalObsidian)
                            .border(1.dp, TacticalBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Silhouette & Biometric score
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TacticalSurfaceVariant)
                                .border(1.dp, CyanSubtle, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Profile",
                                tint = CyanGlow,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = suspect.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aliases: " + suspect.aliases.joinToString(", "),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ThreatBadge(threat = suspect.threatLevel)
                                JurisdictionBadge(jurisdiction = suspect.jurisdiction)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Biometric & Status Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Biometric Match",
                            value = "${suspect.biometricConfidence}%",
                            subValue = "Facial Vector Lock",
                            accentColor = if (suspect.biometricConfidence > 90) TacticalEmerald else TacticalAmber,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Age / Origin",
                            value = "${suspect.age} YRS",
                            subValue = suspect.nationality,
                            accentColor = CyanGlow,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Offense
                    DossierSection(title = "PRIMARY OFFENSE & CHARGES") {
                        Text(
                            text = suspect.primaryOffense,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Warrants
                    DossierSection(title = "FEDERATED WARRANTS & NOTICES") {
                        suspect.activeWarrants.forEach { warrant ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AlertCrimson,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = warrant,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AlertCrimson,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sensor Trace & Location
                    DossierSection(title = "TELEMETRY TRACE & RECON") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = suspect.lastKnownLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Linked Node: ${suspect.sensorTraceId} // Case: ${suspect.associatedCaseNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Financial / Signal Hash
                    DossierSection(title = "CRYPTO TRACE & COMMS HASH") {
                        Text(
                            text = suspect.cryptoHash,
                            style = MaterialTheme.typography.bodySmall,
                            color = TacticalEmerald,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Intelligence Notes
                    DossierSection(title = "TACTICAL INTEL NOTES") {
                        Text(
                            text = suspect.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                HorizontalDivider(color = TacticalBorder, modifier = Modifier.padding(vertical = 10.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Toggle Priority Flag
                    Button(
                        onClick = { onToggleFlag(suspect.id) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_flag_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (suspect.isFlagged) AlertCrimson else TacticalSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (suspect.isFlagged) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (suspect.isFlagged) "FLAGGED" else "FLAG TARGET",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Status Dropdown / Button
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showStatusMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("change_status_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow),
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = suspect.status.label.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false },
                            modifier = Modifier.background(TacticalSurface)
                        ) {
                            SuspectStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = status.label,
                                            color = status.color,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    onClick = {
                                        onUpdateStatus(suspect.id, status)
                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Deploy Geofence
                    Button(
                        onClick = { onDeployGeofence(suspect) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("deploy_geofence_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "GEOFENCE",
                            color = TacticalObsidian,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DossierSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TacticalObsidian)
            .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}
