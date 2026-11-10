package com.example.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "DeepanitaTTS"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechAmplitude = MutableStateFlow(0f)
    val speechAmplitude: StateFlow<Float> = _speechAmplitude.asStateFlow()

    private var amplitudeJob: Job? = null
    private var lastSpokenText: String = ""

    private var currentPitch = 1.15f // Sweet feminine pitch
    private var currentRate = 0.95f  // Soft, clear, gentle speaking rate

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupVoice()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    startSimulatingAmplitude()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopSimulatingAmplitude()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopSimulatingAmplitude()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    stopSimulatingAmplitude()
                    Log.e(tag, "TTS Error: $errorCode")
                }
            })
        } else {
            Log.e(tag, "TextToSpeech init failed with status: $status")
        }
    }

    private fun setupVoice() {
        val t = tts ?: return
        // Try Bengali locale first
        val bnLocale = Locale("bn", "BD")
        val result = t.setLanguage(bnLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            t.language = Locale.ENGLISH
        }
        t.setPitch(currentPitch)
        t.setSpeechRate(currentRate)

        // Select female voice if available
        try {
            val voices = t.voices
            if (!voices.isNullOrEmpty()) {
                val femaleVoice = voices.find { voice ->
                    val name = voice.name.lowercase()
                    (name.contains("female") || name.contains("fem") || name.contains("#female") || name.contains("bn-bd-x-bnd"))
                            && !voice.isNetworkConnectionRequired
                } ?: voices.find { it.name.lowercase().contains("female") }
                if (femaleVoice != null) {
                    t.voice = femaleVoice
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Could not set custom voice: ${e.message}")
        }
    }

    fun speak(text: String, languageCode: String? = null, onComplete: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            Log.w(tag, "TTS not ready yet")
            return
        }

        lastSpokenText = text
        stop()

        val t = tts ?: return

        // Set language based on text content or parameter
        if (languageCode != null) {
            val loc = when (languageCode) {
                "en" -> Locale.US
                "hi" -> Locale("hi", "IN")
                "ar" -> Locale("ar")
                "ur" -> Locale("ur", "PK")
                else -> Locale("bn", "BD")
            }
            t.language = loc
        } else {
            // Detect if contains Bengali characters (0x0980 - 0x09FF)
            val hasBengali = text.any { it.code in 0x0980..0x09FF }
            t.language = if (hasBengali) Locale("bn", "BD") else Locale.US
        }

        t.setPitch(currentPitch)
        t.setSpeechRate(currentRate)

        val utteranceId = "deepanita_${System.currentTimeMillis()}"
        t.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun repeatLast() {
        if (lastSpokenText.isNotBlank()) {
            speak(lastSpokenText)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        stopSimulatingAmplitude()
    }

    fun updateSettings(pitch: Float, rate: Float) {
        currentPitch = pitch
        currentRate = rate
        tts?.setPitch(pitch)
        tts?.setSpeechRate(rate)
    }

    private fun startSimulatingAmplitude() {
        amplitudeJob?.cancel()
        amplitudeJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _isSpeaking.value) {
                // Natural speaking waveform oscillation
                val base = (0.35f + (Math.sin(System.currentTimeMillis() / 80.0).toFloat() * 0.3f) + (Math.random().toFloat() * 0.35f)).coerceIn(0.1f, 1.0f)
                _speechAmplitude.value = base
                delay(60)
            }
            _speechAmplitude.value = 0f
        }
    }

    private fun stopSimulatingAmplitude() {
        amplitudeJob?.cancel()
        amplitudeJob = null
        _speechAmplitude.value = 0f
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
