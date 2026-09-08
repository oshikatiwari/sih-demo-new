package com.example.smriti.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.PineGreen

@Composable
fun BluetoothBanner(
    isConnected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isConnected) Color(0xFFE8F5E9) else Color(0xFFF1F5F9))
            .border(
                width = 1.2.dp,
                color = if (isConnected) Color(0xFF52B788) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("bluetooth_banner")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Default.Hearing else Icons.Default.Bluetooth,
                contentDescription = "Bluetooth Status",
                tint = if (isConnected) PineGreen else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isConnected) "Connected: External Speaker & Hearing Aid" else "Bluetooth Audio Disconnected",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) ForestGreen else Color.DarkGray
                )
                Text(
                    text = if (isConnected) "Voice guidance is amplified for elder clarity" else "Tap to connect external audio device",
                    fontSize = 12.sp,
                    color = Color(0xFF4A5568)
                )
            }

            if (isConnected) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = PineGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
