package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BengaliDialogues
import com.example.model.GameSettings
import com.example.model.PlayerProgress
import com.example.model.SteeringMode
import com.example.model.VoiceRole
import com.example.model.WeatherPreference
import com.example.ui.components.DhakaScreenTopBar
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BengalEmerald
import com.example.ui.theme.MarigoldGold
import com.example.ui.theme.RickshawCrimson

@Composable
fun SettingsScreen(
    progress: PlayerProgress,
    settings: GameSettings,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleVoice: () -> Unit,
    voiceSummaryBn: String,
    onTestVoiceRole: (VoiceRole) -> Unit,
    onSelectWeatherPref: (WeatherPreference) -> Unit,
    onSelectSteeringMode: (SteeringMode) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        DhakaScreenTopBar(
            titleBn = "সেটিংস",
            coins = progress.coins,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsToggleCard(
                title = "গেমের শব্দ ও শহরের পরিবেশ",
                subtitle = "ঘণ্টা, কয়েন, দূরবর্তী হর্ন, রাস্তার কোলাহল ও চায়ের দোকানের শব্দ",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                checked = settings.soundEnabled,
                testTag = "setting_sound_switch",
                onCheckedChange = { onToggleSound() }
            )

            SettingsToggleCard(
                title = "ব্যাকগ্রাউন্ড মিউজিক",
                subtitle = "হালকা দেশি সুরের আবহ সংগীত",
                icon = Icons.Filled.MusicNote,
                checked = settings.musicEnabled,
                testTag = "setting_music_switch",
                onCheckedChange = { onToggleMusic() }
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = RickshawCrimson)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("বাংলা কণ্ঠস্বর (TTS ভয়েস)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "রিকশাওয়ালা ও যাত্রীর মজার বাংলা সংলাপ শুনাবে",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.voiceEnabled,
                            onCheckedChange = { onToggleVoice() },
                            modifier = Modifier.testTag("setting_voice_switch")
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = voiceSummaryBn,
                        style = MaterialTheme.typography.labelSmall,
                        color = BengalEmerald
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTestVoiceRole(VoiceRole.DRIVER_MALE) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_male_button")
                        ) {
                            Text("পুরুষ")
                        }
                        OutlinedButton(
                            onClick = { onTestVoiceRole(VoiceRole.PASSENGER_FEMALE) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_female_button")
                        ) {
                            Text("মহিলা")
                        }
                        OutlinedButton(
                            onClick = { onTestVoiceRole(VoiceRole.PASSENGER_CHILD) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_child_button")
                        ) {
                            Text("শিশু")
                        }
                    }
                }
            }

            // Weather Preference
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "অসীম রাইডের আবহাওয়া",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        WeatherPreference.entries.forEach { pref ->
                            FilterChip(
                                selected = settings.weatherPreference == pref,
                                onClick = { onSelectWeatherPref(pref) },
                                label = { Text(pref.titleBn) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Steering Mode
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "স্টিয়ারিং নিয়ন্ত্রণ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SteeringMode.entries.forEach { mode ->
                            FilterChip(
                                selected = settings.steeringMode == mode,
                                onClick = { onSelectSteeringMode(mode) },
                                label = { Text(mode.titleBn) }
                            )
                        }
                    }
                }
            }

            // Game Info Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ঢাকা রিকশা (Dhaka Rickshaw)", fontWeight = FontWeight.Bold)
                    Text(
                        text = "মোট মজার বাংলা সংলাপ: ${BengaliDialogues.allLines.size}টি • সম্পূর্ণ অফলাইন গেম",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Reset progress
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reset_progress_button")
            ) {
                Icon(Icons.Filled.DeleteForever, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("গেম প্রোগ্রেস রিসেট করুন", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("প্রোগ্রেস মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
            text = { Text("আপনার সব কয়েন, আনলক করা রিকশা ও মিশন রেকর্ড মুছে যাবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetProgress()
                    }
                ) {
                    Text("হ্যাঁ, রিসেট করুন", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল", color = BengalEmerald)
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, MarigoldGold.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = BengalEmerald)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
