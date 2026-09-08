package com.example.smriti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.service.AppStrings
import com.example.smriti.service.SpeechToTextManager
import com.example.smriti.service.TtsManager
import com.example.smriti.service.VoiceAssistantService
import com.example.smriti.ui.components.BluetoothBanner
import com.example.smriti.ui.components.EmergencyAlertBanner
import com.example.smriti.ui.components.LanguagePickerModal
import com.example.smriti.ui.components.VoiceButton
import com.example.smriti.ui.components.VoiceControlModal
import com.example.smriti.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ttsManager: TtsManager,
    speechManager: SpeechToTextManager,
    onNavigate: (route: String) -> Unit,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()
    val isBluetoothConnected by SmritiRepository.bluetoothConnected.collectAsState()
    val emergencyAlertActive by SmritiRepository.emergencyAlertActive.collectAsState()
    val lastEmergencyMessage by SmritiRepository.lastEmergencyMessage.collectAsState()
    val activePatient = SmritiRepository.getActivePatient()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var voicePromptText by remember {
        mutableStateOf(VoiceAssistantService.getScreenGuidance("home", currentLang))
    }
    var showVoiceModal by remember { mutableStateOf(false) }
    val isListening by speechManager.isListening.collectAsState()
    var isSpeaking by remember { mutableStateOf(false) }

    // Update screen guidance whenever language changes
    LaunchedEffect(currentLang) {
        voicePromptText = VoiceAssistantService.getScreenGuidance("home", currentLang)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "स्mriti",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = activePatient.name,
                                fontSize = 12.sp,
                                color = Color(0xFFD8F3DC)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showVoiceModal = true },
                        modifier = Modifier.testTag("action_mic_topbar")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Control", tint = Color.White)
                    }

                    IconButton(
                        onClick = { showLanguagePicker = true },
                        modifier = Modifier.testTag("action_language_picker")
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            SmritiRepository.toggleBluetooth()
                            val connected = !isBluetoothConnected
                            val prompt = if (connected) VoiceAssistantService.getBluetoothPrompt(currentLang) else "Bluetooth disconnected"
                            ttsManager.speak(prompt, currentLang)
                        },
                        modifier = Modifier.testTag("action_bluetooth")
                    ) {
                        Icon(
                            Icons.Default.Hearing,
                            contentDescription = "Bluetooth Audio",
                            tint = if (isBluetoothConnected) Color(0xFF95D5B2) else Color.White.copy(alpha = 0.5f)
                        )
                    }

                    IconButton(
                        onClick = { onNavigate("caregiver") },
                        modifier = Modifier.testTag("action_caregiver_dashboard")
                    ) {
                        Icon(Icons.Default.SupervisorAccount, contentDescription = "Caregiver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PineGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SandBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Emergency Alert Banner if triggered
            if (emergencyAlertActive) {
                EmergencyAlertBanner(
                    message = lastEmergencyMessage,
                    onViewSentinel = { onNavigate("gps_sentinel") },
                    onDismiss = { SmritiRepository.dismissEmergencyAlert() }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Spoken Screen Guidance Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MintPastel),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, EmeraldGreen, RoundedCornerShape(20.dp))
                    .clickable {
                        ttsManager.speak(voicePromptText, currentLang)
                    }
                    .testTag("voice_guidance_card")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Read aloud",
                            tint = PineGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = voicePromptText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bluetooth speaker banner
            BluetoothBanner(
                isConnected = isBluetoothConnected,
                onToggle = {
                    SmritiRepository.toggleBluetooth()
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Today's Cognitive Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                    .clickable { onNavigate("caregiver") }
                    .testTag("home_score_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MintPastel),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${SmritiRepository.getLatestCps().toInt()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PineGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.get("today_cognitive_score", currentLang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "${AppStrings.get("engagement_level", currentLang)}: ${activePatient.currentDisplayLabel}",
                            fontSize = 13.sp,
                            color = Color(0xFF4A5568)
                        )
                    }

                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = "View Trends",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = AppStrings.get("daily_brain_activities", currentLang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Activity 1: Memory Matching Game
            ActivityCard(
                title = AppStrings.get("memory_matching", currentLang),
                subtitle = AppStrings.get("memory_matching_sub", currentLang),
                icon = Icons.Default.Style,
                color = PineGreen,
                tag = "activity_memory_game",
                onClick = { onNavigate("game_memory") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 2: Pattern Recognition Game
            ActivityCard(
                title = AppStrings.get("pattern_recognition", currentLang),
                subtitle = AppStrings.get("pattern_recognition_sub", currentLang),
                icon = Icons.Default.Extension,
                color = Color(0xFF2E7D32),
                tag = "activity_pattern_game",
                onClick = { onNavigate("game_pattern") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 3: Object Recognition Game
            ActivityCard(
                title = AppStrings.get("object_recognition", currentLang),
                subtitle = AppStrings.get("object_recognition_sub", currentLang),
                icon = Icons.Default.Category,
                color = Color(0xFF00796B),
                tag = "activity_object_game",
                onClick = { onNavigate("game_object") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 4: Dementia Reminiscence Therapy (Autobiographical Memory)
            ActivityCard(
                title = AppStrings.get("reminiscence_therapy", currentLang),
                subtitle = AppStrings.get("reminiscence_therapy_sub", currentLang),
                icon = Icons.Default.Favorite,
                color = Color(0xFF8B5CF6),
                tag = "activity_reminiscence_game",
                onClick = { onNavigate("game_reminiscence") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 5: Clinical Clock Orientation Test
            ActivityCard(
                title = AppStrings.get("clock_orientation", currentLang),
                subtitle = AppStrings.get("clock_orientation_sub", currentLang),
                icon = Icons.Default.AccessTime,
                color = Color(0xFFD97706),
                tag = "activity_clock_game",
                onClick = { onNavigate("game_clock") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 6: Reminders & Daily Schedule
            ActivityCard(
                title = AppStrings.get("reminders_title", currentLang),
                subtitle = AppStrings.get("reminders_sub", currentLang),
                icon = Icons.Default.Medication,
                color = ForestGreen,
                tag = "activity_reminders",
                onClick = { onNavigate("reminders") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 7: GPS Sentinel & Geofence
            ActivityCard(
                title = AppStrings.get("gps_title", currentLang),
                subtitle = AppStrings.get("gps_sub", currentLang),
                icon = Icons.Default.Security,
                color = Color(0xFF1E3A8A),
                tag = "activity_gps_sentinel",
                onClick = { onNavigate("gps_sentinel") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity 6: Caregiver Dashboard
            ActivityCard(
                title = AppStrings.get("caregiver_title", currentLang),
                subtitle = AppStrings.get("caregiver_sub", currentLang),
                icon = Icons.Default.Assessment,
                color = Color(0xFF4C1D95),
                tag = "activity_caregiver",
                onClick = { onNavigate("caregiver") }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Large Voice Assistant Button
            VoiceButton(
                isListening = isListening,
                isSpeaking = isSpeaking,
                onClick = {
                    showVoiceModal = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showVoiceModal) {
        VoiceControlModal(
            speechManager = speechManager,
            ttsManager = ttsManager,
            currentLang = currentLang,
            isOpen = showVoiceModal,
            onDismiss = { showVoiceModal = false },
            onNavigate = { target ->
                showVoiceModal = false
                onNavigate(target)
            }
        )
    }

    if (showLanguagePicker) {
        LanguagePickerModal(
            currentLangCode = currentLang,
            onLanguageSelected = { newLang ->
                SmritiRepository.setLanguage(newLang)
                ttsManager.speak("Language changed to ${VoiceAssistantService.SUPPORTED_LANGUAGES.find { it.code == newLang }?.name}", newLang)
            },
            onDismiss = { showLanguagePicker = false }
        )
    }
}

@Composable
private fun ActivityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF4A5568),
                    lineHeight = 18.sp
                )
            }

            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
