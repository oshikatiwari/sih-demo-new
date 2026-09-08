package com.example.smriti.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: String,
    val name: String,
    val age: Int,
    val conditionTag: String,
    val statusSummary: String,
    val currentTier: String,
    val currentDisplayLabel: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val safeZoneAddress: String
)
