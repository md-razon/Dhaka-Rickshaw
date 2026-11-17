package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
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
import com.example.data.MissionCatalog
import com.example.game.RickshawCanvasRenderer.drawShowcaseCycleRickshaw
import com.example.model.PlayerProgress
import com.example.ui.components.DhakaScreenTopBar
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.InkCharcoal
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson
import com.example.ui.theme.RoyalIndigo

@Composable
fun GarageScreen(
    progress: PlayerProgress,
    onSelectOrBuyDesign: (String, Int) -> Unit,
    onSelectOrBuyCanopy: (String, Int) -> Unit,
    onSelectOrBuyDecoration: (String, Int) -> Unit,
    onUpgradeStat: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var selectedTab by remember { mutableIntStateOf(0) }
    var animPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) {
                    val dt = ((now - last) / 1_000_000_000f).coerceAtMost(0.05f)
                    animPhase += dt * 4.2f
                }
                last = now
            }
        }
    }

    val design = MissionCatalog.designById(progress.equippedDesignId)
    val canopy = MissionCatalog.canopyById(progress.equippedCanopyId)
    val decoration = MissionCatalog.decorationById(progress.equippedDecorationId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        DhakaScreenTopBar(
            titleBn = "রিকশা গ্যারেজ",
            coins = progress.coins,
            onBack = onBack
        )

        // Live Interactive 2D Cycle Rickshaw Showcase
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.5.dp, MarigoldGold),
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(190.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFE0B2), Color(0xFFFFF8E1), Color(0xFFD7CCC8))
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawShowcaseCycleRickshaw(
                        design = design,
                        canopy = canopy,
                        decoration = decoration,
                        animPhase = animPhase
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xCC1E1B18),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "${design.nameBn} • ${canopy.nameBn}",
                        color = MarigoldGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("রঙ ও ডিজাইন", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("garage_tab_designs")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("হুড ও সাজ", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("garage_tab_canopy")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("আপগ্রেড", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("garage_tab_upgrades")
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(MissionCatalog.rickshawDesigns, key = { it.id }) { item ->
                        val unlocked = item.id in progress.unlockedDesigns
                        val equipped = item.id == progress.equippedDesignId
                        GarageItemCard(
                            title = item.nameBn,
                            subtitle = item.subtitleBn,
                            swatchPrimary = item.bodyPrimary,
                            swatchSecondary = item.bodySecondary,
                            price = item.price,
                            unlocked = unlocked,
                            equipped = equipped,
                            testTag = "design_${item.id}",
                            onAction = { onSelectOrBuyDesign(item.id, item.price) }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "ক্যানোপি / হুড স্টাইল",
                            style = MaterialTheme.typography.titleMedium,
                            color = RickshawCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(MissionCatalog.canopyStyles, key = { it.id }) { c ->
                        val unlocked = c.id in progress.unlockedCanopies
                        val equipped = c.id == progress.equippedCanopyId
                        GarageItemCard(
                            title = c.nameBn,
                            subtitle = c.descriptionBn,
                            swatchPrimary = design.canopyColor,
                            swatchSecondary = design.trimGold,
                            price = c.price,
                            unlocked = unlocked,
                            equipped = equipped,
                            testTag = "canopy_${c.id}",
                            onAction = { onSelectOrBuyCanopy(c.id, c.price) }
                        )
                    }
                    item {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "রিকশা সাজসজ্জা",
                            style = MaterialTheme.typography.titleMedium,
                            color = BengalEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(MissionCatalog.decorationStyles, key = { it.id }) { d ->
                        val unlocked = d.id in progress.unlockedDecorations
                        val equipped = d.id == progress.equippedDecorationId
                        GarageItemCard(
                            title = d.nameBn,
                            subtitle = d.descriptionBn,
                            swatchPrimary = MarigoldGold,
                            swatchSecondary = RickshawCrimson,
                            price = d.price,
                            unlocked = unlocked,
                            equipped = equipped,
                            testTag = "decor_${d.id}",
                            onAction = { onSelectOrBuyDecoration(d.id, d.price) }
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        UpgradeStatCard(
                            title = "প্যাডেল গতি (Speed)",
                            description = "সর্বোচ্চ গতি এবং প্যাডেলিং শক্তি বাড়ায়।",
                            level = progress.speedLevel,
                            icon = Icons.Filled.Speed,
                            accent = BengalEmerald,
                            testTag = "upgrade_speed_button",
                            onUpgrade = { onUpgradeStat("speed") }
                        )
                    }
                    item {
                        UpgradeStatCard(
                            title = "ব্রেক ক্ষমতা (Brake)",
                            description = "দ্রুত ও নিরাপদে যাত্রী স্টপে থামার ক্ষমতা বাড়ায়।",
                            level = progress.brakeLevel,
                            icon = Icons.Filled.StopCircle,
                            accent = RickshawCrimson,
                            testTag = "upgrade_brake_button",
                            onUpgrade = { onUpgradeStat("brake") }
                        )
                    }
                    item {
                        UpgradeStatCard(
                            title = "হ্যান্ডলিং (Handling)",
                            description = "ব্যস্ত ট্রাফিকে বাম-ডান সাইড কাটানোর নিয়ন্ত্রণ বাড়ায়।",
                            level = progress.handlingLevel,
                            icon = Icons.Filled.DirectionsBike,
                            accent = RoyalIndigo,
                            testTag = "upgrade_handling_button",
                            onUpgrade = { onUpgradeStat("handling") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GarageItemCard(
    title: String,
    subtitle: String,
    swatchPrimary: Color,
    swatchSecondary: Color,
    price: Int,
    unlocked: Boolean,
    equipped: Boolean,
    testTag: String,
    onAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, if (equipped) BengalEmerald else MarigoldGold.copy(alpha = 0.45f)),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(swatchPrimary)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(swatchSecondary)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onAction,
                enabled = !equipped,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        equipped -> Color.Gray
                        unlocked -> BengalEmerald
                        else -> MarigoldGold
                    },
                    contentColor = if (!unlocked && !equipped) InkCharcoal else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                if (equipped) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("সক্রিয়", fontWeight = FontWeight.Bold)
                } else if (unlocked) {
                    Text("ব্যবহার", fontWeight = FontWeight.Bold)
                } else {
                    Text("৳$price", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun UpgradeStatCard(
    title: String,
    description: String,
    level: Int,
    icon: ImageVector,
    accent: Color,
    testTag: String,
    onUpgrade: () -> Unit
) {
    val isMax = level >= 5
    val cost = MissionCatalog.upgradeCost(level)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, accent.copy(alpha = 0.6f)),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("লেভেল $level / ৫", fontWeight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { level / 5f },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = accent
                    )
                }
                Button(
                    onClick = onUpgrade,
                    enabled = !isMax,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMax) Color.Gray else MarigoldGold,
                        contentColor = InkCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag(testTag)
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (isMax) "সর্বোচ্চ" else "আপগ্রেড ৳$cost", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
