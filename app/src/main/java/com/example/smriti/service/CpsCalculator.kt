package com.example.smriti.service

import com.example.smriti.model.CpsResult
import com.example.smriti.model.SessionMetrics
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Cognitive Performance & Supportive Engagement Calculator.
 * Faithfully ports Python backend formula and weighting specifications:
 * - Accuracy: 30%
 * - Response Speed: 20%
 * - Completion Rate: 20%
 * - Consistency: 15%
 * - Memory Performance: 15%
 */
object CpsCalculator {

    private const val WEIGHT_ACCURACY = 0.30
    private const val WEIGHT_RESPONSE_SPEED = 0.20
    private const val WEIGHT_COMPLETION = 0.20
    private const val WEIGHT_CONSISTENCY = 0.15
    private const val WEIGHT_MEMORY = 0.15

    private val DIFFICULTY_TIERS = listOf(
        TierConfig(threshold = 85.0, tier = "Hard", displayLabel = "Gentle Challenge"),
        TierConfig(threshold = 65.0, tier = "Medium-Hard", displayLabel = "Steady Practice"),
        TierConfig(threshold = 40.0, tier = "Moderate", displayLabel = "Comfortable Pace"),
        TierConfig(threshold = 0.0, tier = "Easy", displayLabel = "Relaxed Practice")
    )

    private data class TierConfig(val threshold: Double, val tier: String, val displayLabel: String)

    /**
     * Converts response timing (ms) into a 0-100 speed indicator.
     * Normalized between 1500ms (best) and 8000ms (worst), clamped.
     */
    fun normalizeResponseSpeed(
        responseTimeMs: Double,
        bestMs: Double = 1500.0,
        worstMs: Double = 8000.0
    ): Double {
        val ms = if (responseTimeMs < 60.0) responseTimeMs * 1000.0 else responseTimeMs
        if (ms <= bestMs) return 100.0
        if (ms >= worstMs) return 0.0

        val span = worstMs - bestMs
        val raw = 100.0 * (worstMs - ms) / span
        return (round(raw * 100.0) / 100.0).coerceIn(0.0, 100.0)
    }

    /**
     * Measures stability across recent session accuracies using population standard deviation.
     */
    fun consistencyScore(recentAccuracies: List<Double>): Double {
        if (recentAccuracies.size < 2) return 100.0
        val avg = recentAccuracies.average()
        val variance = recentAccuracies.map { (it - avg).pow(2) }.average()
        val spread = sqrt(variance)
        val score = max(0.0, 100.0 - (spread / 50.0) * 100.0)
        return round(score * 100.0) / 100.0
    }

    /**
     * Computes the final CPS score (0-100) and maps to supportive tier and display label.
     */
    fun calculateCps(session: SessionMetrics, recentAccuracies: List<Double> = emptyList()): CpsResult {
        val accuracyScore = session.accuracy.coerceIn(0.0, 100.0)
        val speedScore = normalizeResponseSpeed(session.responseTimeMs)
        val completionScore = session.completionRate.coerceIn(0.0, 100.0)
        val consistency = consistencyScore(recentAccuracies)

        val memoryScore = if (session.isMemoryGame && session.memorySpecificAccuracy != null) {
            session.memorySpecificAccuracy.coerceIn(0.0, 100.0)
        } else {
            accuracyScore
        }

        val rawCps = (accuracyScore * WEIGHT_ACCURACY) +
                (speedScore * WEIGHT_RESPONSE_SPEED) +
                (completionScore * WEIGHT_COMPLETION) +
                (consistency * WEIGHT_CONSISTENCY) +
                (memoryScore * WEIGHT_MEMORY)

        val roundedCps = round(rawCps * 10.0) / 10.0

        val tierMatch = DIFFICULTY_TIERS.firstOrNull { roundedCps >= it.threshold }
            ?: DIFFICULTY_TIERS.last()

        return CpsResult(
            cps = roundedCps,
            difficultyTier = tierMatch.tier,
            displayLabel = tierMatch.displayLabel,
            accuracyScore = round(accuracyScore * 10.0) / 10.0,
            speedScore = round(speedScore * 10.0) / 10.0,
            completionScore = round(completionScore * 10.0) / 10.0,
            consistencyScore = round(consistency * 10.0) / 10.0,
            memoryScore = round(memoryScore * 10.0) / 10.0
        )
    }
}
