package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AlertSeverity
import com.example.model.InvestigationCase
import com.example.model.SystemAlert
import com.example.ui.AegisTab
import com.example.ui.AegisViewModel
import com.example.ui.components.AegisRadarCanvas
import com.example.ui.components.MetricCard
import com.example.ui.components.SecurityResponseWorkflowDialog
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analytics.collectAsState()
    val sensors by viewModel.sensors.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val cases by viewModel.cases.collectAsState()
    val userProfile by viewModel.currentUser.collectAsState()

    var showWorkflowDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Tactical HUD Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CyanGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.aegis_hud_banner),
                    contentDescription = "Tactical Global HUD",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Tactical Overlay Header
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    TacticalObsidian.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(TacticalEmerald, CircleShape)
                            )
                            Text(
                                text = "EXECUTIVE DEFENSE SYSTEM ONLINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanGlow,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = "Global Surveillance & Federated Threat Matrix",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Operator: ${userProfile.name} // ${userProfile.callsign}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Inside AEGIS: AI Security Response Workflow Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalSurface)
                    .border(1.dp, AlertViolet.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .testTag("security_workflow_card")
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
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AlertViolet, CircleShape)
                            )
                            Text(
                                text = "AEGIS AI RESPONSE WORKFLOW",
                                style = MaterialTheme.typography.labelSmall,
                                color = AlertViolet,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                        Surface(
                            color = AlertViolet.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "LIVE DRILL SIMULATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = AlertViolet,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "How AEGIS Detects, Investigates & Neutralizes Threats",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Step inside the autonomous 5-phase security pipeline: Heuristic Anomaly Detection -> Federated Cross-Correlation -> Instant Containment -> Executive Tactical Dispatch -> Immutable Forensic Audit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showWorkflowDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_response_workflow_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = AlertViolet),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INSIDE AEGIS: LAUNCH WORKFLOW SIMULATION",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Executive Analytics Metrics Row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Threat Index",
                    value = "${analytics.threatMatrixScore} / 100",
                    subValue = "Active Threats: ${analytics.activeThreatsCount}",
                    accentColor = if (analytics.threatMatrixScore > 75) AlertCrimson else TacticalAmber,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Telemetry Throughput",
                    value = "${analytics.networkThroughputMbps}",
                    subValue = "MB/s Encrypted TLS",
                    accentColor = CyanGlow,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Sensor Grid",
                    value = "${analytics.activeSensorsOnline}/${analytics.totalSensors}",
                    subValue = "All Sectors Monitored",
                    accentColor = TacticalEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Radar Sweep Component
        item {
            AegisRadarCanvas(
                sensors = sensors,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Quick Tactical Actions Bar
        item {
            Column {
                Text(
                    text = "ACTIONABLE EXECUTIVE PROTOCOLS",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Map,
                        label = "Maps",
                        color = CyanGlow,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectTab(AegisTab.TACTICAL_MAP) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Badge,
                        label = "Dossiers",
                        color = TacticalAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectTab(AegisTab.SUSPECTS) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Sensors,
                        label = "Sensors",
                        color = TacticalEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectTab(AegisTab.SENSORS) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Api,
                        label = "API Test",
                        color = AlertViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectTab(AegisTab.API_GATEWAY) }
                    )
                }
            }
        }

        // Critical Alerts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE SYSTEM ALERTS (${alerts.count { !it.isResolved }})",
                    style = MaterialTheme.typography.labelMedium,
                    color = AlertCrimson,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AUTO-TRIAGE ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        items(alerts.take(4), key = { it.id }) { alert ->
            AlertItemCard(
                alert = alert,
                onDispatch = { action -> viewModel.dispatchActionableAlert(alert.id, action) },
                onDismiss = { viewModel.dismissAlert(alert.id) }
            )
        }

        // Active Investigation Cases
        item {
            Text(
                text = "INVESTIGATION PROGRESS TRACKER",
                style = MaterialTheme.typography.labelMedium,
                color = CyanGlow,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(cases, key = { it.id }) { investigation ->
            InvestigationCaseCard(
                investigation = investigation,
                onAdvance = { viewModel.updateCaseProgress(investigation.id, 5) }
            )
        }
    }

    if (showWorkflowDialog) {
        SecurityResponseWorkflowDialog(
            viewModel = viewModel,
            onDismiss = { showWorkflowDialog = false }
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AlertItemCard(
    alert: SystemAlert,
    onDispatch: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (alert.isResolved) TacticalSurface.copy(alpha = 0.5f) else TacticalSurface)
            .border(
                1.dp,
                if (alert.isResolved) TacticalBorder else alert.severity.color.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(alert.severity.color, CircleShape)
                    )
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sector: ${alert.sector} // Sensor: ${alert.sourceSensorId}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )

            if (alert.actionTaken != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Action: ${alert.actionTaken}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TacticalEmerald,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!alert.isResolved) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDispatch("Deploy Patrol Drone Intercept") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AlertCrimson),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text(
                            text = "DEPLOY DRONE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    OutlinedButton(
                        onClick = { onDispatch("Sector Containment Protocol") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text(
                            text = "LOCKDOWN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Acknowledge Alert",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvestigationCaseCard(
    investigation: InvestigationCase,
    onAdvance: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TacticalSurface)
            .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = investigation.caseNumber,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "PROGRESS: ${investigation.progressPercentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (investigation.progressPercentage > 75) TacticalEmerald else TacticalAmber,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = investigation.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = investigation.synopsis,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { investigation.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CyanGlow,
                trackColor = TacticalSurfaceElevated,
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lead: ${investigation.leadInvestigator} // ${investigation.evidenceCount} Evidence Items",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                TextButton(
                    onClick = onAdvance,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "+ Advance Evidence",
                        fontSize = 10.sp,
                        color = CyanGlow
                    )
                }
            }
        }
    }
}
