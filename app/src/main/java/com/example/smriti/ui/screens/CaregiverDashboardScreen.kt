package com.example.smriti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.smriti.model.GameSessionRecord
import com.example.smriti.model.Patient
import com.example.smriti.service.AnomalyDetector
import com.example.smriti.service.AppStrings
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.components.CognitiveTrendChart
import com.example.smriti.ui.theme.AlertBackground
import com.example.smriti.ui.theme.AlertRed
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverDashboardScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit,
    onOpenGps: () -> Unit
) {
    val selectedPatientId by SmritiRepository.selectedPatientId.collectAsState()
    val allSessions by SmritiRepository.patientSessions.collectAsState()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    val currentPatient = SmritiRepository.getActivePatient()
    val sessions = allSessions[selectedPatientId] ?: emptyList()
    val latestScore = sessions.lastOrNull()?.cps ?: 80.0
    val anomaly = SmritiRepository.checkPatientAnomaly(selectedPatientId)

    var showContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("caregiver_dashboard", currentLang), fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.get("back", currentLang), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenGps) {
                        Icon(Icons.Default.LocationOn, contentDescription = AppStrings.get("gps_sentinel_title", currentLang), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PineGreen)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6F4))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Selector
            item {
                Text(
                    text = AppStrings.get("select_patient", currentLang),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SmritiRepository.PATIENTS.forEach { patient ->
                        val isSelected = patient.id == selectedPatientId
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { SmritiRepository.selectPatient(patient.id) }
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) PineGreen else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .testTag("select_patient_${patient.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MintPastel else Color.White
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = patient.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ForestGreen
                                )
                                Text(
                                    text = "Age ${patient.age} • ${patient.conditionTag}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4A5568)
                                )
                            }
                        }
                    }
                }
            }

            // Anomaly Alert Banner (if alert detected for this patient)
            if (anomaly != null && anomaly.isAlertTriggered) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, AlertRed, RoundedCornerShape(18.dp))
                            .testTag("anomaly_alert_banner"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AlertBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AlertRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Caregiver Support Notice",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = AlertRed
                                    )
                                    Text(
                                        text = "Activity Pattern Shift Detected",
                                        fontSize = 12.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = anomaly.message,
                                fontSize = 14.sp,
                                color = Color(0xFF7F1D1D),
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Prior 3-Session Avg: ${anomaly.priorAvg}", fontSize = 12.sp, color = Color.Gray)
                                Text("Recent Avg: ${anomaly.recentAvg}", fontSize = 12.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                                Text("${anomaly.pctChange}%", fontSize = 12.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Overview Metric Tiles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricTile(
                        label = "Latest CPS Score",
                        value = "$latestScore",
                        subtitle = currentPatient.currentDisplayLabel,
                        color = PineGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Trend Status",
                        value = if (anomaly != null) "Review" else "Steady",
                        subtitle = "${sessions.size} sessions recorded",
                        color = if (anomaly != null) Color(0xFFD97706) else EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive Longitudinal Trend Analysis Chart
            item {
                CognitiveTrendChart(sessions = sessions)
            }

            // Quick Caregiver Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showContactDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("caregiver_call_button")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Patient")
                    }

                    OutlinedButton(
                        onClick = onOpenGps,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PineGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("caregiver_gps_button")
                    ) {
                        Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sentinel Radar")
                    }
                }
            }

            // Session History Section Header
            item {
                Text(
                    text = "Cognitive Session History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            }

            // Session Records
            items(sessions.reversed()) { record ->
                SessionRecordCard(record)
            }
        }
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = { Text("Direct Check-in", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Patient: ${currentPatient.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Emergency Contact: ${currentPatient.emergencyContactName}")
                    Text("Phone: ${currentPatient.emergencyContactPhone}")
                    Text("Safe Zone Address: ${currentPatient.safeZoneAddress}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContactDialog = false
                        ttsManager.speak("Initiating check-in call to ${currentPatient.name}", "en")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
                ) {
                    Text("Place Call")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF4A5568))
        }
    }
}

@Composable
private fun SessionRecordCard(record: GameSessionRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MintPastel),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${record.cps.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PineGreen
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.gameType,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${record.formattedDate} • ${record.displayLabel}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${record.accuracy.toInt()}% Acc",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
            }
        }
    }
}
