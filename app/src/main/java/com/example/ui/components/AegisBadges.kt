package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Jurisdiction
import com.example.model.SuspectStatus
import com.example.model.ThreatLevel
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun ThreatBadge(threat: ThreatLevel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(threat.color.copy(alpha = 0.15f))
            .border(1.dp, threat.color.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(threat.color, RoundedCornerShape(3.dp))
            )
            Text(
                text = threat.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = threat.color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun SuspectStatusBadge(status: SuspectStatus, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(status.color.copy(alpha = 0.15f))
            .border(1.dp, status.color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = status.color,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun JurisdictionBadge(jurisdiction: Jurisdiction, modifier: Modifier = Modifier) {
    val borderColor = when (jurisdiction) {
        Jurisdiction.INTERPOL -> AlertViolet
        Jurisdiction.EUROPOL -> CyanGlow
        Jurisdiction.FBI_NCIC -> TacticalAmber
        Jurisdiction.CSTF_CYBER -> TacticalEmerald
        Jurisdiction.BORDER_MARITIME -> Color(0xFF38BDF8)
        Jurisdiction.TOKYO_MPD -> Color(0xFFF43F5E)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(borderColor.copy(alpha = 0.12f))
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = jurisdiction.code,
            style = MaterialTheme.typography.labelSmall,
            color = borderColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ClearanceBadge(role: UserRole, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(role.badgeColor.copy(alpha = 0.15f))
            .border(1.dp, role.badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(role.badgeColor, RoundedCornerShape(3.5.dp))
            )
            Text(
                text = "LVL ${role.clearanceLevel} // ${role.title.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = role.badgeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subValue: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TacticalSurface)
            .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}
