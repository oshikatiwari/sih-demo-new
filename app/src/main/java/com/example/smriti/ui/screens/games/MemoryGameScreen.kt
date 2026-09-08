package com.example.smriti.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.CpsResult
import com.example.smriti.model.SessionMetrics
import com.example.smriti.service.AppStrings
import com.example.smriti.service.TtsManager
import com.example.smriti.service.VoiceAssistantService
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    // 6 pairs of cultural & regional symbols
    val initialIcons = remember {
        listOf("☕", "☕", "🌸", "🌸", "🦏", "🦏", "🎋", "🎋", "🪘", "🪘", "🛖", "🛖").shuffled()
    }
    var cardIcons by remember { mutableStateOf(initialIcons) }
    var cardFlipped by remember { mutableStateOf(List(12) { false }) }
    var cardMatched by remember { mutableStateOf(List(12) { false }) }

    var previousIndex by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    var attempts by remember { mutableIntStateOf(0) }
    var matchesFound by remember { mutableIntStateOf(0) }
    var errors by remember { mutableIntStateOf(0) }
    var startTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var showResultDialog by remember { mutableStateOf(false) }
    var finalResult by remember { mutableStateOf<CpsResult?>(null) }
    var finalAccuracy by remember { mutableDoubleStateOf(0.0) }

    fun resetGame() {
        cardIcons = initialIcons.shuffled()
        cardFlipped = List(12) { false }
        cardMatched = List(12) { false }
        previousIndex = null
        isBusy = false
        attempts = 0
        matchesFound = 0
        errors = 0
        startTimeMs = System.currentTimeMillis()
        showResultDialog = false
        finalResult = null
    }

    LaunchedEffect(Unit) {
        ttsManager.speak(VoiceAssistantService.getScreenGuidance("gameplay", currentLang), currentLang)
    }

    fun onCardClick(index: Int) {
        if (isBusy || cardFlipped[index] || cardMatched[index]) return

        // Flip this card
        cardFlipped = cardFlipped.toMutableList().also { it[index] = true }

        if (previousIndex == null) {
            previousIndex = index
        } else {
            attempts++
            isBusy = true
            val prev = previousIndex!!
            previousIndex = null

            if (cardIcons[prev] == cardIcons[index]) {
                // Match!
                coroutineScope.launch {
                    ttsManager.playChime()
                    cardMatched = cardMatched.toMutableList().also {
                        it[prev] = true
                        it[index] = true
                    }
                    matchesFound++
                    isBusy = false

                    if (matchesFound == cardIcons.size / 2) {
                        val elapsedMs = (System.currentTimeMillis() - startTimeMs).toDouble()
                        val acc = ((cardIcons.size / 2.0) / attempts.coerceAtLeast(1) * 100.0).coerceIn(0.0, 100.0)
                        finalAccuracy = acc

                        val metrics = SessionMetrics(
                            accuracy = acc,
                            responseTimeMs = elapsedMs / attempts.coerceAtLeast(1),
                            completionRate = 100.0,
                            attempts = attempts,
                            errors = errors,
                            isMemoryGame = true,
                            memorySpecificAccuracy = acc
                        )

                        val result = SmritiRepository.recordGameSession("Memory Matching", metrics)
                        finalResult = result
                        showResultDialog = true

                        ttsManager.speak(
                            "Great job! Your cognitive practice score is ${result.cps}. Practice level: ${result.displayLabel}",
                            currentLang
                        )
                    }
                }
            } else {
                // Mismatch
                errors++
                coroutineScope.launch {
                    delay(800)
                    cardFlipped = cardFlipped.toMutableList().also {
                        it[prev] = false
                        it[index] = false
                    }
                    isBusy = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("memory_matching", currentLang), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.get("back", currentLang), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            ttsManager.speak(VoiceAssistantService.getScreenGuidance("gameplay", currentLang), currentLang)
                        },
                        modifier = Modifier.testTag("action_read_guidance")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen to guidance", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            SmritiRepository.triggerEmergencyReminder("SOS triggered from Memory Matching game")
                            ttsManager.playAlertTone()
                            ttsManager.speak("Emergency reminder dispatched to caregiver.", currentLang)
                        },
                        modifier = Modifier.testTag("action_sos_game")
                    ) {
                        Icon(Icons.Default.Emergency, contentDescription = "Emergency Alert", tint = Color(0xFFFCA5A5))
                    }

                    IconButton(onClick = { resetGame() }) {
                        Icon(Icons.Default.Refresh, contentDescription = AppStrings.get("play_again", currentLang), tint = Color.White)
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
                .padding(16.dp)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pairs", fontSize = 13.sp, color = Color.Gray)
                    Text("$matchesFound / 6", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PineGreen)
                }
                Box(modifier = Modifier.height(28.dp).width(1.dp).background(Color(0xFFE2E8F0)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(AppStrings.get("attempts", currentLang), fontSize = 13.sp, color = Color.Gray)
                    Text("$attempts", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
                Box(modifier = Modifier.height(28.dp).width(1.dp).background(Color(0xFFE2E8F0)))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(AppStrings.get("accuracy", currentLang), fontSize = 13.sp, color = Color.Gray)
                    Text("${((matchesFound.toDouble() / attempts.coerceAtLeast(1)) * 100).toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = EmeraldGreen)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3x4 Grid of Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cardIcons.size) { index ->
                    val isRevealed = cardFlipped[index] || cardMatched[index]
                    val isMatched = cardMatched[index]

                    val rotation by animateFloatAsState(
                        targetValue = if (isRevealed) 180f else 0f,
                        animationSpec = tween(durationMillis = 350),
                        label = "cardFlip"
                    )

                    val backgroundColor by animateColorAsState(
                        targetValue = when {
                            isMatched -> Color(0xFFD8F3DC)
                            isRevealed -> Color.White
                            else -> PineGreen
                        },
                        label = "bgColor"
                    )

                    Card(
                        modifier = Modifier
                            .aspectRatio(0.85f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable(enabled = !isRevealed && !isBusy) {
                                onCardClick(index)
                            }
                            .testTag("memory_card_$index"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.dp,
                            color = if (isMatched) EmeraldGreen else if (isRevealed) Color(0xFFCBD5E1) else EmeraldGreen
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (rotation > 90f) {
                                Text(
                                    text = cardIcons[index],
                                    fontSize = 42.sp,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                                )
                            } else {
                                Text(
                                    text = "✦",
                                    fontSize = 28.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Results Dialog
    if (showResultDialog && finalResult != null) {
        val result = finalResult!!
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Activity Completed!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Accuracy: ${finalAccuracy.toInt()}%",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Cognitive Score: ", fontSize = 17.sp)
                        Text(
                            text = "${result.cps}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PineGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MintPastel)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Support Level: ${result.displayLabel}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }

                    // Vector Analytics Breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Accuracy:", fontSize = 12.sp, color = Color.Gray)
                            Text("${result.accuracyScore.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Reaction Speed:", fontSize = 12.sp, color = Color.Gray)
                            Text("${result.speedScore.toInt()}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Memory Recall:", fontSize = 12.sp, color = Color.Gray)
                            Text("${result.memoryScore.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }

                    Text(
                        text = "Great job completing your brain exercise today! Regular practice keeps memory active.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Return Home")
                }
            },
            dismissButton = {
                TextButton(onClick = { resetGame() }) {
                    Text("Play Again", color = PineGreen)
                }
            }
        )
    }
}
