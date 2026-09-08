package com.example.smriti.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.SessionMetrics
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen

data class CulturalObjectItem(
    val emoji: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val culturalFact: String,
    val hint: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectGameScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val items = remember {
        listOf(
            CulturalObjectItem(
                emoji = "🛖",
                question = "What is this traditional dwelling made of bamboo and thatch?",
                options = listOf("Assamese Hut (Ghar)", "Brick Apartment", "Concrete Tent", "Boathouse"),
                correctIndex = 0,
                culturalFact = "Traditional North Eastern village homes are built with local bamboo and raised stilts (Chang Ghar) to stay cool and safe during monsoon seasons.",
                hint = "It is built with bamboo and eco-friendly natural materials."
            ),
            CulturalObjectItem(
                emoji = "🫖",
                question = "Which beverage item is famous across Assam tea gardens?",
                options = listOf("Chai Kettle (Cha Pot)", "Coffee Filter", "Juice Blender", "Soup Bowl"),
                correctIndex = 0,
                culturalFact = "Assam is renowned worldwide for rich, malty black tea grown on the fertile banks of the Brahmaputra River.",
                hint = "Used to brew fresh hot tea."
            ),
            CulturalObjectItem(
                emoji = "🪘",
                question = "Which festive percussion instrument is played during Bihu celebrations?",
                options = listOf("Bihu Dhol (Drum)", "Electric Guitar", "Saxophone", "Violin"),
                correctIndex = 0,
                culturalFact = "The Dhol is a two-sided drum crafted from jackfruit wood, central to the energetic spring Bihu dances of Assam.",
                hint = "It makes rhythmic beats played with bamboo sticks during festivals."
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var gameCompleted by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }

    val currentItem = items[currentIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Object Recognition", fontWeight = FontWeight.Bold, color = Color.White) },
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
            // Header Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Object ${currentIndex + 1} of ${items.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen
                )
                TextButton(
                    onClick = { showHint = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = PineGreen)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Need a Hint?")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Cultural Object Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .border(2.dp, EmeraldGreen, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentItem.emoji,
                        fontSize = 80.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = currentItem.question,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Options list
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentItem.options.forEachIndexed { optIndex, optionText ->
                    val isChosen = selectedOption == optIndex
                    val isCorrect = optIndex == currentItem.correctIndex

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = optIndex
                                if (isCorrect) {
                                    score++
                                    ttsManager.playChime()
                                }
                                showExplanation = true
                            }
                            .border(
                                width = 1.5.dp,
                                color = if (isChosen) PineGreen else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("object_opt_$optIndex"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) MintPastel else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isChosen) PineGreen else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + optIndex).toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChosen) Color.White else Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHint) {
        AlertDialog(
            onDismissRequest = { showHint = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Cultural Hint 💡", fontWeight = FontWeight.Bold) },
            text = { Text(currentItem.hint, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { showHint = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text("Got it")
                }
            }
        )
    }

    if (showExplanation) {
        val isCorrect = selectedOption == currentItem.correctIndex
        AlertDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    text = if (isCorrect) "Well Done! 🌟" else "Cultural Heritage Fact",
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) PineGreen else Color(0xFF1E3A8A)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Correct Answer: ${currentItem.options[currentItem.correctIndex]}",
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = currentItem.culturalFact,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExplanation = false
                        selectedOption = null
                        if (currentIndex < items.size - 1) {
                            currentIndex++
                        } else {
                            val elapsed = (System.currentTimeMillis() - startTime).toDouble()
                            val acc = (score.toDouble() / items.size.toDouble()) * 100.0
                            SmritiRepository.recordGameSession(
                                "Object Recognition",
                                SessionMetrics(
                                    accuracy = acc,
                                    responseTimeMs = elapsed / items.size,
                                    completionRate = 100.0,
                                    attempts = items.size,
                                    errors = items.size - score
                                )
                            )
                            gameCompleted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text(if (currentIndex < items.size - 1) "Next Object" else "Finish")
                }
            }
        )
    }

    if (gameCompleted) {
        AlertDialog(
            onDismissRequest = onBack,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Heritage Activity Complete! 🌿", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "You correctly identified $score out of ${items.size} regional objects!\nRecognizing familiar cultural symbols helps stimulate long-term semantic memory.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = PineGreen)) {
                    Text("Return Home")
                }
            }
        )
    }
}
