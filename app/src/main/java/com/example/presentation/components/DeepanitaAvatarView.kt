package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.presentation.AssistantStatus
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RoyalPurple
import kotlin.math.roundToInt

@Composable
fun DeepanitaAvatarView(
    status: AssistantStatus,
    amplitude: Float,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 220.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

    // Gentle breathing vertical float
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_float"
    )

    // Glowing lotus aura rotation
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "aura_rotation"
    )

    // Pulse scale when speaking or listening
    val pulseMultiplier = when (status) {
        AssistantStatus.SPEAKING -> 1f + (amplitude * 0.18f)
        AssistantStatus.LISTENING -> 1.05f + (amplitude * 0.12f)
        AssistantStatus.PROCESSING -> 1.03f
        AssistantStatus.IDLE -> 1f
    }

    val glowColor1 = when (status) {
        AssistantStatus.SPEAKING -> LotusPink
        AssistantStatus.LISTENING -> NeonCyan
        AssistantStatus.PROCESSING -> RoyalPurple
        AssistantStatus.IDLE -> NeonCyan.copy(alpha = 0.6f)
    }

    val glowColor2 = when (status) {
        AssistantStatus.SPEAKING -> NeonCyan
        AssistantStatus.LISTENING -> LotusPink
        AssistantStatus.PROCESSING -> Color(0xFFE056FD)
        AssistantStatus.IDLE -> RoyalPurple.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .size(avatarSize + 48.dp)
            .offset { IntOffset(0, floatAnim.roundToInt()) }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing ripple ring 1
        Box(
            modifier = Modifier
                .size(avatarSize + 36.dp)
                .scale(pulseMultiplier)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor1.copy(alpha = 0.45f),
                            glowColor2.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Middle animated neon gradient border ring
        Box(
            modifier = Modifier
                .size(avatarSize + 12.dp)
                .scale(pulseMultiplier * 0.98f)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            NeonCyan,
                            LotusPink,
                            RoyalPurple,
                            NeonCyan
                        )
                    )
                )
                .padding(3.dp)
        )

        // Inner Avatar Image
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.deepanita_avatar),
                contentDescription = "দীপানিতা (Deepanita AI Voice Assistant Avatar)",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
