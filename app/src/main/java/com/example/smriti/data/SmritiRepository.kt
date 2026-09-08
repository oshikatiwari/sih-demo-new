package com.example.smriti.data

import com.example.smriti.model.*
import com.example.smriti.service.AnomalyDetector
import com.example.smriti.service.CpsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object SmritiRepository {

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    // Supported Patients
    val PATIENTS = listOf(
        Patient(
            id = "demo-patient-01",
            name = "Aarav Sharma",
            age = 74,
            conditionTag = "Mild Cognitive Impairment",
            statusSummary = "Steady Practice • Consistent engagement",
            currentTier = "Medium-Hard",
            currentDisplayLabel = "Steady Practice",
            emergencyContactName = "Sunita Sharma (Daughter)",
            emergencyContactPhone = "+91 98765 43210",
            safeZoneAddress = "Guwahati Green Enclave, Sector 4"
        ),
        Patient(
            id = "demo-patient-12",
            name = "Kamala Devi",
            age = 78,
            conditionTag = "Memory Care • Alert Triggered",
            statusSummary = "Recent score drop detected • Support recommended",
            currentTier = "Moderate",
            currentDisplayLabel = "Comfortable Pace",
            emergencyContactName = "Rohan Devi (Son)",
            emergencyContactPhone = "+91 98123 45678",
            safeZoneAddress = "Silpukhuri Heritage Road, House #12"
        )
    )

    // Current State Holders
    private val _currentRole = MutableStateFlow("patient") // "patient" or "caregiver"
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _selectedPatientId = MutableStateFlow("demo-patient-01")
    val selectedPatientId: StateFlow<String> = _selectedPatientId.asStateFlow()

    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _bluetoothConnected = MutableStateFlow(true)
    val bluetoothConnected: StateFlow<Boolean> = _bluetoothConnected.asStateFlow()

    // Sessions map: patientId -> List<GameSessionRecord>
    private val _patientSessions = MutableStateFlow<Map<String, List<GameSessionRecord>>>(
        mapOf(
            "demo-patient-01" to listOf(
                GameSessionRecord("sess-1", "Memory Matching", 78.5, 80.0, "Steady Practice", "2026-09-06T09:30:00", "06 Sep, 09:30 AM"),
                GameSessionRecord("sess-2", "Pattern Recognition", 82.0, 85.0, "Steady Practice", "2026-09-06T17:15:00", "06 Sep, 05:15 PM"),
                GameSessionRecord("sess-3", "Object Recognition", 80.0, 82.0, "Steady Practice", "2026-09-07T10:00:00", "07 Sep, 10:00 AM"),
                GameSessionRecord("sess-4", "Memory Matching", 85.2, 90.0, "Gentle Challenge", "2026-09-07T16:45:00", "07 Sep, 04:45 PM"),
                GameSessionRecord("sess-5", "Memory Matching", 88.0, 92.0, "Gentle Challenge", "2026-09-08T09:15:00", "08 Sep, 09:15 AM")
            ),
            // Kamala Devi: Noticeable drop from ~82 down to ~52 across recent sessions
            "demo-patient-12" to listOf(
                GameSessionRecord("sess-k1", "Memory Matching", 82.0, 85.0, "Steady Practice", "2026-09-04T10:00:00", "04 Sep, 10:00 AM"),
                GameSessionRecord("sess-k2", "Pattern Recognition", 80.5, 82.0, "Steady Practice", "2026-09-04T17:00:00", "04 Sep, 05:00 PM"),
                GameSessionRecord("sess-k3", "Object Recognition", 83.0, 88.0, "Steady Practice", "2026-09-05T10:30:00", "05 Sep, 10:30 AM"),
                GameSessionRecord("sess-k4", "Memory Matching", 58.0, 60.0, "Comfortable Pace", "2026-09-06T11:00:00", "06 Sep, 11:00 AM"),
                GameSessionRecord("sess-k5", "Pattern Recognition", 52.0, 50.0, "Comfortable Pace", "2026-09-07T10:15:00", "07 Sep, 10:15 AM"),
                GameSessionRecord("sess-k6", "Memory Matching", 49.5, 48.0, "Comfortable Pace", "2026-09-08T09:45:00", "08 Sep, 09:45 AM")
            )
        )
    )
    val patientSessions: StateFlow<Map<String, List<GameSessionRecord>>> = _patientSessions.asStateFlow()

    // Reminders
    private val _reminders = MutableStateFlow<List<Reminder>>(
        listOf(
            Reminder("rem-1", "Morning Memory Support Tablet", "Take 1 tablet after breakfast with warm water", "08:30 AM", "medication", true),
            Reminder("rem-2", "Hydration Reminder", "Drink 250ml of warm water or mild herbal tea", "01:30 PM", "hydration", true),
            Reminder("rem-3", "Daily Brain Activity Session", "Play Memory Matching for 10 relaxing minutes", "05:00 PM", "game", false),
            Reminder("rem-4", "Evening Garden Walk", "Gentle 15-minute stroll inside safe home boundary", "06:30 PM", "walk", false)
        )
    )
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    // Geofence Sentinel State
    private val _geofenceState = MutableStateFlow(GeofenceState())
    val geofenceState: StateFlow<GeofenceState> = _geofenceState.asStateFlow()

    // Actions
    fun setRole(role: String) {
        _currentRole.value = role
    }

    fun selectPatient(patientId: String) {
        _selectedPatientId.value = patientId
    }

    fun setLanguage(langCode: String) {
        _currentLanguage.value = langCode
    }

    fun toggleBluetooth() {
        _bluetoothConnected.value = !_bluetoothConnected.value
    }

    fun toggleReminder(reminderId: String) {
        _reminders.value = _reminders.value.map {
            if (it.id == reminderId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun addReminder(title: String, subtitle: String, time: String, type: String) {
        val newRem = Reminder(
            id = "rem-${System.currentTimeMillis()}",
            title = title,
            subtitle = subtitle,
            time = time,
            type = type,
            isCompleted = false
        )
        _reminders.value = _reminders.value + newRem
    }

    fun recordGameSession(
        gameType: String,
        metrics: SessionMetrics
    ): CpsResult {
        val currentPatient = _selectedPatientId.value
        val history = _patientSessions.value[currentPatient] ?: emptyList()
        val recentAccuracies = history.takeLast(5).map { it.accuracy }

        val cpsResult = CpsCalculator.calculateCps(metrics, recentAccuracies)

        val newRecord = GameSessionRecord(
            sessionId = "sess-${System.currentTimeMillis()}",
            gameType = gameType,
            cps = cpsResult.cps,
            accuracy = metrics.accuracy,
            displayLabel = cpsResult.displayLabel,
            timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()),
            formattedDate = dateFormat.format(Date())
        )

        val currentMap = _patientSessions.value.toMutableMap()
        val list = (currentMap[currentPatient] ?: emptyList()) + newRecord
        currentMap[currentPatient] = list
        _patientSessions.value = currentMap

        return cpsResult
    }

    fun getActivePatient(): Patient {
        val id = _selectedPatientId.value
        return PATIENTS.firstOrNull { it.id == id } ?: PATIENTS.first()
    }

    fun getLatestCps(patientId: String = _selectedPatientId.value): Double {
        val list = _patientSessions.value[patientId] ?: emptyList()
        return list.lastOrNull()?.cps ?: 80.0
    }

    fun checkPatientAnomaly(patientId: String = _selectedPatientId.value): AnomalyDetector.AnomalyResult? {
        val list = _patientSessions.value[patientId] ?: emptyList()
        val scores = list.map { it.cps }
        return AnomalyDetector.checkTrend(scores)
    }

    fun updateGeofenceRadius(newRadius: Double) {
        val current = _geofenceState.value
        val inZone = current.distanceMeters <= newRadius
        _geofenceState.value = current.copy(
            radiusMeters = newRadius,
            isInSafeZone = inZone,
            breachAlertActive = !inZone
        )
    }

    fun simulateBreach(trigger: Boolean) {
        val current = _geofenceState.value
        if (trigger) {
            _geofenceState.value = current.copy(
                isInSafeZone = false,
                distanceMeters = current.radiusMeters + 45.0,
                breachAlertActive = true,
                lastPingTime = "Alert: 1 min ago"
            )
        } else {
            _geofenceState.value = current.copy(
                isInSafeZone = true,
                distanceMeters = 35.0,
                breachAlertActive = false,
                lastPingTime = "Just now"
            )
        }
    }
}
