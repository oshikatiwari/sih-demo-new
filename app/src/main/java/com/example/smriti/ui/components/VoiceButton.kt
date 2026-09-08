package com.example.smriti.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.PineGreen

@Composable
fun VoiceButton(
    isListening: Boolean,
    isSpeaking: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening || isSpeaking) 1.15f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    when {
                        isListening -> Color(0xFFDC2626) // Red when listening
                        isSpeaking -> Color(0xFF2563EB) // Blue when speaking
                        else -> PineGreen
                    }
                )
                .border(
                    width = 4.dp,
                    color = when {
                        isListening -> Color(0xFFFCA5A5)
                        isSpeaking -> Color(0xFF93C5FD)
                        else -> EmeraldGreen
                    },
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .testTag("voice_assistant_button")
        ) {
            Icon(
                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                contentDescription = "Voice Assistant",
                tint = Color.White,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                isListening -> "Listening to you..."
                isSpeaking -> "Smriti is speaking..."
                else -> "Tap to speak with Smriti"
            },
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = ForestGreen
        )
    }
}
