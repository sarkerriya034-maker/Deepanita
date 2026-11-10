package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.KnowledgeBase
import com.example.presentation.MainViewModel
import com.example.presentation.ScreenDestination
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.LotusPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MedicalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedIndex by remember { mutableStateOf<Int?>(0) }

    val filteredList = KnowledgeBase.MEDICINES.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.genericName.contains(searchQuery, ignoreCase = true) ||
                it.commonUses.contains(searchQuery, ignoreCase = true)
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
                    text = "AI চিকিৎসা তথ্য সহকারী",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "ওষুধের সাধারণ ব্যবহার, নিয়ম ও সতর্কতা",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        // Safety Disclaimer Card (MANDATORY)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GoldenAmber.copy(alpha = 0.15f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "সতর্কতা: দীপানিতা কোনো ডাক্তার নয়। এখানে প্রদত্ত তথ্য শুধুমাত্র প্রাথমিক স্বাস্থ্য শিক্ষা ও সচেতনতার জন্য। যেকোনো ওষুধ সেবনের পূর্বে অবশ্যই একজন রেজিস্টার্ড ডাক্তারের পরামর্শ নিন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFFFD166),
                        lineHeight = 18.sp
                    )
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ওষুধ বা রোগের নাম অনুসন্ধান করুন...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("medicine_search_bar"),
            shape = RoundedCornerShape(20.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Medicine List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList.indices.toList()) { index ->
                val med = filteredList[index]
                val isExpanded = expandedIndex == index

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isExpanded) NeonCyan.copy(alpha = 0.5f) else SurfaceCardHigh
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedIndex = if (isExpanded) null else index }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                )
                                Text(
                                    text = med.genericName,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val speechText = "${med.name}। সাধারণ ব্যবহার: ${med.commonUses}। সতর্কতা: ${med.warnings}"
                                        viewModel.speak(speechText)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.VolumeUp,
                                        contentDescription = "শুনুন",
                                        tint = LotusPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                DetailSection("ব্যবহার", med.commonUses, Color.White)
                                DetailSection("কীভাবে কাজ করে", med.howItWorks, TextPrimary)
                                DetailSection("পার্শ্বপ্রতিক্রিয়া", med.sideEffects, Color(0xFFFFB4A2))
                                DetailSection("সতর্কতা", med.warnings, GoldenAmber)
                                DetailSection("কখন ডাক্তার দেখাবেন", med.whenToConsult, Color(0xFFFF758F))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String, contentColor: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "• $title:",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = contentColor,
                lineHeight = 18.sp
            ),
            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
        )
    }
}
