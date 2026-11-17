package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameplayScreen
import com.example.model.AppScreen
import com.example.ui.screens.GarageScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.DhakaRickshawTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DhakaRickshawTheme {
                DhakaRickshawApp()
            }
        }
    }
}

@Composable
fun DhakaRickshawApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val launchConfig by viewModel.launchConfig.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.MAIN_MENU -> MainMenuScreen(
                        progress = progress,
                        onStartEndless = { viewModel.startEndlessRide() },
                        onNavigate = { viewModel.navigateTo(it) }
                    )

                    AppScreen.MISSIONS -> MissionsScreen(
                        progress = progress,
                        onStartMission = { viewModel.startMission(it) },
                        onStartChallenge = { viewModel.startChallenge(it) },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )

                    AppScreen.GARAGE -> GarageScreen(
                        progress = progress,
                        onSelectOrBuyDesign = { id, price -> viewModel.selectOrBuyDesign(id, price) },
                        onSelectOrBuyCanopy = { id, price -> viewModel.selectOrBuyCanopy(id, price) },
                        onSelectOrBuyDecoration = { id, price -> viewModel.selectOrBuyDecoration(id, price) },
                        onUpgradeStat = { stat -> viewModel.upgradeStat(stat) },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )

                    AppScreen.SHOP -> ShopScreen(
                        progress = progress,
                        onBuyDesign = { id, price -> viewModel.selectOrBuyDesign(id, price) },
                        onBuyCanopy = { id, price -> viewModel.selectOrBuyCanopy(id, price) },
                        onBuyDecoration = { id, price -> viewModel.selectOrBuyDecoration(id, price) },
                        onClaimDailyBonus = { viewModel.claimDailyBonus() },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        progress = progress,
                        settings = settings,
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleMusic = { viewModel.toggleMusic() },
                        onToggleVoice = { viewModel.toggleVoice() },
                        voiceSummaryBn = viewModel.getVoiceSummaryBn(),
                        onTestVoiceRole = { role -> viewModel.testVoiceRole(role) },
                        onSelectWeatherPref = { viewModel.setWeatherPreference(it) },
                        onSelectSteeringMode = { viewModel.setSteeringMode(it) },
                        onResetProgress = { viewModel.resetProgress() },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )

                    AppScreen.GAMEPLAY -> GameplayScreen(
                        launchConfig = launchConfig,
                        progress = progress,
                        settings = settings,
                        audioManager = viewModel.audioManager,
                        onToggleVoice = { viewModel.toggleVoice() },
                        onToggleSound = { viewModel.toggleSound() },
                        onRunCompleted = { summary, mId, cId ->
                            viewModel.onRunCompleted(summary, mId, cId)
                        },
                        onExitToMenu = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )
                }
            }
        }
    }
}
