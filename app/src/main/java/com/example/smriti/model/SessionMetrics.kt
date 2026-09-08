package com.example.smriti.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionMetrics(
    val accuracy: Double,              // 0-100, % correct answers
    val responseTimeMs: Double,        // timing per action in milliseconds
    val completionRate: Double,        // 0-100, % of session completed
    val attempts: Int,
    val errors: Int,
    val hintsUsed: Int = 0,
    val isMemoryGame: Boolean = false,
    val memorySpecificAccuracy: Double? = null
)

@Serializable
data class CpsResult(
    val cps: Double,
    val difficultyTier: String,
    val displayLabel: String,
    val accuracyScore: Double,
    val speedScore: Double,
    val completionScore: Double,
    val consistencyScore: Double,
    val memoryScore: Double
)

@Serializable
data class GameSessionRecord(
    val sessionId: String,
    val gameType: String,
    val cps: Double,
    val accuracy: Double,
    val displayLabel: String,
    val timestamp: String,
    val formattedDate: String
)
