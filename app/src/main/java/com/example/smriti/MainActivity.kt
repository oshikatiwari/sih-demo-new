package com.example.smriti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.smriti.data.SmritiRepository
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.screens.*
import com.example.smriti.ui.screens.games.ClockOrientationScreen
import com.example.smriti.ui.screens.games.MemoryGameScreen
import com.example.smriti.ui.screens.games.ObjectGameScreen
import com.example.smriti.ui.screens.games.PatternGameScreen
import com.example.smriti.ui.screens.games.ReminiscenceTherapyScreen
import com.example.smriti.ui.theme.SandBackground
import com.example.smriti.ui.theme.SmritiTheme

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TtsManager
    private lateinit var speechManager: com.example.smriti.service.SpeechToTextManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ttsManager = TtsManager(this)
        speechManager = com.example.smriti.service.SpeechToTextManager(this)

        setContent {
            SmritiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SandBackground
                ) {
                    var currentRoute by remember { mutableStateOf("home") }

                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screen_transition"
                    ) { route ->
                        when (route) {
                            "login" -> LoginScreen(
                                onLoginSuccess = { role ->
                                    if (role == "caregiver") {
                                        currentRoute = "caregiver"
                                    } else {
                                        currentRoute = "home"
                                    }
                                }
                            )
                            "home" -> HomeScreen(
                                ttsManager = ttsManager,
                                speechManager = speechManager,
                                onNavigate = { target -> currentRoute = target },
                                onLogout = { currentRoute = "login" }
                            )
                            "caregiver" -> CaregiverDashboardScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" },
                                onOpenGps = { currentRoute = "gps_sentinel" }
                            )
                            "reminders" -> RemindersScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "gps_sentinel" -> GpsSentinelScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "game_memory" -> MemoryGameScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "game_pattern" -> PatternGameScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "game_object" -> ObjectGameScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "game_reminiscence" -> ReminiscenceTherapyScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            "game_clock" -> ClockOrientationScreen(
                                ttsManager = ttsManager,
                                onBack = { currentRoute = "home" }
                            )
                            else -> HomeScreen(
                                ttsManager = ttsManager,
                                speechManager = speechManager,
                                onNavigate = { target -> currentRoute = target },
                                onLogout = { currentRoute = "login" }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.stopListening()
        ttsManager.release()
    }
}
