package com.example.presentation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.ChatHistoryEntity
import com.example.data.local.ReminderEntity
import com.example.data.model.ChatMessage
import com.example.data.model.ReminderItem
import com.example.data.model.ReminderType
import com.example.data.model.VoiceSettings
import com.example.domain.AssistantEngine
import com.example.domain.CommandResult
import com.example.voice.SpeechRecognitionManager
import com.example.voice.TtsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AssistantStatus {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

enum class ScreenDestination {
    AVATAR_VOICE,
    DASHBOARD,
    CHAT,
    MEDICAL,
    NAVIGATION,
    CAMERA,
    STORIES_MUSIC,
    REMINDERS_ALARMS,
    APP_LAUNCHER,
    SETTINGS
}

data class CallConfirmationState(
    val name: String,
    val number: String,
    val prompt: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "MainViewModel"
    private val database = AppDatabase.getInstance(application)
    private val geminiAiService = GeminiAiService()
    private val assistantEngine = AssistantEngine(application, geminiAiService, database)

    val ttsManager = TtsManager(application)
    private var speechManager: SpeechRecognitionManager? = null

    private val _status = MutableStateFlow(AssistantStatus.IDLE)
    val status: StateFlow<AssistantStatus> = _status.asStateFlow()

    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    private val _lastSpokenAnswer = MutableStateFlow(
        "হ্যালো! আমি দীপানিতা। সিদ্ধার্থ মোজুমদারের ব্যক্তিগত AI Voice Assistant। আজ তোমাকে কীভাবে সাহায্য করতে পারি?"
    )
    val lastSpokenAnswer: StateFlow<String> = _lastSpokenAnswer.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _voiceSettings = MutableStateFlow(VoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenDestination.AVATAR_VOICE)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _callConfirmation = MutableStateFlow<CallConfirmationState?>(null)
    val callConfirmation: StateFlow<CallConfirmationState?> = _callConfirmation.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = database.chatHistoryDao().getAllMessages()
        .map { list ->
            list.map { entity ->
                ChatMessage(
                    id = entity.id,
                    text = entity.text,
                    isUser = entity.isUser,
                    timestamp = entity.timestamp,
                    language = entity.language,
                    actionType = entity.actionType,
                    actionDetail = entity.actionDetail
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val reminders: StateFlow<List<ReminderItem>> = database.reminderDao().getAllReminders()
        .map { list ->
            list.map { entity ->
                ReminderItem(
                    id = entity.id,
                    title = entity.title,
                    timeMillis = entity.timeMillis,
                    isCompleted = entity.isCompleted,
                    type = when (entity.type) {
                        "ALARM" -> ReminderType.ALARM
                        "TIMER" -> ReminderType.TIMER
                        else -> ReminderType.REMINDER
                    },
                    note = entity.note
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        _isOnline.value = assistantEngine.isOnline()
        initSpeechRecognition()
        observeTtsSpeaking()
    }

    private fun initSpeechRecognition() {
        speechManager = SpeechRecognitionManager(
            context = getApplication(),
            onResult = { recognized ->
                _currentQuery.value = recognized
                processVoiceInput(recognized)
            },
            onError = { errMsg ->
                _status.value = AssistantStatus.IDLE
                _snackMessage.value = errMsg
            }
        )
    }

    private fun observeTtsSpeaking() {
        viewModelScope.launch {
            ttsManager.isSpeaking.collect { speaking ->
                if (speaking) {
                    _status.value = AssistantStatus.SPEAKING
                } else if (_status.value == AssistantStatus.SPEAKING) {
                    _status.value = AssistantStatus.IDLE
                }
            }
        }
    }

    fun startListening() {
        ttsManager.stop()
        _status.value = AssistantStatus.LISTENING
        val lang = _voiceSettings.value.language
        speechManager?.startListening(lang)
    }

    fun stopListening() {
        speechManager?.stopListening()
        if (_status.value == AssistantStatus.LISTENING) {
            _status.value = AssistantStatus.IDLE
        }
    }

    fun toggleVoiceListening() {
        if (_status.value == AssistantStatus.LISTENING) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun submitQuery(query: String) {
        if (query.isBlank()) return
        _currentQuery.value = query
        processVoiceInput(query)
    }

    private fun processVoiceInput(query: String) {
        viewModelScope.launch {
            _status.value = AssistantStatus.PROCESSING

            // Save user message to database
            saveChatMessage(query, isUser = true)

            val userName = _voiceSettings.value.userName
            val history = chatMessages.value

            when (val result = assistantEngine.processUserCommand(query, userName, history)) {
                is CommandResult.SpokenAnswer -> {
                    _lastSpokenAnswer.value = result.text
                    saveChatMessage(result.text, isUser = false, actionType = result.actionType, actionDetail = result.actionDetail)
                    speak(result.text)
                }
                is CommandResult.LaunchIntent -> {
                    _lastSpokenAnswer.value = result.text
                    saveChatMessage(result.text, isUser = false, actionType = "LAUNCH_INTENT")
                    speak(result.text)
                    try {
                        getApplication<Application>().startActivity(result.intent)
                    } catch (e: Exception) {
                        _snackMessage.value = "অ্যাপ্লিকেশন চালু করতে সমস্যা হয়েছে: ${e.message}"
                    }
                }
                is CommandResult.CallConfirmation -> {
                    _callConfirmation.value = CallConfirmationState(result.name, result.number, result.prompt)
                    _lastSpokenAnswer.value = result.prompt
                    saveChatMessage(result.prompt, isUser = false, actionType = "CALL_PROMPT")
                    speak(result.prompt)
                }
            }
        }
    }

    fun confirmCall() {
        val conf = _callConfirmation.value ?: return
        _callConfirmation.value = null
        try {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${conf.number}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(callIntent)
            speak("${conf.name} কে ফোন করছি...")
        } catch (e: SecurityException) {
            _snackMessage.value = "ফোন কল করার পারমিশন নেই।"
            speak("ফোন করার পারমিশন প্রয়োজন।")
        } catch (e: Exception) {
            _snackMessage.value = "কল ব্যর্থ হয়েছে: ${e.message}"
        }
    }

    fun dismissCall() {
        _callConfirmation.value = null
        speak("কল বাতিল করা হয়েছে।")
    }

    fun speak(text: String) {
        ttsManager.speak(text)
    }

    fun repeatLastAnswer() {
        ttsManager.repeatLast()
    }

    fun stopSpeaking() {
        ttsManager.stop()
        _status.value = AssistantStatus.IDLE
    }

    private suspend fun saveChatMessage(text: String, isUser: Boolean, actionType: String? = null, actionDetail: String? = null) {
        withContext(Dispatchers.IO) {
            database.chatHistoryDao().insertMessage(
                ChatHistoryEntity(
                    text = text,
                    isUser = isUser,
                    timestamp = System.currentTimeMillis(),
                    language = _voiceSettings.value.language,
                    actionType = actionType,
                    actionDetail = actionDetail
                )
            )
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            database.chatHistoryDao().clearHistory()
        }
    }

    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun toggleOnlineMode() {
        _isOnline.value = !_isOnline.value
        _voiceSettings.value = _voiceSettings.value.copy(isOnlineMode = _isOnline.value)
        val modeText = if (_isOnline.value) "অনলাইন মোড চালু করা হয়েছে।" else "অফলাইন মোড সক্রিয় রয়েছে।"
        speak(modeText)
    }

    fun updateVoiceSettings(rate: Float, pitch: Float, language: String, userName: String) {
        _voiceSettings.value = _voiceSettings.value.copy(
            speechRate = rate,
            pitch = pitch,
            language = language,
            userName = userName
        )
        ttsManager.updateSettings(pitch, rate)
    }

    fun addReminder(title: String, type: ReminderType, timeMillis: Long, note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            database.reminderDao().insertReminder(
                ReminderEntity(
                    title = title,
                    timeMillis = timeMillis,
                    isCompleted = false,
                    type = type.name,
                    note = note
                )
            )
        }
    }

    fun toggleReminderCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            database.reminderDao().setCompleted(id, completed)
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            database.reminderDao().deleteById(id)
        }
    }

    fun clearSnack() {
        _snackMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        speechManager?.destroy()
        ttsManager.shutdown()
    }
}
