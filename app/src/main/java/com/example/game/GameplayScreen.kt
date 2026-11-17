package com.example.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioManager
import com.example.data.MissionCatalog
import com.example.game.RickshawCanvasRenderer.renderWorld
import com.example.model.ActiveLaunchConfig
import com.example.model.GameMode
import com.example.model.GameSettings
import com.example.model.MissionGoalType
import com.example.model.PlayerProgress
import com.example.model.RunSummary
import com.example.model.SteeringMode
import com.example.ui.components.AnimatedSpeechBubble
import com.example.ui.components.CoinBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.CoinGold
import com.example.ui.theme.InkCharcoal
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson
import kotlin.math.roundToInt

@Composable
fun GameplayScreen(
    launchConfig: ActiveLaunchConfig,
    progress: PlayerProgress,
    settings: GameSettings,
    audioManager: GameAudioManager,
    onToggleVoice: () -> Unit,
    onToggleSound: () -> Unit,
    onRunCompleted: (RunSummary, String?, String?) -> Unit,
    onExitToMenu: () -> Unit
) {
    var runSeed by remember { mutableLongStateOf(0L) }
    var engine by remember(runSeed, launchConfig) {
        mutableStateOf(
            GameEngine(
                mode = launchConfig.mode,
                mission = launchConfig.mission,
                challenge = launchConfig.challenge,
                progress = progress,
                settings = settings
            )
        )
    }

    var isPaused by remember { mutableStateOf(false) }
    var frameTick by remember { mutableLongStateOf(0L) }
    var summaryPosted by remember(runSeed) { mutableStateOf(false) }
    var runSummary by remember(runSeed) { mutableStateOf<RunSummary?>(null) }

    // Multi-touch input states
    var steerLeft by remember { mutableStateOf(false) }
    var steerRight by remember { mutableStateOf(false) }
    var pedalHeld by remember { mutableStateOf(false) }
    var brakeHeld by remember { mutableStateOf(false) }
    var hornPressed by remember { mutableStateOf(false) }
    var pendingDragDeltaX by remember { mutableStateOf(0f) }

    val design = remember(progress.equippedDesignId) {
        MissionCatalog.designById(progress.equippedDesignId)
    }
    val canopy = remember(progress.equippedCanopyId) {
        MissionCatalog.canopyById(progress.equippedCanopyId)
    }
    val decoration = remember(progress.equippedDecorationId) {
        MissionCatalog.decorationById(progress.equippedDecorationId)
    }

    BackHandler {
        if (runSummary != null) {
            onExitToMenu()
        } else {
            isPaused = !isPaused
        }
    }

    fun dispatchEvents(events: List<GameSoundEvent>) {
        for (ev in events) {
            when (ev) {
                GameSoundEvent.Coin -> audioManager.playCoinPickup()
                GameSoundEvent.Pickup, GameSoundEvent.Delivery -> audioManager.playPassengerChime()
                GameSoundEvent.Crash -> audioManager.playCrash()
                GameSoundEvent.Bell -> audioManager.playBellHorn()
                GameSoundEvent.Victory -> audioManager.playVictory()
                is GameSoundEvent.DistantHorn -> audioManager.triggerDistantTrafficHorn(ev.isBus)
                is GameSoundEvent.Speak -> audioManager.speakBengali(ev.line.textBn, ev.voiceRole)
            }
        }
    }

    // Manage Dhaka City Ambient Soundscape (crowd chatter, tea-stall clinks, distant honking, rain)
    DisposableEffect(Unit) {
        onDispose {
            audioManager.stopDhakaCityAmbience()
        }
    }

    LaunchedEffect(isPaused, engine.isFinished, settings.soundEnabled) {
        if (!isPaused && !engine.isFinished && settings.soundEnabled) {
            audioManager.updateAmbientWeather(engine.weather)
            audioManager.startDhakaCityAmbience()
        } else {
            audioManager.stopDhakaCityAmbience()
        }
    }

    // 60fps Game Loop
    LaunchedEffect(engine, isPaused) {
        var lastNanos = 0L
        while (!engine.isFinished) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L && !isPaused) {
                    val dt = (nanos - lastNanos) / 1_000_000_000f
                    val dragDx = pendingDragDeltaX
                    pendingDragDeltaX = 0f
                    val events = engine.step(
                        dt,
                        InputControls(
                            steerLeft = steerLeft,
                            steerRight = steerRight,
                            pedal = pedalHeld,
                            brake = brakeHeld,
                            dragDeltaX = dragDx
                        )
                    )
                    dispatchEvents(events)
                    audioManager.updateAmbientWeather(engine.weather)
                }
                lastNanos = nanos
                frameTick++
            }
        }
        if (engine.isFinished && !summaryPosted) {
            summaryPosted = true
            val summary = engine.buildSummary()
            runSummary = summary
            val completedMissionId = if (summary.isVictory) launchConfig.mission?.id else null
            val completedChallengeId = if (summary.isVictory) launchConfig.challenge?.id else null
            onRunCompleted(summary, completedMissionId, completedChallengeId)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F2B))
    ) {
        val canvasWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val canvasHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

        // 1. 2D Canvas World
        val swipeModifier = if (settings.steeringMode == SteeringMode.BOTH) {
            Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        pendingDragDeltaX += (dragAmount.x / canvasWidthPx)
                    }
                )
            }
        } else {
            Modifier
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(swipeModifier)
        ) {
            @Suppress("UNUSED_EXPRESSION")
            frameTick
            renderWorld(engine, design, canopy, decoration)
        }

        // 2. Floating Score/Pickup Particles
        @Suppress("UNUSED_EXPRESSION")
        frameTick
        engine.particles.forEach { p ->
            Text(
                text = p.text,
                color = if (p.isPositive) Color(0xFFFFF176) else Color(0xFFFF8A80),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier.offset {
                    IntOffset(
                        x = (p.x * canvasWidthPx - 56f).roundToInt(),
                        y = (p.y * canvasHeightPx).roundToInt()
                    )
                }
            )
        }

        // 3. Top Gameplay HUD (Cleanly Aligned, Safe Status Bar Insets)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = 720.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Telemetry Pill: Hearts | Distance | Speed
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xD90F172A),
                    border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.8f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(engine.maxHearts) { idx ->
                                Icon(
                                    imageVector = if (idx < engine.hearts) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "হৃদয়",
                                    tint = if (idx < engine.hearts) AlertRed else Color(0xFF64748B),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.22f))
                        )

                        Text(
                            text = "${engine.distanceMeters} মি.",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.22f))
                        )

                        Text(
                            text = "${engine.speedKmh.toInt()} কিমি",
                            color = MarigoldGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }

                // Right Utility Cluster: Coins + Voice Toggle + Pause (All 48dp centered vertically)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoinBadge(coins = engine.coinsCollected)

                    Surface(
                        shape = CircleShape,
                        color = Color(0xD90F172A),
                        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.45f)),
                        shadowElevation = 4.dp
                    ) {
                        IconButton(
                            onClick = onToggleVoice,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("hud_voice_toggle")
                        ) {
                            Icon(
                                imageVector = if (settings.voiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = "কণ্ঠস্বর মিউট বা চালু",
                                tint = if (settings.voiceEnabled) MarigoldGold else Color.LightGray
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = RickshawCrimson,
                        border = BorderStroke(1.5.dp, MarigoldGold),
                        shadowElevation = 4.dp
                    ) {
                        IconButton(
                            onClick = { isPaused = true },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("pause_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Pause,
                                contentDescription = "বিরতি",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Row 2: Mission / Challenge objective bar & Passenger status banner
            MissionOrPassengerBanner(engine = engine, launchConfig = launchConfig)

            // Row 3: Funny Bengali Animated Speech Bubble (non-blocking at top!)
            AnimatedSpeechBubble(
                bubble = engine.activeBubble,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .widthIn(max = 520.dp)
            )
        }

        // 4. Subtle Bottom Dashboard Scrim for Crisp Control Contrast
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(118.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xAA080D18))
                    )
                )
        )

        // 5. Unified Single-Row Bottom Control Bar: Left | Brake | Horn | Speedup | Right
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 720.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xDB0F172A),
                border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.8f)),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Left (far left)
                    ArcadeKeyButton(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        labelBn = "বাম",
                        containerColor = Color(0xFF1E293B),
                        activeColor = Color(0xFF334155),
                        borderColor = MarigoldGold.copy(alpha = 0.85f),
                        contentColor = Color.White,
                        testTag = "steer_left_button",
                        modifier = Modifier
                            .weight(1.05f)
                            .height(64.dp),
                        onPressedChange = { steerLeft = it }
                    )

                    // 2. Brake
                    ArcadeKeyButton(
                        icon = Icons.Filled.StopCircle,
                        labelBn = "ব্রেক",
                        containerColor = Color(0xFFDC2626),
                        activeColor = Color(0xFFEF4444),
                        borderColor = Color.White.copy(alpha = 0.9f),
                        contentColor = Color.White,
                        testTag = "brake_button",
                        modifier = Modifier
                            .weight(1.0f)
                            .height(64.dp),
                        onPressedChange = { brakeHeld = it }
                    )

                    // 3. Horn (center)
                    val hornScale by animateFloatAsState(
                        targetValue = if (hornPressed) 0.93f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "hornScale"
                    )
                    val hornShape = RoundedCornerShape(18.dp)
                    Surface(
                        shape = hornShape,
                        color = if (hornPressed) Color(0xFFFDE047) else MarigoldGold,
                        border = BorderStroke(if (hornPressed) 2.5.dp else 1.6.dp, Color.White),
                        shadowElevation = if (hornPressed) 1.dp else 5.dp,
                        modifier = Modifier
                            .weight(0.95f)
                            .height(64.dp)
                            .scale(hornScale)
                            .clip(hornShape)
                            .testTag("horn_button")
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        hornPressed = true
                                        try {
                                            tryAwaitRelease()
                                        } finally {
                                            hornPressed = false
                                        }
                                    },
                                    onTap = {
                                        if (!isPaused && !engine.isFinished) {
                                            dispatchEvents(engine.triggerHorn())
                                        }
                                    }
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = "হর্ন",
                                tint = InkCharcoal,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "হর্ন",
                                color = InkCharcoal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }

                    // 4. Speedup (Pedal)
                    ArcadeKeyButton(
                        icon = Icons.AutoMirrored.Filled.DirectionsBike,
                        labelBn = "গতি",
                        containerColor = BengalEmerald,
                        activeColor = Color(0xFF10B981),
                        borderColor = MarigoldGold,
                        contentColor = Color.White,
                        testTag = "pedal_button",
                        modifier = Modifier
                            .weight(1.1f)
                            .height(64.dp),
                        onPressedChange = { pedalHeld = it }
                    )

                    // 5. Right (far right)
                    ArcadeKeyButton(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        labelBn = "ডান",
                        containerColor = Color(0xFF1E293B),
                        activeColor = Color(0xFF334155),
                        borderColor = MarigoldGold.copy(alpha = 0.85f),
                        contentColor = Color.White,
                        testTag = "steer_right_button",
                        modifier = Modifier
                            .weight(1.05f)
                            .height(64.dp),
                        onPressedChange = { steerRight = it }
                    )
                }
            }
        }

        // 6. Pause Overlay
        if (isPaused && runSummary == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(2.5.dp, MarigoldGold),
                    modifier = Modifier
                        .fillMaxWidth(0.86f)
                        .widthIn(max = 460.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "রাইড বিরতি",
                            style = MaterialTheme.typography.headlineLarge,
                            color = RickshawCrimson
                        )
                        Text(
                            text = "দূরত্ব: ${engine.distanceMeters} মিটার • স্কোর: ${engine.score}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = onToggleSound,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (settings.soundEnabled) "শব্দ: চালু" else "শব্দ: বন্ধ")
                            }
                            OutlinedButton(
                                onClick = onToggleVoice,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (settings.voiceEnabled) "ভয়েস: চালু" else "ভয়েস: বন্ধ")
                            }
                        }

                        Button(
                            onClick = { isPaused = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("resume_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BengalEmerald)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("চালিয়ে যান", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                isPaused = false
                                runSeed++
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("restart_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MarigoldGold, contentColor = InkCharcoal)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("আবার শুরু করুন", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExitToMenu,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("exit_menu_button")
                        ) {
                            Icon(Icons.Filled.Home, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("মেনুতে ফিরুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 7. Game Over / Mission Victory Summary Overlay
        runSummary?.let { summary ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB8000000)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(3.dp, if (summary.isVictory) CoinGold else RickshawCrimson),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .widthIn(max = 480.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = if (summary.isVictory) CoinGold else RickshawCrimson,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = when {
                                summary.isVictory -> "দারুণ! লক্ষ্য পূরণ!"
                                summary.mode == GameMode.ENDLESS -> "রাইড সম্পন্ন!"
                                else -> "আবার চেষ্টা করুন!"
                            },
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (summary.isVictory) BengalEmerald else RickshawCrimson,
                            textAlign = TextAlign.Center
                        )
                        summary.missionTitleBn?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatRow("অতিক্রান্ত দূরত্ব", "${summary.distanceMeters} মিটার")
                                StatRow("মোট স্কোর", "${summary.score}")
                                StatRow("যাত্রী পৌঁছেছেন", "${summary.passengersDelivered} জন")
                                StatRow("সংগৃহীত কয়েন", "+৳ ${summary.coinsEarned}")
                                if (summary.bonusCoins > 0) {
                                    StatRow("মিশন বোনাস", "+৳ ${summary.bonusCoins}")
                                }
                            }
                        }

                        Text(
                            text = "“${summary.closingQuoteBn}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = { runSeed++ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("play_again_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BengalEmerald)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("আবার খেলুন", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExitToMenu,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("summary_menu_button")
                        ) {
                            Icon(Icons.Filled.Home, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("প্রধান মেনু", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionOrPassengerBanner(
    engine: GameEngine,
    launchConfig: ActiveLaunchConfig
) {
    val mission = launchConfig.mission
    val challenge = launchConfig.challenge

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (engine.hasPassenger) Color(0xDD047857) else Color(0xCC1E293B),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Text(
                text = when {
                    engine.passengerTransition?.phase == TransitionPhase.BOARDING ->
                        "${engine.passengerTransition?.persona?.displayTitleBn ?: "যাত্রী"} রিকশায় উঠছে..."
                    engine.passengerTransition?.phase == TransitionPhase.ALIGHTING ->
                        "যাত্রী নামছে! ভাড়া পাওয়া গেছে!"
                    engine.hasPassenger ->
                        "${engine.currentPassengerPersona.displayTitleBn} আছে • হলুদ স্টপে নামান"
                    else ->
                        "সবুজ স্টপে যাত্রী তুলুন • পৌঁছেছেন: ${engine.passengersDelivered}"
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        if (mission != null) {
            Spacer(Modifier.width(8.dp))
            val progressFrac = when (mission.goalType) {
                MissionGoalType.DISTANCE ->
                    engine.distanceMeters.toFloat() / mission.targetDistanceMeters.coerceAtLeast(1)
                MissionGoalType.PASSENGERS ->
                    ((engine.passengersDelivered.toFloat() / mission.targetPassengers.coerceAtLeast(1)) +
                        (engine.distanceMeters.toFloat() / mission.targetDistanceMeters.coerceAtLeast(1))) / 2f
                MissionGoalType.COINS ->
                    ((engine.coinsCollected.toFloat() / mission.targetCoins.coerceAtLeast(1)) +
                        (engine.distanceMeters.toFloat() / mission.targetDistanceMeters.coerceAtLeast(1))) / 2f
                MissionGoalType.COMBO_DELIVERY_AND_DISTANCE ->
                    ((engine.passengersDelivered.toFloat() / mission.targetPassengers.coerceAtLeast(1)) +
                        (engine.distanceMeters.toFloat() / mission.targetDistanceMeters.coerceAtLeast(1))) / 2f
            }.coerceIn(0f, 1f)

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xD90F172A),
                border = BorderStroke(1.dp, MarigoldGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
                    Text(
                        text = "মিশন ${(progressFrac * 100).toInt()}%",
                        color = MarigoldGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { progressFrac },
                        modifier = Modifier
                            .width(88.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MarigoldGold,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
        } else if (challenge != null) {
            Spacer(Modifier.width(8.dp))
            val remaining = (challenge.timeLimitSeconds - engine.elapsedSeconds.toInt()).coerceAtLeast(0)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (remaining <= 15) AlertRed else Color(0xD90F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "সময়: ${remaining}সে • ${engine.distanceMeters}/${challenge.targetDistanceMeters}মি",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ArcadeKeyButton(
    icon: ImageVector,
    labelBn: String,
    containerColor: Color,
    activeColor: Color,
    borderColor: Color,
    contentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onPressedChange: (Boolean) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "keyScale"
    )
    val shape = RoundedCornerShape(18.dp)

    Surface(
        shape = shape,
        color = if (pressed) activeColor else containerColor,
        border = BorderStroke(if (pressed) 2.5.dp else 1.6.dp, if (pressed) MarigoldGold else borderColor),
        shadowElevation = if (pressed) 1.dp else 5.dp,
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .testTag(testTag)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        onPressedChange(true)
                        try {
                            tryAwaitRelease()
                        } finally {
                            pressed = false
                            onPressedChange(false)
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = labelBn,
                tint = contentColor,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = labelBn,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = RickshawCrimson
        )
    }
}
