package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.AegisViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TacticalMapScreen(
    viewModel: AegisViewModel,
    modifier: Modifier = Modifier
) {
    val mapMarkers by viewModel.mapMarkers.collectAsState()
    val filteredMarkers by viewModel.filteredMarkers.collectAsState()
    val selectedCategory by viewModel.selectedMarkerCategory.collectAsState()
    val selectedMarker by viewModel.selectedMarker.collectAsState()
    val mapMode by viewModel.mapDisplayMode.collectAsState()
    val terrainZones by viewModel.terrainZones.collectAsState()

    val markerCounts = remember(mapMarkers) {
        LocationCategory.entries.associateWith { cat ->
            mapMarkers.count { it.category == cat }
        }
    }

    val activeMarker = selectedMarker ?: filteredMarkers.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "TACTICAL MAP MARKERS & SURVEILLANCE",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Categorized Location Markers: Forests, Mountains, Urban & Villages",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Map Legend (Forest, Mountain, Urban, Village)
        item {
            MapMarkerLegend()
        }

        // Map Layer Mode Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "MAP DISPLAY LAYER:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MapDisplayMode.entries) { mode ->
                        FilterChip(
                            selected = mapMode == mode,
                            onClick = { viewModel.setMapDisplayMode(mode) },
                            label = { Text(mode.label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                                selectedLabelColor = CyanGlow
                            )
                        )
                    }
                }
            }
        }

        // Interactive Map Viewport with Map Markers
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CyanGlow.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .testTag("tactical_terrain_map_viewport")
            ) {
                // Map Satellite Base Imagery
                Image(
                    painter = painterResource(id = R.drawable.aegis_terrain_map),
                    contentDescription = "Tactical Terrain Map",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Optional Layer Shading (Thermal or Grid tint)
                if (mapMode == MapDisplayMode.THERMAL_INFRARED) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AlertCrimson.copy(alpha = 0.18f))
                    )
                } else if (mapMode == MapDisplayMode.ELEVATION_CONTOUR) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(TacticalAmber.copy(alpha = 0.12f))
                    )
                }

                // Top HUD telemetry telemetry bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GRID: 46.123°N, 10.456°E // SAT-LOCK",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanGlow,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredMarkers.size} MARKERS DISPLAYED",
                            style = MaterialTheme.typography.labelSmall,
                            color = TacticalEmerald,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Render Map Markers categorized on map
                filteredMarkers.forEach { marker ->
                    val isSelected = selectedMarker?.id == marker.id
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        TacticalMapMarker(
                            marker = marker,
                            isSelected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.selectMarker(null)
                                } else {
                                    viewModel.selectMarker(marker)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (marker.xRatio * 300).dp,
                                    y = (marker.yRatio * 200).dp
                                )
                        )
                    }
                }

                // Floating Callout overlay when a marker is clicked
                selectedMarker?.let { sel ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 26.dp, start = 12.dp, end = 12.dp)
                    ) {
                        MapMarkerCallout(
                            marker = sel,
                            onDismiss = { viewModel.selectMarker(null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bottom HUD Status bar on map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(TacticalObsidian.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOCATION: ${activeMarker?.name ?: "OVERVIEW"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanGlow,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "CATEGORY: ${activeMarker?.category?.badgeLabel ?: "ALL"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = activeMarker?.category?.themeColor ?: TacticalEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Marker Category Filter Bar (Forest, Mountain, Urban, Village)
        item {
            MarkerCategoryFilterChips(
                selectedCategory = selectedCategory,
                markerCounts = markerCounts,
                onSelectCategory = { cat ->
                    viewModel.selectMarkerCategory(cat)
                    val match = mapMarkers.firstOrNull { cat == null || it.category == cat }
                    viewModel.selectMarker(match)
                }
            )
        }

        // Selected Marker Detail Dossier Card
        activeMarker?.let { marker ->
            item {
                MapMarkerDetailCard(
                    marker = marker,
                    onDeployDrone = { viewModel.deployDroneToMarker(it) },
                    onDeploySensor = { viewModel.deploySensorToMarker(it, "Acoustic / Seismic Beacon") }
                )
            }
        }

        // Categorized Markers Directory
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCATION DIRECTORY (${filteredMarkers.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "TAP PIN OR ITEM TO FOCUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        items(filteredMarkers, key = { it.id }) { marker ->
            val isSelected = activeMarker?.id == marker.id
            MarkerDirectoryRow(
                marker = marker,
                isSelected = isSelected,
                onClick = { viewModel.selectMarker(marker) }
            )
        }
    }
}

@Composable
private fun MarkerDirectoryRow(
    marker: MapMarker,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TacticalSurfaceVariant else TacticalSurface)
            .border(
                1.dp,
                if (isSelected) marker.category.themeColor else TacticalBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("marker_dir_row_${marker.id}")
    ) {
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
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(marker.category.backgroundTint)
                        .border(1.dp, marker.category.themeColor, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(marker.category),
                        contentDescription = marker.category.displayName,
                        tint = marker.category.themeColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = marker.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = marker.category.themeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = marker.category.badgeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = marker.category.themeColor,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "${marker.coordinates} • Elev: ${marker.elevationMeters}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = marker.status.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = marker.status.color,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = "${marker.coverageRadiusKm} km range",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }
    }
}
