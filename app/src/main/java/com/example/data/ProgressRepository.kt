package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.GameSettings
import com.example.model.PlayerProgress
import com.example.model.SteeringMode
import com.example.model.WeatherPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dhaka_rickshaw_prefs")

class ProgressRepository(private val context: Context) {

    private object Keys {
        val COINS = intPreferencesKey("coins")
        val BEST_DISTANCE = intPreferencesKey("best_distance")
        val BEST_SCORE = intPreferencesKey("best_score")
        val TOTAL_PASSENGERS = intPreferencesKey("total_passengers")
        val UNLOCKED_DESIGNS = stringSetPreferencesKey("unlocked_designs")
        val UNLOCKED_CANOPIES = stringSetPreferencesKey("unlocked_canopies")
        val UNLOCKED_DECORATIONS = stringSetPreferencesKey("unlocked_decorations")
        val EQUIPPED_DESIGN = stringPreferencesKey("equipped_design")
        val EQUIPPED_CANOPY = stringPreferencesKey("equipped_canopy")
        val EQUIPPED_DECORATION = stringPreferencesKey("equipped_decoration")
        val SPEED_LEVEL = intPreferencesKey("speed_level")
        val BRAKE_LEVEL = intPreferencesKey("brake_level")
        val HANDLING_LEVEL = intPreferencesKey("handling_level")
        val COMPLETED_MISSIONS = stringSetPreferencesKey("completed_missions")
        val COMPLETED_CHALLENGES = stringSetPreferencesKey("completed_challenges")
        val LAST_BONUS_DAY = stringPreferencesKey("last_bonus_day")

        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val WEATHER_PREF = stringPreferencesKey("weather_pref")
        val STEERING_MODE = stringPreferencesKey("steering_mode")
    }

    val progressFlow: Flow<PlayerProgress> = context.dataStore.data.map { prefs ->
        PlayerProgress(
            coins = prefs[Keys.COINS] ?: 150,
            bestDistanceMeters = prefs[Keys.BEST_DISTANCE] ?: 0,
            bestScore = prefs[Keys.BEST_SCORE] ?: 0,
            totalPassengersDelivered = prefs[Keys.TOTAL_PASSENGERS] ?: 0,
            unlockedDesigns = prefs[Keys.UNLOCKED_DESIGNS] ?: setOf("lalbagh_classic"),
            unlockedCanopies = prefs[Keys.UNLOCKED_CANOPIES] ?: setOf("classic_fold"),
            unlockedDecorations = prefs[Keys.UNLOCKED_DECORATIONS] ?: setOf("none"),
            equippedDesignId = prefs[Keys.EQUIPPED_DESIGN] ?: "lalbagh_classic",
            equippedCanopyId = prefs[Keys.EQUIPPED_CANOPY] ?: "classic_fold",
            equippedDecorationId = prefs[Keys.EQUIPPED_DECORATION] ?: "none",
            speedLevel = prefs[Keys.SPEED_LEVEL] ?: 1,
            brakeLevel = prefs[Keys.BRAKE_LEVEL] ?: 1,
            handlingLevel = prefs[Keys.HANDLING_LEVEL] ?: 1,
            completedMissions = prefs[Keys.COMPLETED_MISSIONS] ?: emptySet(),
            completedChallenges = prefs[Keys.COMPLETED_CHALLENGES] ?: emptySet(),
            lastBonusDayKey = prefs[Keys.LAST_BONUS_DAY] ?: ""
        )
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        GameSettings(
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
            voiceEnabled = prefs[Keys.VOICE_ENABLED] ?: true,
            weatherPreference = prefs[Keys.WEATHER_PREF]?.let {
                runCatching { WeatherPreference.valueOf(it) }.getOrNull()
            } ?: WeatherPreference.DYNAMIC,
            steeringMode = prefs[Keys.STEERING_MODE]?.let {
                runCatching { SteeringMode.valueOf(it) }.getOrNull()
            } ?: SteeringMode.BOTH
        )
    }

