package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timeMillis: Long,
    val isCompleted: Boolean,
    val type: String, // ALARM, REMINDER, TIMER
    val note: String
)
