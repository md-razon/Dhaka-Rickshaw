package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MissionCatalog
import com.example.model.ChallengeConfig
import com.example.model.Mission
import com.example.model.PlayerProgress
import com.example.ui.components.DhakaScreenTopBar
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.CoinGold
import com.example.ui.theme.InkCharcoal
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson
import com.example.ui.theme.RoyalIndigo

@Composable
fun MissionsScreen(
    progress: PlayerProgress,
    onStartMission: (Mission) -> Unit,
    onStartChallenge: (ChallengeConfig) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        DhakaScreenTopBar(
            titleBn = "মিশন ও চ্যালেঞ্জ",
            coins = progress.coins,
            onBack = onBack
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("মিশন মোড (৮)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_missions")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("চ্যালেঞ্জ মোড (৪)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_challenges")
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(MissionCatalog.missions, key = { it.id }) { mission ->
                    val completed = mission.id in progress.completedMissions
                    MissionCard(
                        mission = mission,
                        completed = completed,
                        onPlay = { onStartMission(mission) }
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(MissionCatalog.challenges, key = { it.id }) { ch ->
                    val completed = ch.id in progress.completedChallenges
                    ChallengeCard(
                        challenge = ch,
                        completed = completed,
                        onPlay = { onStartChallenge(ch) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: Mission,
    completed: Boolean,
    onPlay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            2.dp,
            if (completed) BengalEmerald else MarigoldGold.copy(alpha = 0.7f)
        ),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RickshawCrimson
                    ) {
                        Text(
                            text = "মিশন ${mission.number}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = mission.routeBn,
                        style = MaterialTheme.typography.labelLarge,
                        color = BengalEmerald
                    )
                }
                if (completed) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "সম্পন্ন",
                        tint = BengalEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = mission.titleBn,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = mission.descriptionBn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = MarigoldGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(mission.weather.titleBn, fontSize = 12.sp)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = CoinGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "পুরস্কার: +৳${mission.rewardCoins}",
                            color = Color(0xFFB45309),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = BengalEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("play_mission_${mission.number}")
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("চালান", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: ChallengeConfig,
    completed: Boolean,
    onPlay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, if (completed) BengalEmerald else RoyalIndigo),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = RoyalIndigo)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = challenge.titleBn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = RoyalIndigo
                    )
                }
                if (completed) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "জয়ী", tint = BengalEmerald)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(challenge.subtitleBn, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = challenge.ruleBn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বোনাস: +৳${challenge.rewardCoins}",
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = MarigoldGold, contentColor = InkCharcoal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("play_challenge_${challenge.id}")
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("চ্যালেঞ্জ শুরু", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
