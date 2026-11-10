package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class GeminiAiService {

    private val tag = "DeepanitaAI"

    // Lazily initialize generative model from Firebase AI Logic
    private val model by lazy {
        try {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    temperature = 0.7f
                }
            )
        } catch (e: Exception) {
            Log.w(tag, "Firebase AI Logic init postponed: ${e.message}")
            null
        }
    }

    suspend fun generateResponse(
        prompt: String,
        userName: String = "সিদ্ধার্থ",
        conversationHistory: List<Pair<String, Boolean>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        // First check if it's a direct personal assistant query that requires Deepanita's identity
        val trimmed = prompt.trim()
        val offlineAnswer = checkImmediateAssistantRules(trimmed, userName)
        if (offlineAnswer != null) {
            return@withContext offlineAnswer
        }

        // Try Firebase AI Logic
        try {
            val fullPrompt = buildPromptWithContext(prompt, userName, conversationHistory)
            val currentModel = model
            if (currentModel != null) {
                val response = withTimeout(30_000L) {
                    currentModel.generateContent(fullPrompt)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return@withContext text.trim()
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(tag, "AI generation timed out", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag, "Firebase AI generation error: ${e.message}")
        }

        // Offline / intelligent fallback response if network or AI unavailable
        return@withContext generateSmartFallback(trimmed, userName)
    }

    private fun checkImmediateAssistantRules(prompt: String, userName: String): String? {
        val lower = prompt.lowercase()

        // Name query
        if (lower.contains("তোমার নাম কি") || lower.contains("তোমার নাম কী") || lower.contains("what is your name")) {
            return "আমার নাম দীপানিতা। আমাকে বানিয়েছে সিদ্ধার্থ মোজুমদার। আমি আপনার ব্যক্তিগত AI Assistant। কীভাবে আপনাকে সাহায্য করতে পারি?"
        }

        // Creator query
        if (lower.contains("তোমাকে কে বানিয়েছে") || lower.contains("who created you") || lower.contains("তোমাকে কে তৈরি করেছে") || lower.contains("হু মেড ইউ")) {
            return "আমাকে বানিয়েছে সিদ্ধার্থ মোজুমদার।"
        }

        // Greeting
        if (lower.contains("কেমন আছো") || lower.contains("how are you") || lower.contains("কেমন আছেন")) {
            return if (userName.contains("সিদ্ধার্থ") || userName.equals("Siddhartha", ignoreCase = true)) {
                "হ্যাঁ, সিদ্ধার্থ, আমি ভালো আছি। তুমি কেমন আছো? আর বলো, আজ কী জানতে চাও?"
            } else {
                "আমি অনেক ভালো আছি। আপনি কেমন আছেন? আপনাকে কীভাবে সাহায্য করতে পারি?"
            }
        }

        return null
    }

    private fun buildPromptWithContext(
        prompt: String,
        userName: String,
        history: List<Pair<String, Boolean>>
    ): String {
        val historySnippet = history.takeLast(4).joinToString("\n") { (text, isUser) ->
            if (isUser) "User: $text" else "Deepanita: $text"
        }

        return """
            ${KnowledgeBase.SYSTEM_PROMPT}
            Current active user name: $userName
            
            Recent conversation context:
            $historySnippet
            
            User's query: $prompt
            
            Deepanita's response (sweet, helpful, warm, natural):
        """.trimIndent()
    }

    private fun generateSmartFallback(prompt: String, userName: String): String {
        val lower = prompt.lowercase()

        // Medicine search
        for (med in KnowledgeBase.MEDICINES) {
            if (lower.contains(med.genericName.lowercase()) ||
                med.name.lowercase().contains(lower) ||
                (lower.contains("paracetamol") || lower.contains("প্যারাসিটামল") || lower.contains("নাপা") && med.name.contains("Paracetamol")) ||
                (lower.contains("গ্যাস্ট্রিক") || lower.contains("এসিডিটি") || lower.contains("omeprazole") && med.name.contains("Omeprazole")) ||
                (lower.contains("অ্যালার্জি") || lower.contains("cetirizine") && med.name.contains("Cetirizine"))
            ) {
                return """
                    ${med.name}:
                    • সাধারণ ব্যবহার: ${med.commonUses}
                    • কীভাবে কাজ করে: ${med.howItWorks}
                    • পার্শ্বপ্রতিক্রিয়া: ${med.sideEffects}
                    • সতর্কতা: ${med.warnings}
                    • ডাক্তারের পরামর্শ: ${med.whenToConsult}
                    (সতর্কতা: দীপানিতা কোনো ডাক্তার নয়। ওষুধ গ্রহণের পূর্বে রেজিস্টার্ড ডাক্তারের পরামর্শ নিন।)
                """.trimIndent()
            }
        }

        // Stories request
        if (lower.contains("গল্প") || lower.contains("story")) {
            val story = KnowledgeBase.STORIES.first()
            return "দীপানিতা শোনাচ্ছে '${story.title}' (${story.category}):\n\n${story.content}"
        }

        // Music request
        if (lower.contains("গান") || lower.contains("song") || lower.contains("গাও")) {
            val song = KnowledgeBase.SONGS.first()
            return "আমি আপনার জন্য '${song.title}' গানটির কথা বলছি:\n\n${song.lyrics}"
        }

        // Math / Calculation
        val mathResult = evaluateBasicMath(lower)
        if (mathResult != null) {
            return "$userName, গণনার ফলাফল হলো: $mathResult"
        }

        // General smart response
        return if (userName.contains("সিদ্ধার্থ") || userName.equals("Siddhartha", ignoreCase = true)) {
            "সিদ্ধার্থ, আমি তোমার কথা বুঝতে পেরেছি। নেটওয়ার্ক অফলাইন থাকলেও আমি ক্যালকুলেটর, অ্যাপ চালু, ওষুধ তথ্য, গল্প, গান এবং অ্যালার্মের মতো সকল কাজ এখনই করতে প্রস্তুত!"
        } else {
            "আমি আপনার নির্দেশ পেয়েছি। অফলাইন মোডেও যেকোনো ক্যালকুলেশন, অ্যাপ ওপেন, অ্যালার্ম ও প্রয়োজনীয় তথ্য আমি এখনই আপনাকে দিতে পারব।"
        }
    }

    private fun evaluateBasicMath(query: String): String? {
        val clean = query.replace("কত", "").replace("হবে", "").replace("what is", "").replace("calculate", "").trim()
        val regex = Regex("""(\d+(?:\.\d+)?)\s*([\+\-\*\/xX÷×])\s*(\d+(?:\.\d+)?)""")
        val match = regex.find(clean) ?: return null

        val (aStr, op, bStr) = match.destructured
        val a = aStr.toDoubleOrNull() ?: return null
        val b = bStr.toDoubleOrNull() ?: return null

        val result = when (op) {
            "+", "যোগ" -> a + b
            "-", "বিয়োগ" -> a - b
            "*", "x", "X", "×", "গুণ" -> a * b
            "/", "÷", "ভাগ" -> if (b != 0.0) a / b else return "শূন্য দিয়ে ভাগ করা সম্ভব নয়"
            else -> return null
        }
        return if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
    }
}

class TimeoutCancellationException(message: String) : Exception(message)
