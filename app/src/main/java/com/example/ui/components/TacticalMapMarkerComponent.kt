package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.LocationCategory
import com.example.model.MapMarker
import com.example.model.MarkerOperationalStatus
import com.example.ui.theme.*

fun getCategoryIcon(category: LocationCategory): ImageVector = when (category) {
    LocationCategory.FOREST -> Icons.Default.Forest
    LocationCategory.MOUNTAINS -> Icons.Default.Terrain
    LocationCategory.URBAN -> Icons.Default.LocationCity
    LocationCategory.VILLAGE -> Icons.Default.HolidayVillage
}

@Composable
fun TacticalMapMarker(
    marker: MapMarker,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MarkerPulse_${marker.id}")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (marker.status == MarkerOperationalStatus.THREAT_DETECTED) 800 else 1400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val categoryColor = marker.category.themeColor
    val statusColor = marker.status.color

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(onClick = onClick)
            .testTag("map_marker_${marker.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring for active threats or selected marker
        if (marker.status == MarkerOperationalStatus.THREAT_DETECTED || isSelected) {
            Box(
                modifier = Modifier
                    .size((if (isSelected) 42.dp else 32.dp) * pulseScale)
                    .clip(CircleShape)
                    .background(
                        (if (marker.status == MarkerOperationalStatus.THREAT_DETECTED) AlertCrimson else categoryColor)
                            .copy(alpha = 0.25f)
                    )
            )
        }

        // Inner categorized marker pin
        Box(
            modifier = Modifier
                .size(if (isSelected) 30.dp else 24.dp)
                .clip(CircleShape)
                .background(TacticalObsidian)
                .border(
                    width = if (isSelected) 2.dp else 1.5.dp,
                    color = if (isSelected) CyanGlow else categoryColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(marker.category),
                contentDescription = "${marker.category.displayName} Marker: ${marker.name}",
                tint = if (isSelected) CyanGlow else categoryColor,
                modifier = Modifier.size(if (isSelected) 16.dp else 13.dp)
            )

            // Status dot indicator at top right corner of the pin
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 1.dp, y = (-1).dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .border(0.5.dp, TacticalObsidian, CircleShape)
            )
        }
    }
}

@Composable
fun MapMarkerCallout(
    marker: MapMarker,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, marker.category.themeColor.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .testTag("map_marker_callout_${marker.id}"),
        color = TacticalSurface.copy(alpha = 0.95f),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(marker.category.themeColor.copy(alpha = 0.2f))
                    .border(1.dp, marker.category.themeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(marker.category),
                    contentDescription = null,
                    tint = marker.category.themeColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = marker.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "[${marker.category.badgeLabel}]",
                        style = MaterialTheme.typography.labelSmall,
                        color = marker.category.themeColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "Elev: ${marker.elevationMeters}m • ${marker.status.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Callout",
                    tint = TextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun MapMarkerDetailCard(
    marker: MapMarker,
    onDeployDrone: (MapMarker) -> Unit,
    onDeploySensor: (MapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TacticalSurface)
            .border(1.dp, marker.category.themeColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("marker_detail_card_${marker.id}")
    ) {
        Column {
            // Header: Category Icon, Name, and Status Badge
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
                            .background(marker.category.backgroundTint)
                            .border(1.dp, marker.category.themeColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(marker.category),
                            contentDescription = marker.category.displayName,
                            tint = marker.category.themeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = marker.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${marker.category.displayName} • ${marker.coordinates}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Category Badge
                Surface(
                    color = marker.category.themeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(
                        text = marker.category.badgeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = marker.category.themeColor,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operational Readout Box
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
                        text = "ENVIRONMENTAL TELEMETRY:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = marker.telemetryReadout,
                        style = MaterialTheme.typography.labelSmall,
                        color = marker.category.themeColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Row: Elevation, Coverage, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MarkerStatChip(
                    label = "ALTITUDE",
                    value = "${marker.elevationMeters}m",
                    color = TacticalAmber,
                    modifier = Modifier.weight(1f)
                )
                MarkerStatChip(
                    label = "RADAR RANGE",
                    value = "${marker.coverageRadiusKm} km",
                    color = CyanGlow,
                    modifier = Modifier.weight(1f)
                )
                MarkerStatChip(
                    label = "STATUS",
                    value = marker.status.label.take(10).uppercase(),
                    color = marker.status.color,
                    modifier = Modifier.weight(1.2f)
                )
            }

            // Linked Hostile/Suspect Alert
            if (marker.keySuspectTracked != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlertCrimson.copy(alpha = 0.15f))
                        .border(1.dp, AlertCrimson.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Threat Alert",
                        tint = AlertCrimson,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "FLAGGED TARGET TRACKED: ${marker.keySuspectTracked}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlertCrimson,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            if (marker.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = marker.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDeployDrone(marker) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("deploy_drone_marker_${marker.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = marker.category.themeColor),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TacticalObsidian
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DEPLOY DRONE",
                        color = TacticalObsidian,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }

                OutlinedButton(
                    onClick = { onDeploySensor(marker) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("deploy_sensor_marker_${marker.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanGlow),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ SENSOR BEACON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkerStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(TacticalObsidian)
            .border(1.dp, TacticalBorder, RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun MarkerCategoryFilterChips(
    selectedCategory: LocationCategory?,
    markerCounts: Map<LocationCategory, Int>,
    onSelectCategory: (LocationCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "FILTER MAP MARKERS BY CATEGORY:",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text("ALL (${markerCounts.values.sum()})", fontSize = 10.sp) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                    selectedLabelColor = CyanGlow
                )
            )

            LocationCategory.entries.forEach { category ->
                val count = markerCounts[category] ?: 0
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onSelectCategory(if (selectedCategory == category) null else category) },
                    leadingIcon = {
                        Icon(
                            imageVector = getCategoryIcon(category),
                            contentDescription = null,
                            tint = if (selectedCategory == category) category.themeColor else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    label = { Text("${category.badgeLabel} ($count)", fontSize = 9.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.themeColor.copy(alpha = 0.2f),
                        selectedLabelColor = category.themeColor
                    )
                )
            }
        }
    }
}

@Composable
fun MapMarkerLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp)),
        color = TacticalSurface.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocationCategory.entries.forEach { cat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(cat.backgroundTint)
                            .border(1.dp, cat.themeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(cat),
                            contentDescription = null,
                            tint = cat.themeColor,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(
                        text = cat.badgeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = cat.themeColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
