package com.example.smriti.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechToTextManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _transcribedText = MutableStateFlow("")
    val transcribedText: StateFlow<String> = _transcribedText.asStateFlow()

    private val _speechError = MutableStateFlow<String?>(null)
    val speechError: StateFlow<String?> = _speechError.asStateFlow()

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        langCode: String = "en",
        onResult: (String) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _speechError.value = "Speech recognition is not available on this device."
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _speechError.value = null
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection issue."
                            else -> "Voice recognition paused."
                        }
                        _speechError.value = errorMsg
                        Log.w("SpeechToTextManager", "Speech recognition error: $error ($errorMsg)")
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _transcribedText.value = text
                            onResult(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _transcribedText.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val locale = when (langCode.lowercase()) {
                "hi" -> "hi-IN"
                "as" -> "as-IN"
                "mzo" -> "en-IN"
                "kha" -> "en-IN"
                else -> "en-US"
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, locale)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now to navigate or trigger reminders")
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e("SpeechToTextManager", "Failed to start speech recognizer", e)
            _isListening.value = false
            _speechError.value = e.localizedMessage
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.w("SpeechToTextManager", "Error stopping speech recognizer", e)
        } finally {
            _isListening.value = false
        }
    }
}
