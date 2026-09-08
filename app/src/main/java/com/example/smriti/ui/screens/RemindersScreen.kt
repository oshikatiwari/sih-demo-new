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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.Reminder
import com.example.smriti.service.TtsManager
import com.example.smriti.service.VoiceAssistantService
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val reminders by SmritiRepository.reminders.collectAsState()
    val geofence by SmritiRepository.geofenceState.collectAsState()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ttsManager.speak(VoiceAssistantService.getScreenGuidance("reminders", currentLang), currentLang)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Care Schedule & Reminders", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_reminder_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Color.White)
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
                .padding(18.dp)
        ) {
            // Safe-Zone Sentinel Chip
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (geofence.isInSafeZone) EmeraldGreen else Color(0xFFDC2626), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (geofence.isInSafeZone) MintPastel else Color(0xFFFEF2F2)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (geofence.isInSafeZone) Icons.Default.Shield else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (geofence.isInSafeZone) PineGreen else Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (geofence.isInSafeZone) "Safe-Zone Active • Inside Home Boundary" else "Sentinel Notice • Boundary Check",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (geofence.isInSafeZone) ForestGreen else Color(0xFF991B1B)
                        )
                        Text(
                            text = "Current distance: ${geofence.distanceMeters.toInt()}m from home center",
                            fontSize = 12.sp,
                            color = Color(0xFF4A5568)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Today's Schedule",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reminders) { item ->
                    ReminderItemCard(
                        reminder = item,
                        onToggle = {
                            SmritiRepository.toggleReminder(item.id)
                            if (!item.isCompleted) {
                                ttsManager.playChime()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, sub, time, type ->
                SmritiRepository.addReminder(title, sub, time, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ReminderItemCard(
    reminder: Reminder,
    onToggle: () -> Unit
) {
    val icon: ImageVector = when (reminder.type) {
        "medication" -> Icons.Default.Medication
        "hydration" -> Icons.Default.WaterDrop
        "game" -> Icons.Default.Extension
        "walk" -> Icons.Default.DirectionsWalk
        else -> Icons.Default.Alarm
    }

    val iconBgColor: Color = when (reminder.type) {
        "medication" -> Color(0xFFE0F2FE)
        "hydration" -> Color(0xFFEFF6FF)
        "game" -> MintPastel
        "walk" -> Color(0xFFFEF3C7)
        else -> Color(0xFFF1F5F9)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .border(
                width = 1.dp,
                color = if (reminder.isCompleted) Color(0xFFCBD5E1) else EmeraldGreen,
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("reminder_item_${reminder.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) Color(0xFFF8FAFC) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (reminder.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PineGreen, modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reminder.time,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PineGreen
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reminder.isCompleted) Color.Gray else ForestGreen,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.subtitle,
                    fontSize = 13.sp,
                    color = if (reminder.isCompleted) Color.LightGray else Color(0xFF4A5568),
                    lineHeight = 17.sp
                )
            }

            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PineGreen,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, subtitle: String, time: String, type: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("04:00 PM") }
    var type by remember { mutableStateOf("medication") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = { Text("Add Care Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title (e.g., Evening Medicine)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_reminder_title")
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Guidance (e.g., Take with 1 glass of water)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_reminder_sub")
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Scheduled Time") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_reminder_time")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, subtitle.ifBlank { "Gentle daily care reminder" }, time, type)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PineGreen)
            ) {
                Text("Add Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
