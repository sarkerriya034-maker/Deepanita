package com.example.presentation.screens

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

data class PhoneAppItem(
    val name: String,
    val description: String,
    val packageName: String?,
    val intentAction: String?,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun AppLauncherScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val apps = listOf(
        PhoneAppItem("WhatsApp", "মেসেজিং ও কলিং", "com.whatsapp", null, Icons.Default.Chat, Color(0xFF25D366)),
        PhoneAppItem("YouTube", "ভিডিও ও গান", "com.google.android.youtube", null, Icons.Default.VideoLibrary, Color(0xFFFF0000)),
        PhoneAppItem("Facebook", "সোশ্যাল মিডিয়া", "com.facebook.katana", null, Icons.Default.Chat, Color(0xFF1877F2)),
        PhoneAppItem("গুগল ম্যাপস", "রাস্তা ও নেভিগেশন", "com.google.android.apps.maps", null, Icons.Default.Map, Color(0xFF4285F4)),
        PhoneAppItem("ক্যামেরা", "ছবি ও ভিডিও তোলা", null, MediaStore.ACTION_IMAGE_CAPTURE, Icons.Default.CameraAlt, NeonCyan),
        PhoneAppItem("গ্যালারি", "ছবি ও অ্যালবাম", null, Intent.ACTION_VIEW, Icons.Default.PhotoLibrary, GoldenAmber),
        PhoneAppItem("ক্যালকুলেটর", "গণিত ও হিসাব", "com.google.android.calculator", null, Icons.Default.Calculate, Color(0xFF00E5FF)),
        PhoneAppItem("সেটিংস", "ফোন কন্ট্রোল", "com.android.settings", Settings.ACTION_SETTINGS, Icons.Default.Settings, Color(0xFF9E9E9E))
    )

    fun launchApp(app: PhoneAppItem) {
        try {
            if (app.packageName != null) {
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    viewModel.speak("${app.name} চালু করছি।")
                } else {
                    viewModel.speak("আপনার ফোনে ${app.name} ইনস্টল করা নেই।")
                }
            } else if (app.intentAction != null) {
                val intent = Intent(app.intentAction).apply {
                    if (app.intentAction == Intent.ACTION_VIEW) {
                        type = "image/*"
                    }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                viewModel.speak("${app.name} চালু করছি।")
            }
        } catch (e: Exception) {
            viewModel.speak("অ্যাপটি চালু করতে সমস্যা হয়েছে।")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(16.dp)
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
                    text = "স্মার্ট অ্যাপস লঞ্চার",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "ভয়েস কমান্ড বা এক ট্যাপে যেকোনো অ্যাপ খুলুন",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(apps) { app ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clickable { launchApp(app) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(app.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = app.icon,
                                contentDescription = app.name,
                                tint = app.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = app.description,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
