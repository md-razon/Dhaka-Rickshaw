package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.model.VoiceRole
import com.example.model.WeatherType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class GameAudioManager(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile var soundEnabled: Boolean = true
    @Volatile var musicEnabled: Boolean = true
    @Volatile var voiceEnabled: Boolean = true
    @Volatile private var isSpeakingNow: Boolean = false
    @Volatile private var currentAmbientWeather: WeatherType = WeatherType.DAY
    @Volatile private var pendingExternalHornType: Int = 0 // 0=none, 1=car, 2=bus

    private var tts: TextToSpeech? = null
    @Volatile private var ttsReady: Boolean = false
    @Volatile private var bengaliAvailable: Boolean = false
    private var musicJob: Job? = null
    private var cityAmbienceJob: Job? = null

    private var bdMaleVoice: Voice? = null
    private var bdFemaleVoice: Voice? = null
    private var bdChildCandidateVoice: Voice? = null
    private var detectedBnVoiceCount: Int = 0
    private var hasDistinctFemaleVoice: Boolean = false

    init {
        try {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val engine = tts ?: return@TextToSpeech
                    engine.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    val bdLocale = Locale.Builder().setLanguage("bn").setRegion("BD").build()
                    val bdResult = engine.setLanguage(bdLocale)
                    val ok = if (bdResult != TextToSpeech.LANG_MISSING_DATA &&
                        bdResult != TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        true
                    } else {
                        val inLocale = Locale.Builder().setLanguage("bn").setRegion("IN").build()
                        val inResult = engine.setLanguage(inLocale)
                        inResult != TextToSpeech.LANG_MISSING_DATA &&
                            inResult != TextToSpeech.LANG_NOT_SUPPORTED
                    }
                    bengaliAvailable = ok
                    ttsReady = ok
                    if (ok) {
                        discoverBangladeshiVoices(engine)
                        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                isSpeakingNow = true
                            }
                            override fun onDone(utteranceId: String?) {
                                isSpeakingNow = false
                            }
                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                isSpeakingNow = false
                            }
                            override fun onError(utteranceId: String?, errorCode: Int) {
                                isSpeakingNow = false
                            }
                        })
                    }
                }
            }
        } catch (_: Throwable) {
            ttsReady = false
        }
    }

    private fun discoverBangladeshiVoices(engine: TextToSpeech) {
        try {
            val allVoices = engine.voices ?: return
            val bnVoices = allVoices
                .filter { v ->
                    v.locale.language.equals("bn", ignoreCase = true) &&
                        !v.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
                }
                .sortedWith(
                    compareByDescending<Voice> { it.locale.country.equals("BD", ignoreCase = true) }
                        .thenByDescending { it.quality }
                        .thenBy { it.latency }
                )

            detectedBnVoiceCount = bnVoices.size
            if (bnVoices.isEmpty()) return

            fun isFemaleVoice(v: Voice): Boolean {
                val n = v.name.lowercase(Locale.US)
                val genderFeature = v.features.any { it.contains("female", ignoreCase = true) }
                return genderFeature ||
                    n.contains("female") ||
                    n.contains("-ban-") || n.contains("_ban_") ||
                    n.contains("-bnf-") || n.contains("_bnf_") ||
                    n.contains("-bna-") || n.contains("-bnc-") ||
                    n.contains("-f-") || n.contains("_f_") ||
                    n.contains("wavenet-a") || n.contains("wavenet-c") ||
                    n.contains("standard-a") || n.contains("standard-c")
            }

            fun isMaleVoice(v: Voice): Boolean {
                val n = v.name.lowercase(Locale.US)
                val genderFeature = v.features.any {
                    it.contains("male", ignoreCase = true) && !it.contains("female", ignoreCase = true)
                }
                return genderFeature ||
                    (n.contains("male") && !n.contains("female")) ||
                    n.contains("-bdb-") || n.contains("_bdb_") ||
                    n.contains("-bnm-") || n.contains("_bnm_") ||
                    n.contains("-bnd-") ||
                    n.contains("-m-") || n.contains("_m_") ||
                    n.contains("wavenet-b") || n.contains("wavenet-d") ||
                    n.contains("standard-b") || n.contains("standard-d")
            }

            val femaleMatches = bnVoices.filter { isFemaleVoice(it) }
            val maleMatches = bnVoices.filter { isMaleVoice(it) }

            bdFemaleVoice = femaleMatches.firstOrNull()
                ?: bnVoices.firstOrNull { !isMaleVoice(it) }
                ?: bnVoices.firstOrNull()

            bdMaleVoice = maleMatches.firstOrNull { it.name != bdFemaleVoice?.name }
                ?: maleMatches.firstOrNull()
                ?: bnVoices.firstOrNull { it.name != bdFemaleVoice?.name }
                ?: bnVoices.lastOrNull()

            hasDistinctFemaleVoice = bdFemaleVoice != null && bdMaleVoice != null &&
                bdFemaleVoice?.name != bdMaleVoice?.name

            bdChildCandidateVoice = bdFemaleVoice ?: bnVoices.firstOrNull()
        } catch (_: Throwable) {
        }
    }

    fun getVoiceSummaryBn(): String {
        return when {
            detectedBnVoiceCount > 1 && hasDistinctFemaleVoice ->
                "বাংলা ভয়েস: ${detectedBnVoiceCount}টি (আসল পুরুষ ও মহিলা ভয়েস সক্রিয়)"
            detectedBnVoiceCount > 0 ->
                "বাংলা ভয়েস: ${detectedBnVoiceCount}টি (পুরুষ, মহিলা ও শিশু টোন সক্রিয়)"
            else ->
                "ডিফল্ট বাংলা ভয়েস টোন সক্রিয়"
        }
    }

    fun speakBengali(text: String, role: VoiceRole) {
        if (!voiceEnabled || !ttsReady || !bengaliAvailable) return
        try {
            val engine = tts ?: return
            val naturalText = text
                .replace("!", "।")
                .replace("—", ", ")
                .replace("...", ", ")
                .replace("?", "?")

            when (role) {
                VoiceRole.DRIVER_MALE -> {
                    bdMaleVoice?.let { engine.voice = it }
                    engine.setPitch(if (hasDistinctFemaleVoice) 0.96f else 0.91f)
                    engine.setSpeechRate(0.94f)
                }
                VoiceRole.PASSENGER_MALE -> {
                    bdMaleVoice?.let { engine.voice = it }
                    engine.setPitch(0.99f)
                    engine.setSpeechRate(0.96f)
                }
                VoiceRole.PASSENGER_FEMALE -> {
                    bdFemaleVoice?.let { engine.voice = it }
                    engine.setPitch(if (hasDistinctFemaleVoice) 1.02f else 1.13f)
                    engine.setSpeechRate(0.95f)
                }
                VoiceRole.PASSENGER_CHILD -> {
                    bdChildCandidateVoice?.let { engine.voice = it }
                    engine.setPitch(if (hasDistinctFemaleVoice) 1.18f else 1.24f)
                    engine.setSpeechRate(1.01f)
                }
            }

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.95f)
            }
            engine.speak(naturalText, TextToSpeech.QUEUE_FLUSH, params, "dlg_${System.currentTimeMillis()}")
        } catch (_: Throwable) {
            isSpeakingNow = false
        }
    }

    fun speakBengali(text: String, isDriver: Boolean) {
        speakBengali(text, if (isDriver) VoiceRole.DRIVER_MALE else VoiceRole.PASSENGER_FEMALE)
    }

    fun stopSpeaking() {
        isSpeakingNow = false
        try {
            tts?.stop()
        } catch (_: Throwable) {
        }
    }

    fun updateAmbientWeather(weather: WeatherType) {
        currentAmbientWeather = weather
    }

    fun triggerDistantTrafficHorn(isBus: Boolean) {
        if (!soundEnabled) return
        pendingExternalHornType = if (isBus) 2 else 1
    }

    /**
     * Continuous, low-CPU streaming Dhaka city soundscape:
     * - Bustling street crowd murmur (filtered warm brown/pink noise + undulating amplitude)
     * - Tea-stall chatter & ceramic teacup spoon clinks ("টুং-টুং" + warm conversational syllables)
     * - Distant honking (soft bus air-horns, car double-beeps, faraway cycle bells)
     * - Rain drizzle ambiance when WeatherType.RAIN is active
     * - Smoothly ducks volume whenever Bengali TTS dialogue is speaking
     */
    fun startDhakaCityAmbience() {
        if (!soundEnabled) return
        if (cityAmbienceJob?.isActive == true) return

        cityAmbienceJob = scope.launch {
            val sampleRate = 16000
            val chunkSamples = sampleRate / 10 // 100ms smooth streaming chunks
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferBytes = maxOf(minBuf, chunkSamples * 4)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferBytes)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                val pcm = ShortArray(chunkSamples)
                var seed = 987654321L
                var brown = 0.0
                var lpCrowd = 0.0
                var lpRain = 0.0
                var globalSample = 0L
                var duckGain = 1.0

                // Distant horn state
                var hornRemaining = 0.0
                var hornDuration = 0.6
                var hornMode = 0 // 0=bus air-horn, 1=car double-beep, 2=far cycle bell
                var nextHornTimer = 1.8

                // Tea-stall spoon clink & chatter burst state
                var clinkRemaining = 0.0
                var nextClinkTimer = 2.6
                var chatterRemaining = 0.0
                var chatterPitchHz = 165.0
                var nextChatterTimer = 1.2

                val dt = 1.0 / sampleRate

                while (isActive && soundEnabled) {
                    val weather = currentAmbientWeather
                    val targetDuck = if (isSpeakingNow) 0.28 else 1.0

                    for (i in 0 until chunkSamples) {
                        val t = globalSample * dt
                        globalSample++

                        // Smooth ducking transition
                        duckGain += (targetDuck - duckGain) * 0.0025

                        // Pseudo-random white noise [-1, 1]
                        seed = (seed * 6364136223846793005L + 1442695040888963407L)
                        val white = ((seed ushr 32).toInt().toDouble() / Int.MAX_VALUE.toDouble())

                        // Warm street crowd noise bed (leaky integrator + low-pass)
                        brown = (brown + white * 0.045) * 0.992
                        lpCrowd = lpCrowd * 0.96 + brown * 0.04
                        val crowdSwell = 0.65 + 0.35 * sin(2.0 * PI * 0.22 * t) * sin(2.0 * PI * 0.13 * t + 1.1)
                        val crowdLevel = when (weather) {
                            WeatherType.DAY -> 0.15
                            WeatherType.NIGHT -> 0.07
                            WeatherType.RAIN -> 0.09
                        }
                        var mix = lpCrowd * crowdLevel * crowdSwell

                        // Rain drizzle ambience during monsoon weather
                        if (weather == WeatherType.RAIN) {
                            lpRain = lpRain * 0.72 + white * 0.28
                            mix += lpRain * 0.065
                        }

                        // Check for traffic-triggered distant horn
                        val extHorn = pendingExternalHornType
                        if (extHorn != 0) {
                            pendingExternalHornType = 0
                            if (hornRemaining <= 0.0) {
                                hornMode = if (extHorn == 2) 0 else 1
                                hornDuration = if (hornMode == 0) 0.62 else 0.44
                                hornRemaining = hornDuration
                            }
                        }

                        // Schedule periodic distant Dhaka honking
                        nextHornTimer -= dt
                        if (nextHornTimer <= 0.0) {
                            nextHornTimer = when (weather) {
                                WeatherType.NIGHT -> 4.8 + Random.nextDouble() * 4.5
                                else -> 3.0 + Random.nextDouble() * 4.0
                            }
                            if (hornRemaining <= 0.0) {
                                hornMode = Random.nextInt(3)
                                hornDuration = when (hornMode) {
                                    0 -> 0.68 // Distant bus double air-horn
                                    1 -> 0.44 // Car / CNG double beep
                                    else -> 0.36 // Distant rickshaw bell
                                }
                                hornRemaining = hornDuration
                            }
                        }

                        if (hornRemaining > 0.0) {
                            val elapsedH = hornDuration - hornRemaining
                            val hornSample = when (hornMode) {
                                0 -> {
                                    // Bus horn: two brassy notes at 262Hz + 330Hz ("পোঁ-পোঁ")
                                    val pulse = if (elapsedH < 0.24) {
                                        sin(PI * (elapsedH / 0.24))
                                    } else if (elapsedH in 0.30..0.68) {
                                        sin(PI * ((elapsedH - 0.30) / 0.38))
                                    } else 0.0
                                    val tone = sin(2.0 * PI * 262.0 * t) * 0.55 +
                                        sin(2.0 * PI * 330.0 * t) * 0.35 +
                                        sin(2.0 * PI * 524.0 * t) * 0.10
                                    tone * pulse * 0.055
                                }
                                1 -> {
                                    // Car/CNG double-beep ("পিপ-পিপ")
                                    val pulse = if (elapsedH < 0.14) {
                                        sin(PI * (elapsedH / 0.14))
                                    } else if (elapsedH in 0.22..0.38) {
                                        sin(PI * ((elapsedH - 0.22) / 0.16))
                                    } else 0.0
                                    val tone = sin(2.0 * PI * 440.0 * t) * 0.6 +
                                        sin(2.0 * PI * 554.0 * t) * 0.4
                                    tone * pulse * 0.048
                                }
                                else -> {
                                    // Faraway bicycle/rickshaw bell
                                    val env = exp(-elapsedH * 14.0)
                                    val tone = sin(2.0 * PI * 2120.0 * t) * 0.6 +
                                        sin(2.0 * PI * 3190.0 * t) * 0.4
                                    tone * env * 0.036
                                }
                            }
                            mix += hornSample
                            hornRemaining -= dt
                        }

                        // Tea-stall spoon & glass clinking ("টুং-টুং")
                        nextClinkTimer -= dt
                        if (nextClinkTimer <= 0.0) {
                            nextClinkTimer = 4.2 + Random.nextDouble() * 5.0
                            clinkRemaining = 0.48
                        }
                        if (clinkRemaining > 0.0) {
                            val ec = 0.48 - clinkRemaining
                            val tap1 = if (ec >= 0.0) exp(-ec * 22.0) else 0.0
                            val tap2 = if (ec > 0.15) exp(-(ec - 0.15) * 22.0) else 0.0
                            val tap3 = if (ec > 0.30) exp(-(ec - 0.30) * 24.0) else 0.0
                            val glassRing = sin(2.0 * PI * 2890.0 * t) * 0.55 +
                                sin(2.0 * PI * 4320.0 * t) * 0.35 +
                                sin(2.0 * PI * 5810.0 * t) * 0.10
                            mix += glassRing * (tap1 + tap2 + tap3) * 0.026
                            clinkRemaining -= dt
                        }

                        // Tea-stall lively distant chatter syllable bursts (warm modulated low formants)
                        nextChatterTimer -= dt
                        if (nextChatterTimer <= 0.0) {
                            nextChatterTimer = 2.0 + Random.nextDouble() * 2.8
                            chatterRemaining = 0.85 + Random.nextDouble() * 0.65
                            chatterPitchHz = 135.0 + Random.nextDouble() * 95.0
                        }
                        if (chatterRemaining > 0.0) {
                            val syllEnv = (0.5 + 0.5 * sin(2.0 * PI * 4.8 * t)) *
                                sin(PI * (chatterRemaining / 1.5).coerceIn(0.0, 1.0))
                            val formant = sin(2.0 * PI * chatterPitchHz * t) * 0.45 +
                                sin(2.0 * PI * (chatterPitchHz * 2.15) * t) * 0.35 +
                                sin(2.0 * PI * (chatterPitchHz * 3.2) * t) * 0.20
                            val chatterLevel = if (weather == WeatherType.NIGHT) 0.015 else 0.022
                            mix += formant * syllEnv * chatterLevel
                            chatterRemaining -= dt
                        }

                        val finalSample = (mix * duckGain).coerceIn(-0.92, 0.92)
                        pcm[i] = (finalSample * Short.MAX_VALUE).toInt().toShort()
                    }

                    track.write(pcm, 0, pcm.size)
                }
            } catch (_: Throwable) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    fun stopDhakaCityAmbience() {
        cityAmbienceJob?.cancel()
        cityAmbienceJob = null
    }

    fun playBellHorn() {
        if (!soundEnabled) return
        scope.launch {
            val sampleRate = 22050
            val durationSec = 0.42
            val count = (sampleRate * durationSec).toInt()
            val pcm = ShortArray(count)
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val env1 = exp(-t * 14.0)
                val env2 = if (t > 0.16) exp(-(t - 0.16) * 14.0) else 0.0
                val bellTone =
                    sin(2.0 * PI * 2150.0 * t) * 0.55 +
                    sin(2.0 * PI * 3240.0 * t) * 0.30 +
                    sin(2.0 * PI * 4310.0 * t) * 0.15
                val amp = (env1 + env2) * bellTone * 0.40
                pcm[i] = (amp * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(pcm, sampleRate)
        }
    }

    fun playCoinPickup() {
        if (!soundEnabled) return
        scope.launch {
            val sampleRate = 22050
            val count = (sampleRate * 0.16).toInt()
            val pcm = ShortArray(count)
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val freq = if (t < 0.06) 987.77 else 1318.51
                val env = exp(-t * 17.0)
                val v = sin(2.0 * PI * freq * t) * env * 0.32
                pcm[i] = (v * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(pcm, sampleRate)
        }
    }

    fun playPassengerChime() {
        if (!soundEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99)
            val sampleRate = 22050
            val seg = (sampleRate * 0.085).toInt()
            val pcm = ShortArray(seg * notes.size)
            notes.forEachIndexed { idx, f ->
                for (i in 0 until seg) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 12.0)
                    val v = sin(2.0 * PI * f * t) * env * 0.32
                    pcm[idx * seg + i] = (v * Short.MAX_VALUE).toInt().toShort()
                }
            }
            playPcm(pcm, sampleRate)
        }
    }

    fun playCrash() {
        if (!soundEnabled) return
        scope.launch {
            val sampleRate = 22050
            val count = (sampleRate * 0.26).toInt()
            val pcm = ShortArray(count)
            var seed = 12345
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                seed = (seed * 1103515245 + 12345) and 0x7fffffff
                val noise = ((seed % 2000) / 1000.0 - 1.0)
                val lowThump = sin(2.0 * PI * (120.0 - t * 220.0).coerceAtLeast(45.0) * t)
                val env = exp(-t * 10.0)
                val v = (lowThump * 0.65 + noise * 0.35) * env * 0.48
                pcm[i] = (v * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcm(pcm, sampleRate)
        }
    }

    fun playVictory() {
        if (!soundEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val sampleRate = 22050
            val seg = (sampleRate * 0.12).toInt()
            val pcm = ShortArray(seg * notes.size)
            notes.forEachIndexed { idx, f ->
                for (i in 0 until seg) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 9.0)
                    val v = (sin(2.0 * PI * f * t) + 0.3 * sin(2.0 * PI * f * 2.0 * t)) * env * 0.28
                    pcm[idx * seg + i] = (v * Short.MAX_VALUE).toInt().toShort()
                }
            }
            playPcm(pcm, sampleRate)
        }
    }

    fun startAmbientMusic() {
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            val melody = doubleArrayOf(
                392.0, 440.0, 523.25, 440.0,
                392.0, 329.63, 293.66, 329.63,
                392.0, 523.25, 587.33, 523.25,
                440.0, 392.0, 329.63, 261.63
            )
            var idx = 0
            val sampleRate = 16000
            val seg = (sampleRate * 0.24).toInt()
            while (isActive) {
                if (musicEnabled && !isSpeakingNow) {
                    val f = melody[idx % melody.size]
                    val pcm = ShortArray(seg)
                    for (i in 0 until seg) {
                        val t = i.toDouble() / sampleRate
                        val env = exp(-t * 11.0)
                        val v = sin(2.0 * PI * f * t) * env * 0.065
                        pcm[i] = (v * Short.MAX_VALUE).toInt().toShort()
                    }
                    playPcm(pcm, sampleRate)
                    idx++
                    delay(125)
                } else {
                    delay(280)
                }
            }
        }
    }

    fun stopAmbientMusic() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playPcm(pcm: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferBytes = maxOf(minBuf, pcm.size * 2)
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            track.play()
            val sleepMs = (pcm.size * 1000L / sampleRate) + 40L
            Thread.sleep(sleepMs)
        } catch (_: Throwable) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    fun release() {
        stopDhakaCityAmbience()
        stopAmbientMusic()
        stopSpeaking()
        try {
            tts?.shutdown()
        } catch (_: Throwable) {
        }
    }
}
