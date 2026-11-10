package com.example.presentation.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.MainViewModel
import com.example.presentation.ScreenDestination
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voiceSettings by viewModel.voiceSettings.collectAsState()

    var pitch by remember { mutableFloatStateOf(voiceSettings.pitch) }
    var rate by remember { mutableFloatStateOf(voiceSettings.speechRate) }
    var userNameInput by remember { mutableStateOf(voiceSettings.userName) }
    var selectedLanguage by remember { mutableStateOf(voiceSettings.language) }
    var wakeWordActive by remember { mutableStateOf(voiceSettings.isWakeWordEnabled) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(ScreenDestination.AVATAR_VOICE) },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardHigh)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "ফিরে যান", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "সেটিংস ও কন্ট্রোল",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "ভয়েস টিউন, ভাষা ও গোপনীয়তা",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        // Voice Personalization Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ভয়েস ও ব্যক্তিত্ব টিউন (Sweet Female Voice)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // User name input
                OutlinedTextField(
                    value = userNameInput,
                    onValueChange = {
                        userNameInput = it
                        viewModel.updateVoiceSettings(rate, pitch, selectedLanguage, it)
                    },
                    label = { Text("ব্যবহারকারীর নাম (ইউজারনেম)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SurfaceCardHigh,
                        focusedContainerColor = SurfaceCardHigh.copy(alpha = 0.5f),
                        unfocusedContainerColor = SurfaceCardHigh.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pitch Slider
                Text(
                    text = "ভয়েস পিচ (Voice Pitch): %.2f".format(pitch),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                )
                Slider(
                    value = pitch,
                    onValueChange = {
                        pitch = it
                        viewModel.updateVoiceSettings(rate, it, selectedLanguage, userNameInput)
                    },
                    valueRange = 0.8f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = LotusPink, activeTrackColor = LotusPink)
                )

                // Speech Rate Slider
                Text(
                    text = "কথা বলার গতি (Speech Rate): %.2f".format(rate),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                )
                Slider(
                    value = rate,
                    onValueChange = {
                        rate = it
                        viewModel.updateVoiceSettings(it, pitch, selectedLanguage, userNameInput)
                    },
                    valueRange = 0.7f..1.4f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Button(
                    onClick = {
                        viewModel.speak("হ্যালো সিদ্ধার্থ! এটি আমার মিষ্টি ও স্বাভাবিক ভয়েস টিউন। কেমন লাগছে তোমার?")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHigh)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ভয়েস টেস্ট শুনুন", color = Color.White)
                }
            }
        }

        // Language & Wake Word Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ভাষা ও ওয়েক-ওয়ার্ড",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ওয়েক-ওয়ার্ড ডিটেকশন ('Hey Deepanita')",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                        Text(
                            text = "মুখোমুখি থাকলে দীপানিতা সাড়া দেবে",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                    Switch(
                        checked = wakeWordActive,
                        onCheckedChange = { wakeWordActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = RoyalPurple)
                    )
                }
            }
        }

        // Permissions & Privacy Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "গোপনীয়তা ও অ্যাপ পারমিশন",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "দীপানিতা কোনো গোপনীয় ব্যক্তিগত ডাটা, পাসওয়ার্ড বা অডিও অননুমোদিতভাবে ক্লাউডে আপলোড করে না। সমস্ত অফলাইন কমান্ড ফোনের অভ্যন্তরীণ প্রসেসিং দ্বারা চালিত হয়।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHigh)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldenAmber)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অ্যান্ড্রয়েড পারমিশন ম্যানেজার খুলুন", color = Color.White)
                }
            }
        }

        // Creator Info & About
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCardHigh.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = LotusPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEEPANITA (দীপানিতা) AI Assistant",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "নির্মাতা ও উদ্ভাবক: সিদ্ধার্থ মোজুমদার (Siddhartha Mojumdar)",
                    style = MaterialTheme.typography.bodyMedium.copy(color = NeonCyan, fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "ভার্সন: 1.0.0 • MVVM & Clean Architecture\nঅনলাইন এবং অফলাইন স্মার্ট ভয়েস ইন্টেলিজেন্স সিস্টেম",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, lineHeight = 16.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
