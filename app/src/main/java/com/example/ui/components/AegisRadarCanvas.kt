package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SensorNode
import com.example.model.SensorStatus
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AegisRadarCanvas(
    sensors: List<SensorNode>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(TacticalSurface)
            .border(1.dp, TacticalBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TACTICAL RADAR // SECTOR SWEEP",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanGlow,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${sensors.count { it.status == SensorStatus.ONLINE }} ONLINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TacticalEmerald,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = minOf(size.width, size.height) / 2.1f

                    // Concentric radar circles
                    for (i in 1..4) {
                        val r = maxRadius * (i / 4f)
                        drawCircle(
                            color = CyanSubtle.copy(alpha = 0.35f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Crosshair grid lines
                    drawLine(
                        color = CyanSubtle.copy(alpha = 0.45f),
                        start = Offset(center.x - maxRadius, center.y),
                        end = Offset(center.x + maxRadius, center.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = CyanSubtle.copy(alpha = 0.45f),
                        start = Offset(center.x, center.y - maxRadius),
                        end = Offset(center.x, center.y + maxRadius),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Rotating sweep line
                    rotate(degrees = angle, pivot = center) {
                        drawLine(
                            brush = Brush.radialGradient(
                                colors = listOf(CyanGlow.copy(alpha = 0.8f), Color.Transparent),
                                center = center,
                                radius = maxRadius
                            ),
                            start = center,
                            end = Offset(center.x + maxRadius, center.y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Draw sensor blips
                    sensors.forEach { sensor ->
                        val sx = center.x + (sensor.xPosRatio - 0.5f) * maxRadius * 1.7f
                        val sy = center.y + (sensor.yPosRatio - 0.5f) * maxRadius * 1.7f
                        val sensorOffset = Offset(sx, sy)

                        val blipColor = when (sensor.status) {
                            SensorStatus.ALERTING -> AlertCrimson
                            SensorStatus.ONLINE -> CyanGlow
                            SensorStatus.CALIBRATING -> TacticalAmber
                            SensorStatus.OFFLINE -> TextMuted
                        }

                        // Outer pulse ring for alerting or online
                        if (sensor.status == SensorStatus.ALERTING) {
                            drawCircle(
                                color = AlertCrimson.copy(alpha = 0.35f),
                                radius = 9.dp.toPx() * pulseScale,
                                center = sensorOffset
                            )
                        } else if (sensor.status == SensorStatus.ONLINE) {
                            drawCircle(
                                color = CyanGlow.copy(alpha = 0.2f),
                                radius = 6.dp.toPx() * pulseScale,
                                center = sensorOffset
                            )
                        }

                        // Solid center blip
                        drawCircle(
                            color = blipColor,
                            radius = 4.dp.toPx(),
                            center = sensorOffset
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Radar legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadarLegendItem(color = CyanGlow, label = "Telemetry")
                RadarLegendItem(color = AlertCrimson, label = "Alert / Threat")
                RadarLegendItem(color = TacticalAmber, label = "Calibrating")
            }
        }
    }
}

@Composable
private fun RadarLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 10.sp
        )
    }
}
