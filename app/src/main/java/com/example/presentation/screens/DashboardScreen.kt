package com.example.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.OfflineOrange
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class DashboardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val destination: ScreenDestination,
    val tag: String
)

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val lastAnswer by viewModel.lastSpokenAnswer.collectAsState()

    val dashboardItems = listOf(
        DashboardItem("ভয়েস অ্যাসিস্ট্যান্ট", "দীপানিতার সাথে কথা বলুন", Icons.Default.RecordVoiceOver, NeonCyan, ScreenDestination.AVATAR_VOICE, "dash_voice"),
        DashboardItem("AI চ্যাট হিস্ট্রি", "পূর্বের কথোপকথন দেখুন", Icons.Default.Chat, LotusPink, ScreenDestination.CHAT, "dash_chat"),
        DashboardItem("চিকিৎসা তথ্য", "ওষুধ, লক্ষণ ও প্রাথমিক চিকিৎসা", Icons.Default.LocalHospital, Color(0xFFFF5252), ScreenDestination.MEDICAL, "dash_medical"),
        DashboardItem("GPS নেভিগেশন", "গুগল ম্যাপ ও কাছের স্থান", Icons.Default.Navigation, Color(0xFF4CAF50), ScreenDestination.NAVIGATION, "dash_nav"),
        DashboardItem("AI ক্যামেরা", "টেক্সট ও অবজেক্ট স্ক্যানার", Icons.Default.CameraAlt, GoldenAmber, ScreenDestination.CAMERA, "dash_camera"),
        DashboardItem("গল্প ও সংগীত", "রোমান্টিক গল্প, রূপকথা ও গান", Icons.Default.MusicNote, RoyalPurple, ScreenDestination.STORIES_MUSIC, "dash_stories"),
        DashboardItem("অ্যালার্ম ও রিমাইন্ডার", "টাইমার, অ্যালার্ম ও নোট", Icons.Default.Alarm, Color(0xFF29B6F6), ScreenDestination.REMINDERS_ALARMS, "dash_alarms"),
        DashboardItem("স্মার্ট ফোন কন্ট্রোল", "হোয়াটসঅ্যাপ, ফেসবুক ও অ্যাপস", Icons.Default.Apps, Color(0xFFAB47BC), ScreenDestination.APP_LAUNCHER, "dash_apps"),
        DashboardItem("সেটিংস ও নিরাপত্তা", "ভয়েস, গোপনীয়তা ও পারমিশন", Icons.Default.Settings, Color(0xFF78909C), ScreenDestination.SETTINGS, "dash_settings")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Greeting Banner
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.5f), RoyalPurple.copy(alpha = 0.5f)))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "হ্যালো, ${voiceSettings.userName}!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "আমি দীপানিতা, তোমার ব্যক্তিগত এআই সহকারী।",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isOnline) OnlineGreen.copy(alpha = 0.2f) else OfflineOrange.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isOnline) OnlineGreen else OfflineOrange),
                    modifier = Modifier.clickable { viewModel.toggleOnlineMode() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isOnline) OnlineGreen else OfflineOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOnline) "অনলাইন" else "অফলাইন",
                            color = if (isOnline) OnlineGreen else OfflineOrange,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Feature Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(dashboardItems) { item ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag(item.tag)
                        .clickable { viewModel.navigateTo(item.destination) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = item.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
