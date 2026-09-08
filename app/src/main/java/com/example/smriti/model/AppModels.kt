package com.example.smriti.model

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val type: String, // "medication", "hydration", "game", "walk"
    val isCompleted: Boolean = false
)

@Serializable
data class LanguageItem(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val description: String
)

@Serializable
data class GeofenceState(
    val isInSafeZone: Boolean = true,
    val distanceMeters: Double = 35.0,
    val radiusMeters: Double = 150.0,
    val safeCenterLat: Double = 26.1445,
    val safeCenterLng: Double = 91.7362,
    val currentLat: Double = 26.1448,
    val currentLng: Double = 91.7365,
    val lastPingTime: String = "Just now",
    val batteryPct: Int = 88,
    val isBeaconActive: Boolean = true,
    val breachAlertActive: Boolean = false,
    val authorizedGuardians: List<String> = listOf("caregiver@smriti.care", "dr.verma@cognicare.in")
)
