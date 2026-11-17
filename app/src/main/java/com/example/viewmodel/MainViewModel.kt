package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameAudioManager
import com.example.data.ProgressRepository
import com.example.model.ActiveLaunchConfig
import com.example.model.AppScreen
import com.example.model.ChallengeConfig
import com.example.model.GameMode
import com.example.model.GameSettings
import com.example.model.Mission
import com.example.model.PlayerProgress
import com.example.model.RunSummary
import com.example.model.SteeringMode
import com.example.model.VoiceRole
import com.example.model.WeatherPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProgressRepository(application)
    val audioManager = GameAudioManager(application)

    val progress: StateFlow<PlayerProgress> = repository.progressFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlayerProgress()
    )

    val settings: StateFlow<GameSettings> = repository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GameSettings()
    )

    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _launchConfig = MutableStateFlow(ActiveLaunchConfig())
    val launchConfig: StateFlow<ActiveLaunchConfig> = _launchConfig.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            settings.collect { s ->
                audioManager.soundEnabled = s.soundEnabled
                audioManager.musicEnabled = s.musicEnabled
                audioManager.voiceEnabled = s.voiceEnabled
                if (s.musicEnabled) {
                    audioManager.startAmbientMusic()
                } else {
                    audioManager.stopAmbientMusic()
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun startEndlessRide() {
        _launchConfig.value = ActiveLaunchConfig(mode = GameMode.ENDLESS)
        _currentScreen.value = AppScreen.GAMEPLAY
    }

    fun startMission(mission: Mission) {
        _launchConfig.value = ActiveLaunchConfig(mode = GameMode.MISSION, mission = mission)
        _currentScreen.value = AppScreen.GAMEPLAY
    }

    fun startChallenge(challenge: ChallengeConfig) {
        _launchConfig.value = ActiveLaunchConfig(mode = GameMode.CHALLENGE, challenge = challenge)
        _currentScreen.value = AppScreen.GAMEPLAY
    }

    fun onRunCompleted(summary: RunSummary, completedMissionId: String?, completedChallengeId: String?) {
        viewModelScope.launch {
            repository.recordRunResult(
                coinsEarned = summary.coinsEarned + summary.bonusCoins,
                distanceMeters = summary.distanceMeters,
                score = summary.score,
                passengersDelivered = summary.passengersDelivered,
                completedMissionId = completedMissionId,
                completedChallengeId = completedChallengeId
            )
        }
    }

    fun selectOrBuyDesign(id: String, price: Int) {
        viewModelScope.launch {
            val ok = repository.unlockAndEquipDesign(id, price)
            if (ok) {
                audioManager.playCoinPickup()
                _statusMessage.value = "নতুন রিকশা ডিজাইন সেট করা হয়েছে!"
            } else {
                _statusMessage.value = "পর্যাপ্ত কয়েন নেই! আরও কয়েন সংগ্রহ করুন।"
            }
        }
    }

    fun selectOrBuyCanopy(id: String, price: Int) {
        viewModelScope.launch {
            val ok = repository.unlockAndEquipCanopy(id, price)
            if (ok) {
                audioManager.playCoinPickup()
                _statusMessage.value = "রিকশার হুড পরিবর্তন করা হয়েছে!"
            } else {
                _statusMessage.value = "পর্যাপ্ত কয়েন নেই!"
            }
        }
    }

    fun selectOrBuyDecoration(id: String, price: Int) {
        viewModelScope.launch {
            val ok = repository.unlockAndEquipDecoration(id, price)
            if (ok) {
                audioManager.playCoinPickup()
                _statusMessage.value = "নতুন সাজসজ্জা যুক্ত হয়েছে!"
            } else {
                _statusMessage.value = "পর্যাপ্ত কয়েন নেই!"
            }
        }
    }

    fun upgradeStat(statType: String) {
        viewModelScope.launch {
            val ok = repository.upgradeStat(statType)
            if (ok) {
                audioManager.playVictory()
                _statusMessage.value = "রিকশা আপগ্রেড সফল হয়েছে!"
            } else {
                _statusMessage.value = "আপগ্রেড করতে আরও কয়েন প্রয়োজন!"
            }
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val ok = repository.claimDailyBonus(today, 100)
            if (ok) {
                audioManager.playVictory()
                _statusMessage.value = "আজকের চা-নাস্তা বোনাস +৳১০০ পাওয়া গেছে!"
            } else {
                _statusMessage.value = "আজকের বোনাস আগেই নেওয়া হয়েছে!"
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun toggleSound() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(soundEnabled = !it.soundEnabled) }
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(musicEnabled = !it.musicEnabled) }
        }
    }

    fun toggleVoice() {
        viewModelScope.launch {
            repository.updateSettings {
                val nextVoice = !it.voiceEnabled
                if (!nextVoice) audioManager.stopSpeaking()
                it.copy(voiceEnabled = nextVoice)
            }
        }
    }

    fun setWeatherPreference(pref: WeatherPreference) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(weatherPreference = pref) }
        }
    }

    fun setSteeringMode(mode: SteeringMode) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(steeringMode = mode) }
        }
    }

    fun getVoiceSummaryBn(): String = audioManager.getVoiceSummaryBn()

    fun testVoiceRole(role: VoiceRole) {
        val sample = when (role) {
            VoiceRole.DRIVER_MALE -> "মামা, প্যাডেল আমার পায়ে, রকেট লাগানো নাই!"
            VoiceRole.PASSENGER_MALE -> "ভাই, একটু তাড়াতাড়ি চালান তো, অফিস দেরি হয়ে যাচ্ছে!"
            VoiceRole.PASSENGER_FEMALE -> "মামা, সামনে একটু দেখে চালান, হাতে বাজারের ব্যাগ আছে!"
            VoiceRole.PASSENGER_CHILD -> "রিকশা কাকু! ওই দেখো কত্ত বড় বাস যাচ্ছে, খুব মজা!"
        }
        audioManager.speakBengali(sample, role)
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _statusMessage.value = "সব গেম প্রোগ্রেস রিসেট করা হয়েছে।"
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
