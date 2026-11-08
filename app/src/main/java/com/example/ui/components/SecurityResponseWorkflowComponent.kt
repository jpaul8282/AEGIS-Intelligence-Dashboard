package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.IncidentScenario
import com.example.model.WorkflowPhase
import com.example.ui.AegisViewModel
import com.example.ui.theme.*

@Composable
fun SecurityResponseWorkflowDialog(
    viewModel: AegisViewModel,
    onDismiss: () -> Unit
) {
    val activeScenario by viewModel.activeWorkflowScenario.collectAsState()
    val currentPhase by viewModel.currentWorkflowPhase.collectAsState()
    val scenarios by viewModel.incidentScenarios.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CyanGlow.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .testTag("security_response_workflow_dialog"),
            color = TacticalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
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
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(currentPhase.themeColor)
                        )
                        Column {
                            Text(
                                text = "AEGIS AI SECURITY RESPONSE WORKFLOW",
                                style = MaterialTheme.typography.titleSmall,
                                color = CyanGlow,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Autonomous Threat Detection • AI Investigation • Executive Containment",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_workflow_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Workflow",
                            tint = TextMuted
                        )
                    }
                }

                HorizontalDivider(color = TacticalBorder, modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Workflow Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Scenario Selector Bar
                    Text(
                        text = "SELECT LIVE BREACH SIMULATION SCENARIO:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(scenarios) { scenario ->
                            val isSelected = scenario.id == activeScenario.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectWorkflowScenario(scenario) },
                                label = {
                                    Text(
                                        text = scenario.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                                    selectedLabelColor = CyanGlow
                                )
                            )
                        }
                    }

                    // 5-Stage Interactive Progress Pipeline
                    WorkflowPipelineIndicator(
                        currentPhase = currentPhase,
                        onSelectPhase = { viewModel.selectWorkflowPhase(it) }
                    )

                    // Target & Sector Incident Telemetry Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalObsidian)
                            .border(1.dp, currentPhase.themeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TARGET: ${activeScenario.targetSuspect.uppercase()}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Surface(
                                    color = currentPhase.themeColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AI CONFIDENCE: ${activeScenario.aiConfidencePercent}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = currentPhase.themeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sector: ${activeScenario.sector} // Sensor: ${activeScenario.sensorSource}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vector: ${activeScenario.threatVector}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AlertCrimson,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Dynamic Phase Operation Card
                    when (currentPhase) {
                        WorkflowPhase.DETECTION -> PhaseDetectionCard(
                            scenario = activeScenario,
                            onAdvance = { viewModel.advanceWorkflowPhase() }
                        )
                        WorkflowPhase.INVESTIGATION -> PhaseInvestigationCard(
                            scenario = activeScenario,
                            onAdvance = { viewModel.advanceWorkflowPhase() }
                        )
                        WorkflowPhase.CONTAINMENT -> PhaseContainmentCard(
                            scenario = activeScenario,
                            onAdvance = { viewModel.advanceWorkflowPhase() }
                        )
                        WorkflowPhase.TACTICAL_RESPONSE -> PhaseTacticalResponseCard(
                            scenario = activeScenario,
                            userRole = currentUser.role,
                            onAuthorize = { viewModel.authorizeExecutiveAction(activeScenario.commanderAuthorizationAction) }
                        )
                        WorkflowPhase.FORENSIC_AUDIT -> PhaseForensicAuditCard(
                            scenario = activeScenario,
                            onReset = { viewModel.resetWorkflow() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowPipelineIndicator(
    currentPhase: WorkflowPhase,
    onSelectPhase: (WorkflowPhase) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, TacticalBorder, RoundedCornerShape(10.dp)),
        color = TacticalObsidian
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkflowPhase.entries.forEach { phase ->
                val isCompleted = phase.phaseNumber < currentPhase.phaseNumber
                val isCurrent = phase == currentPhase

                val stepColor = when {
                    isCurrent -> phase.themeColor
                    isCompleted -> TacticalEmerald
                    else -> TextMuted
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectPhase(phase) }
                        .padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) phase.themeColor.copy(alpha = 0.2f) else TacticalSurfaceVariant)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = stepColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = TacticalEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "${phase.phaseNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = stepColor,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = phase.name.substringBefore(" ").take(7),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) stepColor else TextMuted,
                        fontSize = 8.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun PhaseDetectionCard(
    scenario: IncidentScenario,
    onAdvance: () -> Unit
) {
    WorkflowStageContainer(
        phase = WorkflowPhase.DETECTION,
        title = "PHASE 1: HEURISTIC ANOMALY DETECTION",
        guidance = "AEGIS neural telemetry engine monitors incoming packets, thermal wakes, and biometric vectors. Statistical deviation from learned baseline triggers instant alert."
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TacticalObsidian)
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "LIVE SENSOR TELEMETRY READOUT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scenario.detectionTelemetry,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertCrimson,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAdvance,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("advance_to_investigation_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TacticalObsidian)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TRIGGER FEDERATED AI INVESTIGATION",
                color = TacticalObsidian,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PhaseInvestigationCard(
    scenario: IncidentScenario,
    onAdvance: () -> Unit
) {
    WorkflowStageContainer(
        phase = WorkflowPhase.INVESTIGATION,
        title = "PHASE 2: FEDERATED AI INVESTIGATION & LINK ANALYSIS",
        guidance = "Autonomous cross-agency query queries INTERPOL, Europol, FBI/NCIC, and CSTF Cyber databases in parallel, synthesizing suspect dossier and associates."
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TacticalObsidian)
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "CROSS-JURISDICTION GRAPH CORRELATION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scenario.federatedIntelCorrelation,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanGlow,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAdvance,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("advance_to_containment_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = TacticalObsidian)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "EXECUTE AUTONOMOUS CONTAINMENT PROTOCOLS",
                color = TacticalObsidian,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PhaseContainmentCard(
    scenario: IncidentScenario,
    onAdvance: () -> Unit
) {
    WorkflowStageContainer(
        phase = WorkflowPhase.CONTAINMENT,
        title = "PHASE 3: INSTANTANEOUS AUTONOMOUS CONTAINMENT",
        guidance = "AEGIS acts without latency to prevent infrastructure or life harm. Non-destructive countermeasures execute autonomously within milliseconds."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "AUTONOMOUS ACTIONS EXECUTED (< 50ms):",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )

            scenario.autonomousCountermeasures.forEach { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TacticalObsidian)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TacticalAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAdvance,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("advance_to_executive_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AlertViolet),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "REQUEST EXECUTIVE COMMANDER AUTHORIZATION",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PhaseTacticalResponseCard(
    scenario: IncidentScenario,
    userRole: com.example.model.UserRole,
    onAuthorize: () -> Unit
) {
    val canAuthorize = userRole.clearanceLevel >= 4

    WorkflowStageContainer(
        phase = WorkflowPhase.TACTICAL_RESPONSE,
        title = "PHASE 4: EXECUTIVE TACTICAL AUTHORIZATION GATE",
        guidance = "Critical physical, drone, or legal actions require human-in-the-loop executive signoff to adhere strictly to rules of engagement and sovereign authority."
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AlertViolet.copy(alpha = 0.12f))
                .border(1.dp, AlertViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "EXECUTIVE ACTION AWAITING COMMANDER SIGN-OFF:",
                    style = MaterialTheme.typography.labelSmall,
                    color = AlertViolet,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scenario.commanderAuthorizationAction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Requires: Clearance Level 4+ (Current: Level ${userRole.clearanceLevel} - ${userRole.title})",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canAuthorize) TacticalEmerald else AlertCrimson,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onAuthorize,
            enabled = canAuthorize,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("authorize_executive_action_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = AlertViolet,
                disabledContainerColor = TacticalSurfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (canAuthorize) "AUTHORIZE IMMEDIATE TACTICAL INTERCEPT" else "AUTHORIZATION LOCKED (SWITCH TO COMMANDER)",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PhaseForensicAuditCard(
    scenario: IncidentScenario,
    onReset: () -> Unit
) {
    WorkflowStageContainer(
        phase = WorkflowPhase.FORENSIC_AUDIT,
        title = "PHASE 5: IMMUTABLE FORENSIC AUDIT & NEUTRALIZATION",
        guidance = "The threat was successfully detected, investigated, and neutralized before damage occurred. Evidence is cryptographically anchored."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TacticalEmerald.copy(alpha = 0.12f))
                    .border(1.dp, TacticalEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "INCIDENT OUTCOME & CONTAINMENT:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TacticalEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scenario.neutralizationOutcome,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TacticalObsidian)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "CRYPTOGRAPHIC FORENSIC LEDGER HASH:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = scenario.forensicLedgerHash,
                        style = MaterialTheme.typography.bodySmall,
                        color = TacticalEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_workflow_drill_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = TacticalObsidian)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SIMULATION COMPLETE // RUN NEXT DRILL",
                color = TacticalObsidian,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun WorkflowStageContainer(
    phase: WorkflowPhase,
    title: String,
    guidance: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(1.dp, phase.themeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = phase.themeColor,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = guidance,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
