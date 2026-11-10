package com.example.data.model

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "bn",
    val actionType: String? = null,
    val actionDetail: String? = null
)
