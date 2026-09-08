package com.example.smriti.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.service.TtsManager
import com.example.smriti.service.VoiceAssistantService
import com.example.smriti.ui.theme.AlertBackground
import com.example.smriti.ui.theme.AlertRed
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsSentinelScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val geofence by SmritiRepository.geofenceState.collectAsState()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()
    var activeMode by remember { mutableStateOf("radar") } // "radar" or "beacon"

    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        ttsManager.speak(VoiceAssistantService.getScreenGuidance("gps", currentLang), currentLang)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GPS Safe-Zone Sentinel", fontWeight = FontWeight.Bold, color = Color.White) },
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
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Switcher (Guardian Radar vs Beacon Tracker)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeMode == "radar") PineGreen else Color.Transparent)
                            .clickable { activeMode = "radar" }
                            .padding(vertical = 10.dp)
                            .testTag("mode_radar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Guardian Radar",
                            fontWeight = FontWeight.Bold,
                            color = if (activeMode == "radar") Color.White else Color.DarkGray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeMode == "beacon") PineGreen else Color.Transparent)
                            .clickable { activeMode = "beacon" }
                            .padding(vertical = 10.dp)
                            .testTag("mode_beacon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Beacon Tracker",
                            fontWeight = FontWeight.Bold,
                            color = if (activeMode == "beacon") Color.White else Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Radar Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .border(2.dp, if (geofence.isInSafeZone) PineGreen else AlertRed, RoundedCornerShape(24.dp))
                    .testTag("radar_canvas_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2818))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxR = size.minDimension / 2f

                        // Concentric range circles
                        for (i in 1..3) {
                            val r = maxR * (i / 3f)
                            drawCircle(
                                color = Color(0xFF2D6A4F).copy(alpha = 0.5f),
                                radius = r,
                                center = center,
                                style = Stroke(width = 1.5f)
                            )
                        }

                        // Safe zone perimeter circle
                        val safeR = (maxR * (geofence.radiusMeters / 300.0).coerceIn(0.2, 0.95)).toFloat()
                        drawCircle(
                            color = if (geofence.isInSafeZone) Color(0xFF52B788).copy(alpha = 0.25f) else Color(0xFFDC2626).copy(alpha = 0.25f),
                            radius = safeR,
                            center = center
                        )
                        drawCircle(
                            color = if (geofence.isInSafeZone) Color(0xFF52B788) else Color(0xFFDC2626),
                            radius = safeR,
                            center = center,
                            style = Stroke(width = 2.5f)
                        )

                        // Radar sweep line
                        val rad = Math.toRadians(sweepAngle.toDouble())
                        val endX = center.x + maxR * cos(rad).toFloat()
                        val endY = center.y + maxR * sin(rad).toFloat()
                        drawLine(
                            color = Color(0xFF74C69D).copy(alpha = 0.6f),
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 2f
                        )

                        // Home Center Marker
                        drawCircle(
                            color = Color.White,
                            radius = 6f,
                            center = center
                        )

                        // Device / Patient Marker
                        val devDistR = (safeR * (geofence.distanceMeters / geofence.radiusMeters)).toFloat()
                        val devOffset = Offset(
                            center.x + devDistR * 0.7f,
                            center.y - devDistR * 0.7f
                        )

                        // Pulsing circle
                        drawCircle(
                            color = if (geofence.isInSafeZone) Color(0xFF52B788).copy(alpha = 0.35f) else Color(0xFFDC2626).copy(alpha = 0.35f),
                            radius = 16f * pulseRadius,
                            center = devOffset
                        )
                        drawCircle(
                            color = if (geofence.isInSafeZone) Color(0xFF52B788) else AlertRed,
                            radius = 9f,
                            center = devOffset
                        )
                    }

                    // Compass HUD text
                    Text(
                        text = if (geofence.isInSafeZone) "SAFE ZONE LOCKED" else "OUTSIDE SAFE ZONE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (geofence.isInSafeZone) Color(0xFF74C69D) else AlertRed,
                        modifier = Modifier.align(Alignment.TopStart).padding(14.dp)
                    )

                    Text(
                        text = "GPS Fix: Active",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.TopEnd).padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (geofence.isInSafeZone) Color.White else AlertBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (geofence.isInSafeZone) "Patient Inside Perimeter" else "Boundary Breach Alert!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (geofence.isInSafeZone) ForestGreen else AlertRed
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (geofence.isInSafeZone) MintPastel else Color(0xFFFCA5A5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${geofence.distanceMeters.toInt()}m from Home",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (geofence.isInSafeZone) PineGreen else AlertRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Last verified location ping: ${geofence.lastPingTime} • Device Battery: ${geofence.batteryPct}%",
                        fontSize = 13.sp,
                        color = Color(0xFF4A5568)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Radius Slider
            Text(
                text = "Safe-Zone Radius: ${geofence.radiusMeters.toInt()} meters",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )

            Slider(
                value = geofence.radiusMeters.toFloat(),
                onValueChange = { SmritiRepository.updateGeofenceRadius(it.toDouble()) },
                valueRange = 50f..500f,
                colors = SliderDefaults.colors(
                    thumbColor = PineGreen,
                    activeTrackColor = PineGreen
                ),
                modifier = Modifier.testTag("geofence_radius_slider")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Breach Simulation Trigger
            Button(
                onClick = {
                    val willBreach = geofence.isInSafeZone
                    SmritiRepository.simulateBreach(willBreach)
                    if (willBreach) {
                        ttsManager.playAlertTone()
                        ttsManager.speak("Attention: Boundary alert triggered. Patient has moved outside designated perimeter.", "en")
                    } else {
                        ttsManager.playChime()
                        ttsManager.speak("Boundary alert cleared. Patient is safely inside perimeter.", "en")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (geofence.isInSafeZone) AlertRed else PineGreen
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_breach_button")
            ) {
                Icon(
                    imageVector = if (geofence.isInSafeZone) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (geofence.isInSafeZone) "Simulate Safe Zone Breach" else "Restore Patient Inside Boundary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Authorized Guardians List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Authorized Emergency Guardians",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    geofence.authorizedGuardians.forEach { guardian ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(guardian, fontSize = 13.sp, color = Color(0xFF4A5568))
                        }
                    }
                }
            }
        }
    }
}