    suspend fun recordRunResult(
        coinsEarned: Int,
        distanceMeters: Int,
        score: Int,
        passengersDelivered: Int,
        completedMissionId: String?,
        completedChallengeId: String?
    ) {
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[Keys.COINS] ?: 150
            prefs[Keys.COINS] = currentCoins + coinsEarned

            val prevBestDist = prefs[Keys.BEST_DISTANCE] ?: 0
            if (distanceMeters > prevBestDist) {
                prefs[Keys.BEST_DISTANCE] = distanceMeters
            }
            val prevBestScore = prefs[Keys.BEST_SCORE] ?: 0
            if (score > prevBestScore) {
                prefs[Keys.BEST_SCORE] = score
            }
            val prevPassengers = prefs[Keys.TOTAL_PASSENGERS] ?: 0
            prefs[Keys.TOTAL_PASSENGERS] = prevPassengers + passengersDelivered

            if (completedMissionId != null) {
                val missions = prefs[Keys.COMPLETED_MISSIONS] ?: emptySet()
                prefs[Keys.COMPLETED_MISSIONS] = missions + completedMissionId
            }
            if (completedChallengeId != null) {
                val challenges = prefs[Keys.COMPLETED_CHALLENGES] ?: emptySet()
                prefs[Keys.COMPLETED_CHALLENGES] = challenges + completedChallengeId
            }
        }
    }

    suspend fun unlockAndEquipDesign(id: String, price: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val unlocked = prefs[Keys.UNLOCKED_DESIGNS] ?: setOf("lalbagh_classic")
            if (id in unlocked) {
                prefs[Keys.EQUIPPED_DESIGN] = id
                success = true
            } else {
                val coins = prefs[Keys.COINS] ?: 150
                if (coins >= price) {
                    prefs[Keys.COINS] = coins - price
                    prefs[Keys.UNLOCKED_DESIGNS] = unlocked + id
                    prefs[Keys.EQUIPPED_DESIGN] = id
                    success = true
                }
            }
        }
        return success
    }

    suspend fun unlockAndEquipCanopy(id: String, price: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val unlocked = prefs[Keys.UNLOCKED_CANOPIES] ?: setOf("classic_fold")
            if (id in unlocked) {
                prefs[Keys.EQUIPPED_CANOPY] = id
                success = true
            } else {
                val coins = prefs[Keys.COINS] ?: 150
                if (coins >= price) {
                    prefs[Keys.COINS] = coins - price
                    prefs[Keys.UNLOCKED_CANOPIES] = unlocked + id
                    prefs[Keys.EQUIPPED_CANOPY] = id
                    success = true
                }
            }
        }
        return success
    }

    suspend fun unlockAndEquipDecoration(id: String, price: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val unlocked = prefs[Keys.UNLOCKED_DECORATIONS] ?: setOf("none")
            if (id in unlocked) {
                prefs[Keys.EQUIPPED_DECORATION] = id
                success = true
            } else {
                val coins = prefs[Keys.COINS] ?: 150
                if (coins >= price) {
                    prefs[Keys.COINS] = coins - price
                    prefs[Keys.UNLOCKED_DECORATIONS] = unlocked + id
                    prefs[Keys.EQUIPPED_DECORATION] = id
                    success = true
                }
            }
        }
        return success
    }

    suspend fun upgradeStat(statType: String): Boolean {
        var upgraded = false
        context.dataStore.edit { prefs ->
            val key = when (statType) {
                "speed" -> Keys.SPEED_LEVEL
                "brake" -> Keys.BRAKE_LEVEL
                else -> Keys.HANDLING_LEVEL
            }
            val level = prefs[key] ?: 1
            if (level < 5) {
                val cost = MissionCatalog.upgradeCost(level)
                val coins = prefs[Keys.COINS] ?: 150
                if (coins >= cost) {
                    prefs[Keys.COINS] = coins - cost
                    prefs[key] = level + 1
                    upgraded = true
                }
            }
        }
        return upgraded
    }

    suspend fun claimDailyBonus(todayKey: String, amount: Int = 100): Boolean {
        var claimed = false
        context.dataStore.edit { prefs ->
            val last = prefs[Keys.LAST_BONUS_DAY] ?: ""
            if (last != todayKey) {
                val coins = prefs[Keys.COINS] ?: 150
                prefs[Keys.COINS] = coins + amount
                prefs[Keys.LAST_BONUS_DAY] = todayKey
                claimed = true
            }
        }
        return claimed
    }

    suspend fun updateSettings(transform: (GameSettings) -> GameSettings) {
        context.dataStore.edit { prefs ->
            val current = GameSettings(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
                voiceEnabled = prefs[Keys.VOICE_ENABLED] ?: true,
                weatherPreference = prefs[Keys.WEATHER_PREF]?.let {
                    runCatching { WeatherPreference.valueOf(it) }.getOrNull()
                } ?: WeatherPreference.DYNAMIC,
                steeringMode = prefs[Keys.STEERING_MODE]?.let {
                    runCatching { SteeringMode.valueOf(it) }.getOrNull()
                } ?: SteeringMode.BOTH
            )
            val updated = transform(current)
            prefs[Keys.SOUND_ENABLED] = updated.soundEnabled
            prefs[Keys.MUSIC_ENABLED] = updated.musicEnabled
            prefs[Keys.VOICE_ENABLED] = updated.voiceEnabled
            prefs[Keys.WEATHER_PREF] = updated.weatherPreference.name
            prefs[Keys.STEERING_MODE] = updated.steeringMode.name
        }
    }

    suspend fun resetAllProgress() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
