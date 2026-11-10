package com.example.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.AssistantStatus
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RoyalPurple
import kotlin.random.Random

@Composable
fun VoiceWaveform(
    status: AssistantStatus,
    amplitude: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    maxHeight: Dp = 48.dp
) {
    val isActive = status == AssistantStatus.SPEAKING || status == AssistantStatus.LISTENING

    val gradient = Brush.verticalGradient(
        colors = when (status) {
            AssistantStatus.SPEAKING -> listOf(LotusPink, RoyalPurple, NeonCyan)
            AssistantStatus.LISTENING -> listOf(NeonCyan, Color(0xFF38BDF8), LotusPink)
            else -> listOf(NeonCyan.copy(alpha = 0.5f), RoyalPurple.copy(alpha = 0.5f))
        }
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val barAnim = remember { Animatable(0.2f) }

            LaunchedEffect(isActive, amplitude) {
                if (isActive) {
                    val targetScale = (0.2f + (amplitude * Random.nextFloat() * 0.8f)).coerceIn(0.15f, 1.0f)
                    barAnim.animateTo(
                        targetValue = targetScale,
                        animationSpec = tween(
                            durationMillis = 80 + (index * 12),
                            easing = LinearEasing
                        )
                    )
                } else {
                    barAnim.animateTo(0.15f, animationSpec = tween(300))
                }
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(barAnim.value)
                    .clip(RoundedCornerShape(2.dp))
                    .background(gradient)
            )
        }
    }
}
