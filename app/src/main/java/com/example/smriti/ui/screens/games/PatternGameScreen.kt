package com.example.smriti.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.SessionMetrics
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PatternQuestion(
    val sequence: List<String>,
    val options: List<String>,
    val correctAnswer: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternGameScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    val questions = remember {
        listOf(
            PatternQuestion(
                sequence = listOf("🔴", "🔵", "🔴", "🔵", "❓"),
                options = listOf("🔴", "🔵", "🟢", "🟡"),
                correctAnswer = "🔴",
                description = "Alternating colored circles"
            ),
            PatternQuestion(
                sequence = listOf("🌸", "🎋", "🌸", "🎋", "❓"),
                options = listOf("🌸", "🎋", "🦏", "☕"),
                correctAnswer = "🌸",
                description = "Blooming flower and bamboo cane"
            ),
            PatternQuestion(
                sequence = listOf("☕", "🪘", "🪘", "☕", "🪘", "❓"),
                options = listOf("🪘", "☕", "🛖", "🌸"),
                correctAnswer = "🪘",
                description = "Traditional tea and rhythmic dhol drum"
            )
        )
    }

    var currentRound by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var isCorrectFeedback by remember { mutableStateOf(false) }
    var gameCompleted by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }

    val currentQ = questions[currentRound]

    fun checkAnswer(option: String) {
        val isCorrect = option == currentQ.correctAnswer
        isCorrectFeedback = isCorrect

        if (isCorrect) {
            score++
            ttsManager.playChime()
        }

        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pattern Recognition", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Round indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sequence ${currentRound + 1} of ${questions.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen
                )
                Text(
                    text = "Score: $score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Complete the Sequence:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sequence row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(2.dp, EmeraldGreen, RoundedCornerShape(20.dp))
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentQ.sequence.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (item == "❓") MintPastel else Color(0xFFF8F9FA))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            fontSize = if (item == "❓") 26.sp else 30.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Which symbol comes next?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Options 2x2 Grid
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PatternOptionButton(
                        symbol = currentQ.options[0],
                        modifier = Modifier.weight(1f),
                        onClick = { checkAnswer(currentQ.options[0]) }
                    )
                    PatternOptionButton(
                        symbol = currentQ.options[1],
                        modifier = Modifier.weight(1f),
                        onClick = { checkAnswer(currentQ.options[1]) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PatternOptionButton(
                        symbol = currentQ.options[2],
                        modifier = Modifier.weight(1f),
                        onClick = { checkAnswer(currentQ.options[2]) }
                    )
                    PatternOptionButton(
                        symbol = currentQ.options[3],
                        modifier = Modifier.weight(1f),
                        onClick = { checkAnswer(currentQ.options[3]) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    text = if (isCorrectFeedback) "Correct Pattern! ✨" else "Try Carefully",
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrectFeedback) PineGreen else Color(0xFFD97706)
                )
            },
            text = {
                Text(
                    text = if (isCorrectFeedback)
                        "Wonderful job identifying the repeating pattern (${currentQ.description})."
                    else
                        "Notice how the symbols repeat in order. Take your time to review.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        if (currentRound < questions.size - 1) {
                            currentRound++
                        } else {
                            // Finished all rounds
                            val elapsed = (System.currentTimeMillis() - startTime).toDouble()
                            val acc = (score.toDouble() / questions.size.toDouble()) * 100.0
                            SmritiRepository.recordGameSession(
                                "Pattern Recognition",
                                SessionMetrics(
                                    accuracy = acc,
                                    responseTimeMs = elapsed / questions.size,
                                    completionRate = 100.0,
                                    attempts = questions.size,
                                    errors = questions.size - score
                                )
                            )
                            gameCompleted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text(if (currentRound < questions.size - 1) "Next Round" else "View Results")
                }
            }
        )
    }

    if (gameCompleted) {
        AlertDialog(
            onDismissRequest = onBack,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PineGreen, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pattern Activity Complete!")
                }
            },
            text = {
                Text(
                    text = "You scored $score out of ${questions.size} correct!\nYour pattern recognition exercise has been recorded in your care history.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text("Return Home")
                }
            }
        )
    }
}

@Composable
private fun PatternOptionButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(88.dp)
            .clickable(onClick = onClick)
            .border(2.dp, PineGreen, RoundedCornerShape(20.dp))
            .testTag("pattern_opt_$symbol"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, fontSize = 38.sp)
        }
    }
}
