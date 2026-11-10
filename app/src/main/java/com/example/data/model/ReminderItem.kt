package com.example.data.model

enum class ReminderType {
    ALARM,
    REMINDER,
    TIMER
}

data class ReminderItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val timeMillis: Long,
    val isCompleted: Boolean = false,
    val type: ReminderType = ReminderType.REMINDER,
    val note: String = ""
)
