package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SensorNode
import com.example.model.SensorStatus
import com.example.model.SensorType
import com.example.ui.AegisViewModel
import com.example.ui.components.AegisRadarCanvas
import com.example.ui.theme.*

@Composable
fun SensorsMonitoringScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val sensors by viewModel.sensors.collectAsState()
    var selectedSector by remember { mutableStateOf<String?>(null) }

    val allSectors = remember(sensors) {
        sensors.map { it.sector.substringBefore(" -") }.distinct()
    }

    val filteredSensors = remember(sensors, selectedSector) {
        if (selectedSector == null) sensors else sensors.filter { it.sector.startsWith(selectedSector!!) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "REAL-TIME SENSOR TELEMETRY GRID",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Perimeter Geofences, Biometric Scanners, Drones & Orbital Feeds",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Radar Visualizer
        item {
            AegisRadarCanvas(
                sensors = sensors,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Sector Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SECTOR FILTER:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedSector == null,
                            onClick = { selectedSector = null },
                            label = { Text("ALL SECTORS", fontSize = 10.sp) }
                        )
                    }
                    items(allSectors) { sector ->
                        FilterChip(
                            selected = selectedSector == sector,
                            onClick = { selectedSector = if (selectedSector == sector) null else sector },
                            label = { Text(sector, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        // Sensor Grid List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MONITORED NODES (${filteredSensors.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AUTO-PULSE: 3.0s",
                    style = MaterialTheme.typography.labelSmall,
                    color = TacticalEmerald,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }

        items(filteredSensors, key = { it.id }) { sensor ->
            SensorNodeCard(
                sensor = sensor,
                onDiagnosticPing = { viewModel.pingSensor(sensor.id) }
            )
        }

        // Live Packet Feed Stream Simulator
        item {
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
                            text = "ENCRYPTED TELEMETRY LOG",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanGlow,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PROTOCOL: AEGIS-UDP/CHACHA20",
                            style = MaterialTheme.typography.labelSmall,
                            color = TacticalEmerald,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                            [10:50:12] PKT#48291 FROM SN-BIO-01 : 148 B/s (AUTH_TOKEN_VERIFIED)
                            [10:50:11] PKT#48290 FROM SN-CYBER-09 : ALERT_STATE_ACTIVE (SYN_BURST)
                            [10:50:09] PKT#48289 FROM SN-SAT-02 : ORBIT_EPHEMERIS_UPDATE_OK
                            [10:50:08] PKT#48288 FROM SN-DRONE-03 : TELEMETRY_STREAM_STABLE
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorNodeCard(
    sensor: SensorNode,
    onDiagnosticPing: () -> Unit
) {
    val sensorIcon = when (sensor.type) {
        SensorType.BIOMETRIC_SCANNER -> Icons.Default.Face
        SensorType.ACOUSTIC_ARRAY -> Icons.Default.Mic
        SensorType.SATELLITE_THERMAL -> Icons.Default.Satellite
        SensorType.DRONE_RECON -> Icons.Default.Flight
        SensorType.CYBER_PERIMETER -> Icons.Default.Security
        SensorType.GEOFENCE_BEACON -> Icons.Default.CellTower
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TacticalSurface)
            .border(
                1.dp,
                if (sensor.status == SensorStatus.ALERTING) AlertCrimson else TacticalBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
            .testTag("sensor_node_${sensor.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(sensor.status.color.copy(alpha = 0.15f))
                            .border(1.dp, sensor.status.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = sensorIcon,
                            contentDescription = sensor.name,
                            tint = sensor.status.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = sensor.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sensor.sector,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = sensor.status.color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(sensor.status.color, CircleShape)
                        )
                        Text(
                            text = sensor.status.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = sensor.status.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Value Metric readout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TacticalObsidian)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TELEMETRY READOUT:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = sensor.valueMetric,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row: Signal, Battery, Packets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RSSI SIGNAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${sensor.signalStrength}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (sensor.signalStrength > 70) TacticalEmerald else TacticalAmber,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column {
                    Text(
                        text = "BATTERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${sensor.batteryPercent}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column {
                    Text(
                        text = "THROUGHPUT",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${sensor.encryptedPacketsPerSec} pkt/s",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Diagnostic ping button
                OutlinedButton(
                    onClick = onDiagnosticPing,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow),
                    modifier = Modifier.testTag("ping_sensor_${sensor.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Ping",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PING (${sensor.lastPingSecondsAgo}s)",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
