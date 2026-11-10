package com.example.data.model

data class VoiceSettings(
    val speechRate: Float = 0.95f,
    val pitch: Float = 1.15f, // Sweet gentle female voice pitch
    val language: String = "bn-BD", // Default Bengali
    val isOnlineMode: Boolean = true,
    val isWakeWordEnabled: Boolean = true,
    val continuousVoiceMode: Boolean = false,
    val femaleVoicePreferred: Boolean = true,
    val userName: String = "সিদ্ধার্থ", // Siddhartha
    val isCreatorMode: Boolean = true
)
