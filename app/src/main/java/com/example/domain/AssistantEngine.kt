package com.example.domain

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.example.ai.GeminiAiService
import com.example.ai.KnowledgeBase
import com.example.data.local.AppDatabase
import com.example.data.local.ChatHistoryEntity
import com.example.data.local.ReminderEntity
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class CommandResult {
    data class SpokenAnswer(val text: String, val actionType: String? = null, val actionDetail: String? = null) : CommandResult()
    data class LaunchIntent(val text: String, val intent: Intent) : CommandResult()
    data class CallConfirmation(val name: String, val number: String, val prompt: String) : CommandResult()
}

class AssistantEngine(
    private val context: Context,
    private val geminiAiService: GeminiAiService,
    private val database: AppDatabase
) {
    private val tag = "AssistantEngine"

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun processUserCommand(
        query: String,
        userName: String = "সিদ্ধার্থ",
        conversationHistory: List<ChatMessage> = emptyList()
    ): CommandResult = withContext(Dispatchers.Default) {
        val q = query.trim()
        val lower = q.lowercase()

        // 1. App Launcher commands
        val appLaunch = checkAppLaunch(lower, userName)
        if (appLaunch != null) {
            return@withContext appLaunch
        }

        // 2. Battery status command
        if (lower.contains("ব্যাটারি") || lower.contains("battery") || lower.contains("কত পারসেন্ট চার্জ") || lower.contains("চার্জ আছে")) {
            val batteryText = getBatteryStatus(userName)
            return@withContext CommandResult.SpokenAnswer(batteryText, actionType = "BATTERY")
        }

        // 3. Navigation / GPS commands
        if (lower.contains("ম্যাপ") || lower.contains("maps") || lower.contains("নেভিগেশন") || lower.contains("যেতে চাই") || lower.contains("রাস্তা দেখাও") || lower.contains("কাছের")) {
            val navResult = handleNavigationQuery(lower, userName)
            if (navResult != null) return@withContext navResult
        }

        // 4. Alarm / Timer commands
        if (lower.contains("অ্যালার্ম") || lower.contains("alarm") || lower.contains("টাইমার") || lower.contains("timer") || lower.contains("মনে করিয়ে")) {
            val alarmResult = handleAlarmQuery(lower, userName)
            if (alarmResult != null) return@withContext alarmResult
        }

        // 5. Phone Call commands
        if (lower.contains("ফোন করো") || lower.contains("কল করো") || lower.contains("call ") || lower.contains("dial ")) {
            val callResult = handleCallQuery(q, userName)
            if (callResult != null) return@withContext callResult
        }

        // 6. Camera command
        if (lower.contains("ক্যামেরা খোলো") || lower.contains("ছবি তোলো") || lower.contains("open camera")) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return@withContext CommandResult.LaunchIntent("$userName, ক্যামেরা চালু করছি।", cameraIntent)
        }

        // 7. General AI conversation & Knowledge
        val historyPairs = conversationHistory.map { it.text to it.isUser }
        val aiResponse = geminiAiService.generateResponse(q, userName, historyPairs)
        return@withContext CommandResult.SpokenAnswer(aiResponse, actionType = "AI_REPLY")
    }

    private fun checkAppLaunch(query: String, userName: String): CommandResult? {
        val apps = mapOf(
            "whatsapp" to listOf("com.whatsapp", "WhatsApp"),
            "হোয়াটসঅ্যাপ" to listOf("com.whatsapp", "WhatsApp"),
            "facebook" to listOf("com.facebook.katana", "Facebook"),
            "ফেসবুক" to listOf("com.facebook.katana", "Facebook"),
            "youtube" to listOf("com.google.android.youtube", "YouTube"),
            "ইউটিউব" to listOf("com.google.android.youtube", "YouTube"),
            "messenger" to listOf("com.facebook.orca", "Messenger"),
            "মেসেঞ্জার" to listOf("com.facebook.orca", "Messenger"),
            "settings" to listOf("com.android.settings", "Settings"),
            "সেটিংস" to listOf("com.android.settings", "সেটিংস"),
            "calculator" to listOf("com.google.android.calculator", "ক্যালকুলেটর"),
            "ক্যালকুলেটর" to listOf("com.google.android.calculator", "ক্যালকুলেটর")
        )

        for ((trigger, appInfo) in apps) {
            if (query.contains(trigger) && (query.contains("খোলো") || query.contains("চালু করো") || query.contains("open") || query.contains("launch"))) {
                val pkg = appInfo[0]
                val appName = appInfo[1]
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return if (intent != null) {
                    CommandResult.LaunchIntent("$userName, $appName চালু করছি।", intent)
                } else {
                    CommandResult.SpokenAnswer("$userName, আপনার ফোনে $appName ইনস্টল করা নেই।", actionType = "APP_NOT_FOUND")
                }
            }
        }
        return null
    }

    private fun getBatteryStatus(userName: String): String {
        return try {
            val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, intentFilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else 50
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val chargingText = if (isCharging) "এবং ফোনটি চার্জ হচ্ছে" else "এবং এখন চার্জ হচ্ছে না"

            if (userName.contains("সিদ্ধার্থ") || userName.equals("Siddhartha", ignoreCase = true)) {
                "সিদ্ধার্থ, তোমার ফোনে $pct% চার্জ আছে $chargingText।"
            } else {
                "আপনার ফোনে বর্তমানে $pct% চার্জ রয়েছে $chargingText।"
            }
        } catch (e: Exception) {
            "ব্যাটারির তথ্য সংগ্রহ করা যায়নি।"
        }
    }

    private fun handleNavigationQuery(query: String, userName: String): CommandResult? {
        var destination = ""
        when {
            query.contains("হাসপাতাল") || query.contains("hospital") -> destination = "nearby hospital"
            query.contains("ফার্মেসি") || query.contains("pharmacy") || query.contains("ওষুধের দোকান") -> destination = "nearby pharmacy"
            query.contains("এটিএম") || query.contains("atm") -> destination = "nearby ATM"
            query.contains("পেট্রোল") || query.contains("petrol pump") -> destination = "nearby petrol pump"
            query.contains("রেস্তোরাঁ") || query.contains("restaurant") -> destination = "nearby restaurant"
            else -> {
                val cleaned = query.replace("ম্যাপে", "").replace("maps", "").replace("খোলো", "").replace("যেতে চাই", "").replace("রাস্তা দেখাও", "").trim()
                if (cleaned.isNotBlank() && cleaned.length > 2) {
                    destination = cleaned
                }
            }
        }

        if (destination.isNotBlank()) {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(destination)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return CommandResult.LaunchIntent("$userName, $destination এর জন্য গুগল ম্যাপ চালু করছি।", mapIntent)
        }
        return null
    }

    private suspend fun handleAlarmQuery(query: String, userName: String): CommandResult? {
        val now = System.currentTimeMillis()
        if (query.contains("টাইমার") || query.contains("timer")) {
            val digits = Regex("""\d+""").find(query)?.value?.toIntOrNull() ?: 5
            val seconds = digits * 60
            val timerIntent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, "Deepanita Timer")
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // Also save locally in database
            database.reminderDao().insertReminder(
                ReminderEntity(
                    title = "$digits মিনিটের টাইমার",
                    timeMillis = now + (seconds * 1000L),
                    isCompleted = false,
                    type = "TIMER",
                    note = "দীপানিতার টাইমার"
                )
            )
            return CommandResult.LaunchIntent("$userName, $digits মিনিটের টাইমার সেট করে দিয়েছি।", timerIntent)
        }

        if (query.contains("অ্যালার্ম") || query.contains("alarm")) {
            val hourMatch = Regex("""(\d+)(?:\s*টায়|\s*টা)""").find(query)?.groupValues?.get(1)?.toIntOrNull() ?: 6
            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hourMatch)
                putExtra(AlarmClock.EXTRA_MINUTES, 0)
                putExtra(AlarmClock.EXTRA_MESSAGE, "Deepanita Alarm")
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            database.reminderDao().insertReminder(
                ReminderEntity(
                    title = "সকাল $hourMatch টায় অ্যালার্ম",
                    timeMillis = now + (3600 * 1000L * 5),
                    isCompleted = false,
                    type = "ALARM",
                    note = "দীপানিতার অ্যালার্ম"
                )
            )
            return CommandResult.LaunchIntent("$userName, সকাল $hourMatch টায় অ্যালার্ম নির্ধারণ করেছি।", alarmIntent)
        }

        if (query.contains("মনে করিয়ে") || query.contains("reminder")) {
            val title = query.replace("মনে করিয়ে দিও", "").replace("মনে করিয়ে দাও", "").trim()
            database.reminderDao().insertReminder(
                ReminderEntity(
                    title = if (title.isNotBlank()) title else "জরুরি কাজ",
                    timeMillis = now + (3600 * 1000L * 2),
                    isCompleted = false,
                    type = "REMINDER",
                    note = "দীপানিতার রিমাইন্ডার"
                )
            )
            return CommandResult.SpokenAnswer("$userName, আমি আপনার রিমাইন্ডারটি সংরক্ষণ করেছি। সময়মতো আপনাকে মনে করিয়ে দেব।", actionType = "REMINDER_SAVED")
        }

        return null
    }

    private fun handleCallQuery(query: String, userName: String): CommandResult? {
        val targetName = query.replace("ফোন করো", "").replace("কল করো", "").replace("call", "").trim()
        if (targetName.isBlank()) {
            return CommandResult.SpokenAnswer("কাকে কল করতে চান, তার নাম বলুন।")
        }

        // Try searching contacts
        return try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
                arrayOf("%$targetName%"),
                null
            )

            var foundName: String? = null
            var foundNumber: String? = null

            cursor?.use {
                if (it.moveToFirst()) {
                    foundName = it.getString(0)
                    foundNumber = it.getString(1)
                }
            }

            if (foundName != null && foundNumber != null) {
                CommandResult.CallConfirmation(
                    name = foundName!!,
                    number = foundNumber!!,
                    prompt = "$userName, $foundName ($foundNumber) কে কল করতে চান? নিশ্চিত করুন।"
                )
            } else {
                CommandResult.SpokenAnswer("$userName, ফোনে '$targetName' নামে কোনো কনট্যাক্ট পাওয়া যায়নি।")
            }
        } catch (e: SecurityException) {
            CommandResult.SpokenAnswer("কনট্যাক্ট খোঁজার জন্য ফোন পারমিশন প্রয়োজন।")
        } catch (e: Exception) {
            CommandResult.SpokenAnswer("কল করতে সমস্যা হয়েছে: ${e.message}")
        }
    }
}
