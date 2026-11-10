package com.example.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ScreenDestination
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextSecondary

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val destination: ScreenDestination,
    val testTag: String
)

@Composable
fun DeepanitaBottomBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("ভয়েস", Icons.Default.RecordVoiceOver, ScreenDestination.AVATAR_VOICE, "nav_item_voice"),
        BottomNavItem("হোম", Icons.Default.Dashboard, ScreenDestination.DASHBOARD, "nav_item_home"),
        BottomNavItem("চিকিৎসা", Icons.Default.LocalHospital, ScreenDestination.MEDICAL, "nav_item_medical"),
        BottomNavItem("ম্যাপ", Icons.Default.Navigation, ScreenDestination.NAVIGATION, "nav_item_nav"),
        BottomNavItem("গল্প-গান", Icons.Default.MusicNote, ScreenDestination.STORIES_MUSIC, "nav_item_stories"),
        BottomNavItem("সেটিংস", Icons.Default.Settings, ScreenDestination.SETTINGS, "nav_item_settings")
    )

    Surface(
        color = SurfaceCard.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardHigh),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.destination
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) NeonCyan else TextSecondary,
                    label = "icon_color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.destination) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag(item.testTag)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.18f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else TextSecondary
                        )
                    )
                }
            }
        }
    }
}
