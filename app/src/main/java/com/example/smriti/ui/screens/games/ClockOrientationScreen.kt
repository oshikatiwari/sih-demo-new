package com.example.smriti.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.SessionMetrics
import com.example.smriti.service.AppStrings
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlin.math.cos
import kotlin.math.sin

data class ClockChallenge(
    val targetHour: Int,
    val targetMinute: Int,
    val promptEn: String,
    val promptHi: String,
    val promptAs: String,
    val promptMzo: String,
    val promptKha: String,
    val timeOfDay: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockOrientationScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    val challenges = remember {
        listOf(
            ClockChallenge(
                targetHour = 10,
                targetMinute = 10,
                promptEn = "Set the clock to 10 past 10 (10:10) — The classic clinical cognitive test time.",
                promptHi = "घड़ी में 10 बजकर 10 मिनट (10:10) सेट करें — यह स्मृति परीक्षण का मानक समय है।",
                promptAs = "ঘড়ীত ১০ বাজি ১০ মিনিট (১০:১০) স্থিৰ কৰক — এইটো মান্য স্মৃতি পৰীক্ষণৰ সময়।",
                promptMzo = "Sana hi dar 10:10-ah set rawh le — He hi hriatna fiahna hun pawimawh a ni.",
                promptKha = "Pyni ia ka baje sha ka 10:10 — Ka rukom pynithuh por ba kyrpang.",
                timeOfDay = "Mid-Morning Tea Time"
            ),
            ClockChallenge(
                targetHour = 3,
                targetMinute = 0,
                promptEn = "Set the clock to 3 o'clock sharp (3:00) — Peaceful afternoon tea time.",
                promptHi = "घड़ी में ठीक 3:00 बजे का समय सेट करें — दोपहर की हल्की चाय का समय।",
                promptAs = "ঘড়ীত ঠিক ৩:০০ বজাৰ সময় নিৰ্ধাৰণ কৰক — দুপৰীয়াৰ বিশ্ৰামৰ সময়।",
                promptMzo = "Sana hi dar 3:00-ah set rawh le — Chawhnu thingpui in hun.",
                promptKha = "Pyni ia ka baje sha ka 3:00 — Ka por dih sha nohphyrngap.",
                timeOfDay = "Afternoon Relaxation"
            ),
            ClockChallenge(
                targetHour = 8,
                targetMinute = 30,
                promptEn = "Set the clock to half past eight (8:30) — Gentle morning routine.",
                promptHi = "घड़ी में 8 बजकर 30 मिनट (8:30) सेट करें — सुबह की दवा का समय।",
                promptAs = "ঘড়ীত ৮ বাজি ৩০ মিনিট (৮:৩০) স্থিৰ কৰক — পুৱাৰ ঔষধৰ সময়।",
                promptMzo = "Sana hi dar 8:30-ah set rawh le — Tukthuan damdawi ei hun.",
                promptKha = "Pyni ia ka baje sha ka 8:30 — Ka por dih dawai mynta step.",
                timeOfDay = "Morning Care Time"
            )
        )
    }

    var currentChallengeIndex by remember { mutableIntStateOf(0) }
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var attempts by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }

    val challenge = challenges[currentChallengeIndex]

    val prompt = when (currentLang) {
        "hi" -> challenge.promptHi
        "as" -> challenge.promptAs
        "mzo" -> challenge.promptMzo
        "kha" -> challenge.promptKha
        else -> challenge.promptEn
    }

    val isCorrect = selectedHour == challenge.targetHour && selectedMinute == challenge.targetMinute

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("clock_orientation", currentLang),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.get("back", currentLang), tint = Color.White)
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
                .background(Color(0xFFF4F6F4))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Clinical Badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintPastel),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Standard Clock Drawing & Temporal Orientation Test",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Assesses executive function, visuospatial skills, & parietal lobe memory",
                            fontSize = 11.sp,
                            color = Color(0xFF2D3748)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task Prompt Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Exercise ${currentChallengeIndex + 1} of ${challenges.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PineGreen
                        )
                        Text(
                            text = challenge.timeOfDay,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = prompt,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            ttsManager.speak(prompt, currentLang)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintPastel),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Listen to Instructions", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Analog Clock Canvas
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(5.dp, PineGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val r = size.width / 2

                    // Hour ticks and numerals
                    for (i in 1..12) {
                        val angleDeg = i * 30.0 - 90.0
                        val angleRad = Math.toRadians(angleDeg)
                        val tickStart = Offset(
                            (center.x + (r - 18.dp.toPx()) * cos(angleRad)).toFloat(),
                            (center.y + (r - 18.dp.toPx()) * sin(angleRad)).toFloat()
                        )
                        val tickEnd = Offset(
                            (center.x + (r - 6.dp.toPx()) * cos(angleRad)).toFloat(),
                            (center.y + (r - 6.dp.toPx()) * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = Color(0xFF1B4332),
                            start = tickStart,
                            end = tickEnd,
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Hour Hand
                    val hourAngleDeg = (selectedHour % 12 + selectedMinute / 60.0) * 30.0 - 90.0
                    val hourAngleRad = Math.toRadians(hourAngleDeg)
                    val hourHandLength = r * 0.52f
                    val hourEnd = Offset(
                        (center.x + hourHandLength * cos(hourAngleRad)).toFloat(),
                        (center.y + hourHandLength * sin(hourAngleRad)).toFloat()
                    )
                    drawLine(
                        color = Color(0xFF1B4332),
                        start = center,
                        end = hourEnd,
                        strokeWidth = 7f,
                        cap = StrokeCap.Round
                    )

                    // Draw Minute Hand
                    val minAngleDeg = selectedMinute * 6.0 - 90.0
                    val minAngleRad = Math.toRadians(minAngleDeg)
                    val minHandLength = r * 0.78f
                    val minEnd = Offset(
                        (center.x + minHandLength * cos(minAngleRad)).toFloat(),
                        (center.y + minHandLength * sin(minAngleRad)).toFloat()
                    )
                    drawLine(
                        color = Color(0xFF2D6A4F),
                        start = center,
                        end = minEnd,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )

                    // Center Pin
                    drawCircle(
                        color = Color(0xFFD8F3DC),
                        radius = 8.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFF1B4332),
                        radius = 4.dp.toPx(),
                        center = center
                    )
                }

                // Digital overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    val minStr = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
                    Text(
                        text = "$selectedHour:$minStr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sliders / Selectors for Hands (Accessible, elder-friendly large touch buttons)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Set Hour Hand: $selectedHour",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                selectedHour = if (selectedHour <= 1) 12 else selectedHour - 1
                            },
                            modifier = Modifier.testTag("clock_hour_minus")
                        ) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease hour", tint = PineGreen, modifier = Modifier.size(32.dp))
                        }
                        Slider(
                            value = selectedHour.toFloat(),
                            onValueChange = { selectedHour = it.toInt() },
                            valueRange = 1f..12f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = PineGreen, activeTrackColor = PineGreen),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                selectedHour = if (selectedHour >= 12) 1 else selectedHour + 1
                            },
                            modifier = Modifier.testTag("clock_hour_plus")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Increase hour", tint = PineGreen, modifier = Modifier.size(32.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Set Minute Hand: ${if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ForestGreen
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                selectedMinute = if (selectedMinute <= 0) 55 else selectedMinute - 5
                            },
                            modifier = Modifier.testTag("clock_min_minus")
                        ) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Decrease minute", tint = PineGreen, modifier = Modifier.size(32.dp))
                        }
                        Slider(
                            value = selectedMinute.toFloat(),
                            onValueChange = { selectedMinute = (it / 5).toInt() * 5 },
                            valueRange = 0f..55f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = PineGreen, activeTrackColor = PineGreen),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                selectedMinute = if (selectedMinute >= 55) 0 else selectedMinute + 5
                            },
                            modifier = Modifier.testTag("clock_min_plus")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Increase minute", tint = PineGreen, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Check Answer / Action
            Button(
                onClick = {
                    attempts++
                    isSubmitted = true
                    if (isCorrect) {
                        score++
                        ttsManager.playChime()
                        ttsManager.speak("Excellent! The clock is set perfectly to ${challenge.targetHour}:${if (challenge.targetMinute < 10) "0${challenge.targetMinute}" else "${challenge.targetMinute}"}.", currentLang)
                    } else {
                        ttsManager.playAlertTone()
                        ttsManager.speak("Gentle reminder: The hour hand points towards ${challenge.targetHour} and minute hand towards ${challenge.targetMinute}. You can adjust with no hurry.", currentLang)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("check_clock_button")
            ) {
                Text(
                    text = "Verify Clock Setting",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            AnimatedVisibility(visible = isSubmitted) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFFD8F3DC) else Color(0xFFFEF3C7)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isCorrect) EmeraldGreen else Color(0xFFD97706)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isCorrect) {
                                    "Perfect orientation! You identified both the hour and minute correctly."
                                } else {
                                    "Target was ${challenge.targetHour}:${if (challenge.targetMinute < 10) "0${challenge.targetMinute}" else "${challenge.targetMinute}"}. Adjust gently or continue."
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (currentChallengeIndex < challenges.size - 1) {
                                currentChallengeIndex++
                                selectedHour = 12
                                selectedMinute = 0
                                isSubmitted = false
                            } else {
                                val elapsedMs = (System.currentTimeMillis() - startTime).toDouble()
                                val acc = (score.toDouble() / challenges.size * 100.0).coerceIn(0.0, 100.0)
                                val metrics = SessionMetrics(
                                    accuracy = acc,
                                    responseTimeMs = elapsedMs / attempts.coerceAtLeast(1),
                                    completionRate = 100.0,
                                    attempts = attempts,
                                    errors = attempts - score,
                                    isMemoryGame = false,
                                    memorySpecificAccuracy = acc
                                )
                                SmritiRepository.recordGameSession("Clock Orientation", metrics)
                                showDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (currentChallengeIndex < challenges.size - 1) "Next Clock Exercise" else "Complete Clock Test",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onBack()
            },
            title = {
                Text(
                    text = "Clock Orientation Completed!",
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Score: $score / ${challenges.size} accurately positioned",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PineGreen
                    )
                    Text(
                        text = "• Clock Drawing is clinically recommended for early cognitive screening.\n• Promotes spatial reasoning, numbers sequencing, and temporal grounding.\n• Results logged to Caregiver Dashboard.",
                        fontSize = 13.sp,
                        color = Color(0xFF4A5568),
                        lineHeight = 19.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text(AppStrings.get("back_to_home", currentLang))
                }
            }
        )
    }
}
