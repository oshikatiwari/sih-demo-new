package com.example.smriti.service

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private var toneGen: ToneGenerator? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            Log.w("TtsManager", "Could not init ToneGenerator", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.ENGLISH
            tts?.setSpeechRate(0.88f) // Slightly slower rate for elderly clarity
            tts?.setPitch(1.05f)
        } else {
            Log.e("TtsManager", "TTS init failed with status: $status")
        }
    }

    fun speak(text: String, langCode: String = "en", onComplete: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            onComplete?.invoke()
            return
        }

        try {
            val locale = when (langCode.lowercase()) {
                "hi" -> Locale("hi", "IN")
                "as" -> Locale("as", "IN")
                "mzo" -> Locale("en", "IN") // Fallback to Indian English accent for Mizo/Khasi if locale not present
                "kha" -> Locale("en", "IN")
                else -> Locale.ENGLISH
            }

            val supported = tts?.isLanguageAvailable(locale)
            if (supported != TextToSpeech.LANG_MISSING_DATA && supported != TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = locale
            } else {
                tts?.language = Locale.ENGLISH
            }

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "smriti_utterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TtsManager", "Error speaking text", e)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun playChime() {
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (_: Exception) {}
    }

    fun playAlertTone() {
        try {
            toneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 300)
        } catch (_: Exception) {}
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        toneGen?.release()
        toneGen = null
    }
}
