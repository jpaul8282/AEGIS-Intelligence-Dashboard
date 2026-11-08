package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApiEndpoint
import com.example.model.ApiMethod
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.AegisViewModel
import com.example.ui.components.ClearanceBadge
import com.example.ui.theme.*

@Composable
fun ApiGatewayScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val endpoints by viewModel.apiEndpoints.collectAsState()
    val userProfile by viewModel.currentUser.collectAsState()
    val testLogs by viewModel.apiTestLogs.collectAsState()
    val selectedEndpoint by viewModel.selectedEndpoint.collectAsState()

    var activeEndpoint by remember(endpoints, selectedEndpoint) {
        mutableStateOf(selectedEndpoint ?: endpoints.first())
    }

    var payloadInput by remember(activeEndpoint) {
        mutableStateOf(activeEndpoint.requestPayloadSnippet)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Api,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "SECURE API GATEWAY & RBAC",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Zero-Trust Endpoints, JWT Verification & Role-Based Access Control",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Active RBAC Security Card
        item {
            RbacOverviewCard(
                userProfile = userProfile,
                onSwitchRole = { viewModel.switchRole(it) }
            )
        }

        // Endpoints Registry Header
        item {
            Text(
                text = "SECURE API ENDPOINTS REGISTRY (${endpoints.size})",
                style = MaterialTheme.typography.labelMedium,
                color = CyanGlow,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Endpoints List
        items(endpoints, key = { it.id }) { ep ->
            val isSelected = ep.id == activeEndpoint.id
            val isPermitted = userProfile.role.clearanceLevel >= ep.requiredClearance
            EndpointRowCard(
                endpoint = ep,
                isSelected = isSelected,
                isPermitted = isPermitted,
                onClick = {
                    activeEndpoint = ep
                    viewModel.selectEndpoint(ep)
                }
            )
        }

        // Interactive API Sandbox & Tester
        item {
            ApiSandboxCard(
                endpoint = activeEndpoint,
                userProfile = userProfile,
                payload = payloadInput,
                onPayloadChange = { payloadInput = it },
                onExecute = {
                    viewModel.testEndpoint(activeEndpoint, payloadInput)
                }
            )
        }

        // Recent Audit Test Logs
        if (testLogs.isNotEmpty()) {
            item {
                Text(
                    text = "INTERACTIVE GATEWAY AUDIT LOG",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(testLogs.take(5), key = { it.timestamp + it.endpointId + it.statusCode }) { log ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(TacticalSurface)
                        .border(1.dp, TacticalBorder, RoundedCornerShape(6.dp))
                        .padding(10.dp)
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
                                        .size(6.dp)
                                        .background(
                                            if (log.statusCode in 200..299) TacticalEmerald else AlertCrimson,
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = "HTTP ${log.statusCode}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (log.statusCode in 200..299) TacticalEmerald else AlertCrimson,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = log.endpointId,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${log.latencyMs}ms // ${log.timestamp}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RbacOverviewCard(
    userProfile: UserProfile,
    onSwitchRole: (UserRole) -> Unit
) {
    var showRolesDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(1.dp, userProfile.role.badgeColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(14.dp)
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
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = userProfile.role.badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "RBAC ACCESS PROFILE",
                        style = MaterialTheme.typography.labelMedium,
                        color = userProfile.role.badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                ClearanceBadge(role = userProfile.role)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${userProfile.department} • Callsign: ${userProfile.callsign}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Token display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalObsidian)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOKEN: ${userProfile.sessionToken}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TacticalEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "HMAC VERIFIED",
                        style = MaterialTheme.typography.labelSmall,
                        color = TacticalEmerald,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Allowed actions list
            Text(
                text = "CLEARANCE CAPABILITIES:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            userProfile.role.allowedActions.forEach { action ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = action,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Switch Role button
            OutlinedButton(
                onClick = { showRolesDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_role_modal_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = userProfile.role.badgeColor),
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwitchAccount,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SWITCH SECURITY CLEARANCE LEVEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showRolesDialog) {
        AlertDialog(
            onDismissRequest = { showRolesDialog = false },
            title = {
                Text(
                    text = "SELECT EXECUTIVE CLEARANCE",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRole.entries.forEach { role ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (role == userProfile.role) role.badgeColor else TacticalBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSwitchRole(role)
                                    showRolesDialog = false
                                },
                            color = if (role == userProfile.role) role.badgeColor.copy(alpha = 0.15f) else TacticalSurfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = role.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "LVL ${role.clearanceLevel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = role.badgeColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = role.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRolesDialog = false }) {
                    Text("CLOSE", color = TextSecondary)
                }
            },
            containerColor = TacticalSurface
        )
    }
}

@Composable
private fun EndpointRowCard(
    endpoint: ApiEndpoint,
    isSelected: Boolean,
    isPermitted: Boolean,
    onClick: () -> Unit
) {
    val methodColor = when (endpoint.method) {
        ApiMethod.GET -> TacticalEmerald
        ApiMethod.POST -> CyanGlow
        ApiMethod.PUT -> TacticalAmber
        ApiMethod.DELETE -> AlertCrimson
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TacticalSurfaceVariant else TacticalSurface)
            .border(
                1.dp,
                if (isSelected) CyanGlow else TacticalBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("endpoint_row_${endpoint.id}")
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
                    // Method pill
                    Surface(
                        color = methodColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        border = ButtonDefaults.outlinedButtonBorder
                    ) {
                        Text(
                            text = endpoint.method.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = methodColor,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = endpoint.path,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Required clearance pill
                Text(
                    text = "REQ LVL ${endpoint.requiredClearance}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPermitted) TacticalEmerald else AlertCrimson,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = endpoint.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${endpoint.category}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "Avg: ${endpoint.avgLatencyMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun ApiSandboxCard(
    endpoint: ApiEndpoint,
    userProfile: UserProfile,
    payload: String,
    onPayloadChange: (String) -> Unit,
    onExecute: () -> Unit
) {
    val isPermitted = userProfile.role.clearanceLevel >= endpoint.requiredClearance

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(1.dp, CyanGlow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(14.dp)
            .testTag("api_sandbox_panel")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INTERACTIVE ENDPOINT SANDBOX",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanGlow,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = endpoint.path,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Headers inspector box
            Text(
                text = "REQUEST HEADERS (AUTOMATICALLY INJECTED):",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalObsidian)
                    .padding(8.dp)
            ) {
                Column {
                    endpoint.headers.forEach { (key, value) ->
                        Text(
                            text = "$key: $value",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Request Payload Body
            Text(
                text = "REQUEST PAYLOAD (JSON):",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = payload,
                onValueChange = onPayloadChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_payload_input"),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TextPrimary
                ),
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanGlow,
                    unfocusedBorderColor = TacticalBorder,
                    focusedContainerColor = TacticalObsidian,
                    unfocusedContainerColor = TacticalObsidian
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Execute Button
            Button(
                onClick = onExecute,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("execute_api_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPermitted) CyanGlow else AlertCrimson
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isPermitted) Icons.Default.Send else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TacticalObsidian
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPermitted) "SEND ${endpoint.method.name} REQUEST" else "LOCKED (REQUIRES LEVEL ${endpoint.requiredClearance})",
                    color = TacticalObsidian,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sample Verified Response Box
            Text(
                text = "SAMPLE RESPONSE SPECIFICATION (200 OK):",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalObsidian)
                    .padding(8.dp)
            ) {
                Text(
                    text = endpoint.responsePayloadSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    color = TacticalEmerald,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
