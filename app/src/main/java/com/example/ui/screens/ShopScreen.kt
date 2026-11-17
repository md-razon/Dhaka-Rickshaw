package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.MissionCatalog
import com.example.model.PlayerProgress
import com.example.ui.components.DhakaScreenTopBar
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.InkCharcoal
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShopScreen(
    progress: PlayerProgress,
    onBuyDesign: (String, Int) -> Unit,
    onBuyCanopy: (String, Int) -> Unit,
    onBuyDecoration: (String, Int) -> Unit,
    onClaimDailyBonus: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val bonusClaimedToday = progress.lastBonusDayKey == todayKey

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        DhakaScreenTopBar(
            titleBn = "রিকশা দোকান",
            coins = progress.coins,
            onBack = onBack
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Daily free offline bonus
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = BengalEmerald,
                    border = BorderStroke(2.5.dp, MarigoldGold),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "দৈনিক বোনাস",
                            tint = MarigoldGold,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "দৈনিক চা-নাস্তা বোনাস",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রতিদিন ফ্রি +৳১০০ কয়েন সংগ্রহ করুন!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFD1FAE5)
                            )
                        }
                        Button(
                            onClick = onClaimDailyBonus,
                            enabled = !bonusClaimedToday,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MarigoldGold,
                                contentColor = InkCharcoal
                            ),
                            modifier = Modifier.testTag("claim_daily_bonus_button")
                        ) {
                            Text(
                                text = if (bonusClaimedToday) "নেওয়া হয়েছে" else "নিন +৳১০০",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "নতুন রিকশা মডেল",
                    style = MaterialTheme.typography.titleLarge,
                    color = RickshawCrimson,
                    fontWeight = FontWeight.Bold
                )
            }

            items(MissionCatalog.rickshawDesigns.filter { it.price > 0 }, key = { "shop_d_${it.id}" }) { d ->
                val unlocked = d.id in progress.unlockedDesigns
                ShopItemRow(
                    title = d.nameBn,
                    description = d.subtitleBn,
                    colorDot = d.bodyPrimary,
                    price = d.price,
                    unlocked = unlocked,
                    testTag = "shop_design_${d.id}",
                    onBuy = { onBuyDesign(d.id, d.price) }
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "বিশেষ হুড ও সাজসজ্জা",
                    style = MaterialTheme.typography.titleLarge,
                    color = BengalEmerald,
                    fontWeight = FontWeight.Bold
                )
            }

            items(MissionCatalog.canopyStyles.filter { it.price > 0 }, key = { "shop_c_${it.id}" }) { c ->
                val unlocked = c.id in progress.unlockedCanopies
                ShopItemRow(
                    title = c.nameBn,
                    description = c.descriptionBn,
                    colorDot = MarigoldGold,
                    price = c.price,
                    unlocked = unlocked,
                    testTag = "shop_canopy_${c.id}",
                    onBuy = { onBuyCanopy(c.id, c.price) }
                )
            }

            items(MissionCatalog.decorationStyles.filter { it.price > 0 }, key = { "shop_dec_${it.id}" }) { dec ->
                val unlocked = dec.id in progress.unlockedDecorations
                ShopItemRow(
                    title = dec.nameBn,
                    description = dec.descriptionBn,
                    colorDot = RickshawCrimson,
                    price = dec.price,
                    unlocked = unlocked,
                    testTag = "shop_decor_${dec.id}",
                    onBuy = { onBuyDecoration(dec.id, dec.price) }
                )
            }
        }
    }
}

@Composable
private fun ShopItemRow(
    title: String,
    description: String,
    colorDot: Color,
    price: Int,
    unlocked: Boolean,
    testTag: String,
    onBuy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, if (unlocked) BengalEmerald else MarigoldGold.copy(alpha = 0.5f)),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colorDot)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (unlocked) BengalEmerald else MarigoldGold,
                    contentColor = if (unlocked) Color.White else InkCharcoal
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                Icon(
                    imageVector = if (unlocked) Icons.Filled.Check else Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(if (unlocked) "কেনা আছে" else "৳$price", fontWeight = FontWeight.Bold)
            }
        }
    }
}
