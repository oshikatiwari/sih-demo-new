package com.example.smriti.service

import kotlin.math.round

/**
 * Anomaly Detector & Caregiver Support Notice Engine.
 * Monitors trends in historical cognitive engagement scores.
 * Uses empathetic, reassuring phrasing designed for family caregivers.
 */
object AnomalyDetector {

    const val DROP_THRESHOLD_PCT = 15.0
    const val CAREGIVER_ALERT_MESSAGE =
        "Noticeable change in recent activity patterns — a gentle check-in or caregiver review is recommended."

    data class AnomalyResult(
        val isAlertTriggered: Boolean,
        val message: String,
        val priorAvg: Double,
        val recentAvg: Double,
        val pctChange: Double
    )

    fun checkTrend(scoreHistory: List<Double>, window: Int = 3): AnomalyResult? {
        if (scoreHistory.size < window * 2) return null

        val prior = scoreHistory.subList(scoreHistory.size - (window * 2), scoreHistory.size - window)
        val recent = scoreHistory.subList(scoreHistory.size - window, scoreHistory.size)

        val priorAvg = prior.average()
        val recentAvg = recent.average()

        if (priorAvg == 0.0) return null

        val pctChange = ((recentAvg - priorAvg) / priorAvg) * 100.0

        if (pctChange <= -DROP_THRESHOLD_PCT) {
            return AnomalyResult(
                isAlertTriggered = true,
                message = CAREGIVER_ALERT_MESSAGE,
                priorAvg = round(priorAvg * 10.0) / 10.0,
                recentAvg = round(recentAvg * 10.0) / 10.0,
                pctChange = round(pctChange * 10.0) / 10.0
            )
        }

        return null
    }
}
