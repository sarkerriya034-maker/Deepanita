package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.AssistantStatus
import com.example.presentation.MainViewModel
import com.example.presentation.ScreenDestination
import com.example.presentation.components.DeepanitaAvatarView
import com.example.presentation.components.VoiceWaveform
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OfflineOrange
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AvatarVoiceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val status by viewModel.status.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val lastAnswer by viewModel.lastSpokenAnswer.collectAsState()
    val currentQuery by viewModel.currentQuery.collectAsState()
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val ttsAmplitude by viewModel.ttsManager.speechAmplitude.collectAsState()

    var showTextInput by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "তোমার নাম কী?",
        "তোমাকে কে বানিয়েছে?",
        "হ্যালো দীপানিতা, কেমন আছো?",
        "আমার ফোনে কত চার্জ আছে?",
        "কাছের ফার্মেসি কোথায়?",
        "একটি মিষ্টি প্রেমের গল্প শোনাও",
        "আমাকে একটি গান শোনাও",
        "প্যারাসিটামল ওষুধের কাজ কী?",
        "সকাল ৬টায় অ্যালার্ম দাও",
        "WhatsApp খোলো"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        Color(0xFF0D1533),
                        Color(0xFF080D21)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DEEPANITA",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = NeonCyan
                        )
                    )
                    Text(
                        text = "নির্মাতা: সিদ্ধার্থ মোজুমদার",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LotusPink,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Online/Offline Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isOnline) OnlineGreen.copy(alpha = 0.2f) else OfflineOrange.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isOnline) OnlineGreen else OfflineOrange
                        ),
                        modifier = Modifier
                            .testTag("online_mode_toggle")
                            .clickable { viewModel.toggleOnlineMode() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = if (isOnline) "অনলাইন মোড" else "অফলাইন মোড",
                                tint = if (isOnline) OnlineGreen else OfflineOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOnline) "অনলাইন" else "অফলাইন",
                                color = if (isOnline) OnlineGreen else OfflineOrange,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dashboard Icon Button
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.DASHBOARD) },
                        modifier = Modifier
                            .testTag("nav_to_dashboard")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardHigh)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "ড্যাশবোর্ড",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Center Avatar
            DeepanitaAvatarView(
                status = status,
                amplitude = ttsAmplitude,
                avatarSize = 210.dp,
                onClick = {
                    if (status == AssistantStatus.SPEAKING) {
                        viewModel.stopSpeaking()
                    } else {
                        viewModel.toggleVoiceListening()
                    }
                }
            )

            // Status Indicator Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCardHigh.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = when (status) {
                        AssistantStatus.LISTENING -> "🎙️ শুনছি... কথা বলুন"
                        AssistantStatus.SPEAKING -> "🗣️ দীপানিতা কথা বলছে..."
                        AssistantStatus.PROCESSING -> "⚡ উত্তর প্রস্তুত করছি..."
                        AssistantStatus.IDLE -> "✨ মাইক্রোফোনে ট্যাপ করে কথা বলুন"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = when (status) {
                            AssistantStatus.LISTENING -> NeonCyan
                            AssistantStatus.SPEAKING -> LotusPink
                            AssistantStatus.PROCESSING -> RoyalPurple
                            AssistantStatus.IDLE -> TextSecondary
                        },
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Dynamic Waveform
            VoiceWaveform(
                status = status,
                amplitude = ttsAmplitude,
                modifier = Modifier.padding(vertical = 4.dp),
                barCount = 20,
                maxHeight = 36.dp
            )

            // Spoken Response Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.95f)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.4f), LotusPink.copy(alpha = 0.4f)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp, max = 200.dp)
                    .padding(vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (currentQuery.isNotBlank()) {
                        Text(
                            text = "আপনি: $currentQuery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonCyan,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text(
                        text = lastAnswer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // Quick Suggestions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPrompts.forEach { prompt ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceCardHigh.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clickable {
                                viewModel.submitQuery(prompt)
                            }
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Text input expander
            AnimatedVisibility(visible = showTextInput, enter = fadeIn(), exit = fadeOut()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("এখানে বার্তা লিখুন...", color = TextSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("text_query_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = SurfaceCardHigh,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.submitQuery(textInput)
                                textInput = ""
                                showTextInput = false
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "পাঠান", tint = DeepNavy)
                    }
                }
            }

            // Bottom Voice Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat Last Answer Button
                IconButton(
                    onClick = { viewModel.repeatLastAnswer() },
                    modifier = Modifier
                        .testTag("repeat_button")
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(SurfaceCardHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "উত্তর পুনরায় শুনুন",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Main Big Neon Mic Button
                val isMicActive = status == AssistantStatus.LISTENING
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(16.dp, CircleShape, spotColor = if (isMicActive) LotusPink else NeonCyan)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (isMicActive) listOf(LotusPink, Color(0xFFFF0055)) else listOf(NeonCyan, RoyalPurple)
                            )
                        )
                        .clickable {
                            viewModel.toggleVoiceListening()
                        }
                        .testTag("mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMicActive) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isMicActive) "কথা বলা বন্ধ করুন" else "কথা বলুন",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Stop / Keyboard toggle Button
                if (status == AssistantStatus.SPEAKING) {
                    IconButton(
                        onClick = { viewModel.stopSpeaking() },
                        modifier = Modifier
                            .testTag("stop_speech_button")
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(LotusPink.copy(alpha = 0.25f))
                            .border(1.dp, LotusPink, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "কথা থামান",
                            tint = LotusPink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { showTextInput = !showTextInput },
                        modifier = Modifier
                            .testTag("toggle_keyboard_button")
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardHigh)
                    ) {
                        Icon(
                            imageVector = if (showTextInput) Icons.Default.Close else Icons.Default.Keyboard,
                            contentDescription = "কীবোর্ড মোড",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
