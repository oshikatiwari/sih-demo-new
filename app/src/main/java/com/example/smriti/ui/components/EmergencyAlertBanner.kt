package com.example.smriti.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository

@Composable
fun EmergencyAlertBanner(
    message: String,
    onViewSentinel: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, Color(0xFFDC2626), RoundedCornerShape(16.dp))
            .clickable(onClick = onViewSentinel)
            .testTag("emergency_alert_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFEE2E2), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "EMERGENCY REMINDER ACTIVE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B)
                )
                Text(
                    text = if (message.isNotBlank()) message else "Caregiver notified with GPS beacon",
                    fontSize = 12.sp,
                    color = Color(0xFFB91C1C),
                    maxLines = 2
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp).testTag("dismiss_emergency_banner")
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFF991B1B),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
