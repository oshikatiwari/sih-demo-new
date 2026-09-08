package com.example.smriti.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.smriti.data.SmritiRepository
import com.example.smriti.service.SpeechToTextManager
import com.example.smriti.service.TtsManager
import com.example.smriti.service.VoiceAssistantService
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceControlModal(
    speechManager: SpeechToTextManager,
    ttsManager: TtsManager,
    currentLang: String,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (route: String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isListening by speechManager.isListening.collectAsState()
    val transcribedText by speechManager.transcribedText.collectAsState()
    val speechError by speechManager.speechError.collectAsState()

    var statusMessage by remember { mutableStateOf("Listening... Speak now") }
    var recognizedResultText by remember { mutableStateOf<String?>(null) }
    var recognizedActionRoute by remember { mutableStateOf<String?>(null) }
    var isEmergencyTriggered by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            ttsManager.playChime()
            speechManager.startListening(currentLang) { recognized ->
                val result = VoiceAssistantService.processVoiceQuery(
                    recognized,
                    currentLang,
                    SmritiRepository.getLatestCps()
                )
                recognizedResultText = result.responseText
                recognizedActionRoute = result.actionRoute

                if (result.intent == "EMERGENCY_ALERT") {
                    isEmergencyTriggered = true
                    SmritiRepository.triggerEmergencyReminder("Voice SOS: '$recognized'")
                }

                ttsManager.speak(result.responseText, currentLang)

                coroutineScope.launch {
                    delay(2200)
                    result.actionRoute?.let { route ->
                        onNavigate(route)
                        onDismiss()
                    }
                }
            }
        } else {
            statusMessage = "Microphone permission required for voice recognition."
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            ttsManager.playChime()
            speechManager.startListening(currentLang) { recognized ->
                val result = VoiceAssistantService.processVoiceQuery(
                    recognized,
                    currentLang,
                    SmritiRepository.getLatestCps()
                )
                recognizedResultText = result.responseText
                recognizedActionRoute = result.actionRoute

                if (result.intent == "EMERGENCY_ALERT") {
                    isEmergencyTriggered = true
                    SmritiRepository.triggerEmergencyReminder("Voice SOS: '$recognized'")
                }

                ttsManager.speak(result.responseText, currentLang)

                coroutineScope.launch {
                    delay(2400)
                    result.actionRoute?.let { route ->
                        onNavigate(route)
                        onDismiss()
                    }
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Dialog(onDismissRequest = {
        speechManager.stopListening()
        onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("voice_recognition_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEmergencyTriggered) Color(0xFFFFF1F2) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isEmergencyTriggered) Color(0xFFFEE2E2) else MintPastel),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isEmergencyTriggered) Icons.Default.Warning else Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isEmergencyTriggered) Color(0xFFDC2626) else PineGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEmergencyTriggered) "Emergency Triggered" else "Voice Assistant",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEmergencyTriggered) Color(0xFF991B1B) else ForestGreen
                        )
                    }

                    IconButton(
                        onClick = {
                            speechManager.stopListening()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_voice_modal")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Microphone Animated Pulse / Status Box
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isEmergencyTriggered -> Color(0xFFDC2626)
                                isListening -> Color(0xFFDC2626)
                                recognizedResultText != null -> PineGreen
                                else -> Color(0xFF0D9488)
                            }
                        )
                        .border(
                            width = 4.dp,
                            color = if (isEmergencyTriggered || isListening) Color(0xFFFCA5A5) else EmeraldGreen,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isEmergencyTriggered) Icons.Default.NotificationImportant else Icons.Default.Mic,
                        contentDescription = "Microphone status",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transcribed Live Speech
                if (transcribedText.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "You said:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"$transcribedText\"",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Assistant Response or Listening guidance
                if (recognizedResultText != null) {
                    Text(
                        text = recognizedResultText ?: "",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isEmergencyTriggered) Color(0xFF991B1B) else PineGreen,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                } else {
                    Text(
                        text = if (isListening) "Listening carefully... Please speak." else statusMessage,
                        fontSize = 15.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )
                }

                if (speechError != null && recognizedResultText == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = speechError ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick verbal suggestions
                Text(
                    text = "Try saying:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionPill("Play Cards", modifier = Modifier.weight(1f)) {
                        speechManager.stopListening()
                        onNavigate("game_memory")
                        onDismiss()
                    }
                    SuggestionPill("Clock Exercise", modifier = Modifier.weight(1f)) {
                        speechManager.stopListening()
                        onNavigate("game_clock")
                        onDismiss()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionPill("Tell Story", modifier = Modifier.weight(1f)) {
                        speechManager.stopListening()
                        onNavigate("game_reminiscence")
                        onDismiss()
                    }
                    SuggestionPill("Emergency Help", isAlert = true, modifier = Modifier.weight(1f)) {
                        speechManager.stopListening()
                        SmritiRepository.triggerEmergencyReminder("Quick SOS button pressed")
                        ttsManager.speak("Emergency reminder triggered! Caregiver alerted.", currentLang)
                        onNavigate("gps_sentinel")
                        onDismiss()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dismiss / Action Button
                Button(
                    onClick = {
                        speechManager.stopListening()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEmergencyTriggered) Color(0xFFDC2626) else PineGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_voice_modal_button")
                ) {
                    Text(
                        text = if (isEmergencyTriggered) "View Sentinel Status" else "Done / Cancel",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionPill(
    label: String,
    isAlert: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isAlert) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
            contentColor = if (isAlert) Color(0xFFDC2626) else PineGreen
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
