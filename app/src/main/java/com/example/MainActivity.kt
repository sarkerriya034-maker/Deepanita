package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.presentation.MainViewModel
import com.example.presentation.ScreenDestination
import com.example.presentation.components.DeepanitaBottomBar
import com.example.presentation.screens.AppLauncherScreen
import com.example.presentation.screens.AvatarVoiceScreen
import com.example.presentation.screens.CameraAssistantScreen
import com.example.presentation.screens.ChatScreen
import com.example.presentation.screens.DashboardScreen
import com.example.presentation.screens.MedicalScreen
import com.example.presentation.screens.NavigationScreen
import com.example.presentation.screens.RemindersAlarmsScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.screens.StoriesAndMusicScreen
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepanitaTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeepanitaTheme(darkTheme = true) {
                DeepanitaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DeepanitaApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val callConfirmation by viewModel.callConfirmation.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Request permissions on launch
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle result
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    LaunchedEffect(snackMessage) {
        snackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnack()
        }
    }

    // Handle Back Press
    BackHandler(enabled = currentScreen != ScreenDestination.AVATAR_VOICE) {
        viewModel.navigateTo(ScreenDestination.AVATAR_VOICE)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DeepanitaBottomBar(
                currentScreen = currentScreen,
                onNavigate = { dest -> viewModel.navigateTo(dest) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.AVATAR_VOICE -> AvatarVoiceScreen(viewModel = viewModel)
                ScreenDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                ScreenDestination.CHAT -> ChatScreen(viewModel = viewModel)
                ScreenDestination.MEDICAL -> MedicalScreen(viewModel = viewModel)
                ScreenDestination.NAVIGATION -> NavigationScreen(viewModel = viewModel)
                ScreenDestination.CAMERA -> CameraAssistantScreen(viewModel = viewModel)
                ScreenDestination.STORIES_MUSIC -> StoriesAndMusicScreen(viewModel = viewModel)
                ScreenDestination.REMINDERS_ALARMS -> RemindersAlarmsScreen(viewModel = viewModel)
                ScreenDestination.APP_LAUNCHER -> AppLauncherScreen(viewModel = viewModel)
                ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Call Confirmation Dialog
    if (callConfirmation != null) {
        val conf = callConfirmation!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissCall() },
            title = { Text("ফোন কল নিশ্চিতকরণ", color = Color.White) },
            text = {
                Text(
                    text = "${conf.name} (${conf.number}) কে ফোন করতে চান?",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmCall() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("কল করুন", color = DeepNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissCall() }) {
                    Text("বাতিল", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = SurfaceCard
        )
    }
}
