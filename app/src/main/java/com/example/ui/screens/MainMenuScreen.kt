package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppScreen
import com.example.model.GameMode
import com.example.model.PlayerProgress
import com.example.ui.components.CoinBadge
import com.example.ui.components.FolkArtMenuButton
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.InkCharcoal
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson
import com.example.ui.theme.RoyalIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    progress: PlayerProgress,
    onStartEndless: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var showModePicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Illustrated Dhaka street backdrop
        Image(
            painter = painterResource(id = R.drawable.img_dhaka_menu_bg),
            contentDescription = "ঢাকার রাস্তার দৃশ্য",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Rich atmospheric gradient scrim for crisp readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xB80E1628),
                            Color(0x660E1628),
                            Color(0xCC0E1628)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .widthIn(max = 520.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: Best record & Coins
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xBB164936),
                    border = BorderStroke(1.5.dp, MarigoldGold)
                ) {
                    Text(
                        text = "সেরা: ${progress.bestDistanceMeters} মি.",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                CoinBadge(coins = progress.coins)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Game Logo Card inspired by colorful Bangladeshi rickshaw plates
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = RickshawCrimson.copy(alpha = 0.92f),
                border = BorderStroke(3.dp, MarigoldGold),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsBike,
                            contentDescription = null,
                            tint = MarigoldGold,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ঢাকা রিকশা",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = "DHAKA RICKSHAW 3D • ৩ডি রিকশা রাইড",
                        color = Color(0xFFFFE082),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Menu Buttons (All 5 required menu items)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FolkArtMenuButton(
                    textBn = "খেলুন",
                    icon = Icons.Filled.PlayArrow,
                    containerColor = BengalEmerald,
                    testTag = "menu_play_button",
                    onClick = { showModePicker = true }
                )
                FolkArtMenuButton(
                    textBn = "মিশন (${progress.completedMissions.size}/৮)",
                    icon = Icons.Filled.Flag,
                    containerColor = RickshawCrimson,
                    testTag = "menu_missions_button",
                    onClick = { onNavigate(AppScreen.MISSIONS) }
                )
                FolkArtMenuButton(
                    textBn = "গ্যারেজ",
                    icon = Icons.Filled.Build,
                    containerColor = RoyalIndigo,
                    testTag = "menu_garage_button",
                    onClick = { onNavigate(AppScreen.GARAGE) }
                )
                FolkArtMenuButton(
                    textBn = "দোকান",
                    icon = Icons.Filled.ShoppingBag,
                    containerColor = Color(0xFFE65100),
                    testTag = "menu_shop_button",
                    onClick = { onNavigate(AppScreen.SHOP) }
                )
                FolkArtMenuButton(
                    textBn = "সেটিংস",
                    icon = Icons.Filled.Settings,
                    containerColor = Color(0xFF334155),
                    testTag = "menu_settings_button",
                    onClick = { onNavigate(AppScreen.SETTINGS) }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Footer quick stats
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xAA0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "মোট যাত্রী: ${progress.totalPassengersDelivered} জন",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "সেরা স্কোর: ${progress.bestScore}",
                        color = MarigoldGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showModePicker) {
        ModalBottomSheet(
            onDismissRequest = { showModePicker = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "গেম মোড বেছে নিন",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = RickshawCrimson
                )

                ModeChoiceCard(
                    title = GameMode.ENDLESS.titleBn,
                    subtitle = GameMode.ENDLESS.subtitleBn,
                    accent = BengalEmerald,
                    icon = Icons.Filled.DirectionsBike,
                    testTag = "mode_endless_button",
                    onClick = {
                        showModePicker = false
                        onStartEndless()
                    }
                )
                ModeChoiceCard(
                    title = GameMode.MISSION.titleBn,
                    subtitle = GameMode.MISSION.subtitleBn,
                    accent = RickshawCrimson,
                    icon = Icons.Filled.EmojiEvents,
                    testTag = "mode_mission_button",
                    onClick = {
                        showModePicker = false
                        onNavigate(AppScreen.MISSIONS)
                    }
                )
                ModeChoiceCard(
                    title = GameMode.CHALLENGE.titleBn,
                    subtitle = GameMode.CHALLENGE.subtitleBn,
                    accent = RoyalIndigo,
                    icon = Icons.Filled.Speed,
                    testTag = "mode_challenge_button",
                    onClick = {
                        showModePicker = false
                        onNavigate(AppScreen.MISSIONS)
                    }
                )
            }
        }
    }
}

@Composable
private fun ModeChoiceCard(
    title: String,
    subtitle: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(2.dp, accent),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accent,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MarigoldGold, contentColor = InkCharcoal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("শুরু", fontWeight = FontWeight.Bold)
            }
        }
    }
}
