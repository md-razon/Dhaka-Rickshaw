package com.example.game

import com.example.data.DialogueSelector
import com.example.model.ChallengeConfig
import com.example.model.DialogueCategory
import com.example.model.DialogueLine
import com.example.model.GameMode
import com.example.model.GameSettings
import com.example.model.Mission
import com.example.model.MissionGoalType
import com.example.model.PassengerPersona
import com.example.model.PlayerProgress
import com.example.model.RunSummary
import com.example.model.SpeakerType
import com.example.model.VoiceRole
import com.example.model.WeatherPreference
import com.example.model.WeatherType
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * 2D World Normalized Coordinate System:
 * - X: 0.0 (left edge) .. 1.0 (right edge)
 *      Road asphalt: 0.14 .. 0.86 (4 lanes at 0.24, 0.41, 0.59, 0.76)
 *      Left sidewalk: 0.0 .. 0.14 | Right sidewalk: 0.86 .. 1.0
 * - Y: 0.0 (top of viewport) .. 1.0 (bottom of viewport)
 *      Player traditional 3-wheel cycle rickshaw stays at playerY = 0.74f
 */
enum class TrafficType(
    val halfWidthNorm: Float,
    val halfHeightNorm: Float,
    val baseSpeedKmh: Float
) {
    BUS(0.068f, 0.088f, 5.5f),
    CAR(0.056f, 0.058f, 7.2f),
    MOTORCYCLE(0.028f, 0.042f, 9.5f),
    PUSHCART(0.054f, 0.052f, 2.2f),
    PEDESTRIAN(0.024f, 0.026f, 1.2f)
}

data class TrafficEntity(
    val id: Int,
    val type: TrafficType,
    var x: Float,
    var y: Float,
    var laneIndex: Int,
    var speedKmh: Float,
    val colorIndex: Int,
    var wobblePhase: Float = 0f,
    var honkedReactionTimer: Float = 0f,
    var nearMissed: Boolean = false,
    var lateralDrift: Float = 0f
)

data class CoinEntity(
    val id: Int,
    var x: Float,
    var y: Float,
    var collected: Boolean = false
)

data class PotholeEntity(
    val id: Int,
    var x: Float,
    var y: Float,
    var hit: Boolean = false
)

enum class StopZoneType { PICKUP, DROPOFF }

data class PassengerStopZone(
    val id: Int,
    val type: StopZoneType,
    val onLeftSide: Boolean,
    var y: Float,
    val passengerStyleIndex: Int,
    val persona: PassengerPersona,
    var completed: Boolean = false
)

enum class TransitionPhase { BOARDING, ALIGHTING }

data class PassengerTransitionState(
    val phase: TransitionPhase,
    val persona: PassengerPersona,
    val styleIndex: Int,
    val onLeftSide: Boolean,
    var progress: Float,
    val curbX: Float,
    var anchorY: Float
)

enum class BillboardKind { POLE_BILLBOARD, WALL_POSTER, CINEMA_POSTER }

data class RoadsideBillboard(
    val id: Int,
    val onLeftSide: Boolean,
    var y: Float,
    val kind: BillboardKind,
    val titleBn: String,
    val subtitleBn: String,
    val bgColor: Long,
    val accentColor: Long
)

data class RoadsideBuilding(
    val id: Int,
    val onLeftSide: Boolean,
    var y: Float,
    val heightNorm: Float,
    val wallColor: Long,
    val roofColor: Long,
    val isTree: Boolean
)

data class FloatingParticle(
    var x: Float,
    var y: Float,
    var vy: Float,
    var life: Float,
    val text: String,
    val isPositive: Boolean = true
)

data class BurstCoinParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float
)

data class RainDrop(
    var x: Float,
    var y: Float,
    val speed: Float,
    val length: Float
)

data class ActiveSpeechBubble(
    val line: DialogueLine,
    val speakerTitleBn: String,
    val remainingSec: Float
)

data class InputControls(
    val steerLeft: Boolean = false,
    val steerRight: Boolean = false,
    val pedal: Boolean = false,
    val brake: Boolean = false,
    val dragDeltaX: Float = 0f
)

sealed interface GameSoundEvent {
    data object Coin : GameSoundEvent
    data object Pickup : GameSoundEvent
    data object Delivery : GameSoundEvent
    data object Crash : GameSoundEvent
    data object Bell : GameSoundEvent
    data object Victory : GameSoundEvent
    data class DistantHorn(val isBus: Boolean) : GameSoundEvent
    data class Speak(val line: DialogueLine, val voiceRole: VoiceRole) : GameSoundEvent
}

class GameEngine(
    val mode: GameMode,
    val mission: Mission?,
    val challenge: ChallengeConfig?,
    val progress: PlayerProgress,
    val settings: GameSettings
) {
    private val dialogueSelector = DialogueSelector()

    val roadLeft = 0.14f
    val roadRight = 0.86f
    val laneCenters = floatArrayOf(0.24f, 0.41f, 0.59f, 0.76f)

    private val billboardTemplates = listOf(
        Triple(BillboardKind.POLE_BILLBOARD, "গরম চা ও সিঙ্গারা" to "মামার টং • ৳১০", 0xFFD97706L to 0xFFFFF8E1L),
        Triple(BillboardKind.CINEMA_POSTER, "রংধনু সিনেমা হল" to "সুপারহিট মেগা শো!", 0xFFBE185DL to 0xFFFDE047L),
        Triple(BillboardKind.WALL_POSTER, "হাজী বিরিয়ানি" to "পুরান ঢাকার স্বাদ", 0xFF065F46L to 0xFFFBBF24L),
        Triple(BillboardKind.POLE_BILLBOARD, "সাবধানে চালান" to "সামনে স্কুল এলাকা", 0xFF1D4ED8L to 0xFFFFFFFFL),
        Triple(BillboardKind.WALL_POSTER, "বর্ষা ধামাকা সেল" to "গামছা-লুঙ্গি ৫০% ছাড়!", 0xFF7C3AEDL to 0xFFFDE047L),
        Triple(BillboardKind.CINEMA_POSTER, "ঢাকা রিকশা ফেস্ট" to "রঙিন আলপনা মেলা", 0xFFB91C1CL to 0xFFFEF08AL),
        Triple(BillboardKind.POLE_BILLBOARD, "ক্রিং ক্রিং হর্ন দিন" to "পথচারী দেখে চলুন", 0xFF0F766EL to 0xFFFFFFFFL),
        Triple(BillboardKind.WALL_POSTER, "মিষ্টি ও দই ভান্ডার" to "তাজা খাঁটি ছানা", 0xFFC2410CL to 0xFFFFFBEBL)
    )

    // 2D Player State (calm, authentic pedal-rickshaw pace)
    var playerX: Float = 0.41f
    val playerY: Float = 0.72f
    var playerTiltDeg: Float = 0f
    var speedKmh: Float = 10f
    var wheelAngle: Float = 0f
    var pedalPhase: Float = 0f
    var roadScrollOffset: Float = 0f

    var distanceMetersFloat: Float = 0f
    val distanceMeters: Int get() = distanceMetersFloat.toInt()
    var score: Int = 0
    var coinsCollected: Int = 0
    var passengersDelivered: Int = 0
    var nearMissCount: Int = 0

    var hearts: Int = challenge?.maxHearts ?: 3
    val maxHearts: Int = challenge?.maxHearts ?: 3
    var invulnerableTimer: Float = 0f
    var shakeTimer: Float = 0f
    var bellRingTimer: Float = 0f
    var passengerBounceTimer: Float = 0f
    var elapsedSeconds: Float = 0f

    // Start with a lively female passenger onboard so female dialogue is heard right away
    var hasPassenger: Boolean = true
    var currentPassengerStyle: Int = 0
    var currentPassengerPersona: PassengerPersona = PassengerPersona.FEMALE
    var passengerTripDistance: Float = 0f
    var passengerTransition: PassengerTransitionState? = null

    var weather: WeatherType = initialWeather()
    var lightningAlpha: Float = 0f

    var isFinished: Boolean = false
    var isVictory: Boolean = false

    val traffic = mutableListOf<TrafficEntity>()
    val coins = mutableListOf<CoinEntity>()
    val potholes = mutableListOf<PotholeEntity>()
    val stopZones = mutableListOf<PassengerStopZone>()
    val billboards = mutableListOf<RoadsideBillboard>()
    val buildings = mutableListOf<RoadsideBuilding>()
    val particles = mutableListOf<FloatingParticle>()
    val burstCoins = mutableListOf<BurstCoinParticle>()
    val rainDrops = List(80) {
        RainDrop(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            speed = 1.4f + Random.nextFloat() * 0.8f,
            length = 0.03f + Random.nextFloat() * 0.03f
        )
    }

    var activeBubble: ActiveSpeechBubble? = null
    private var nextRandomDialogueTimer: Float = 4.5f
    private var dialogueCooldown: Float = 0f
    private var coinStreak: Int = 0
    private var lastSpeedJokeDistance: Int = -200
    private var lastJamJokeTime: Float = -20f

    private var trafficSpawnTimer: Float = 1.1f
    private var coinSpawnTimer: Float = 1.2f
    private var potholeSpawnTimer: Float = 4.5f
    private var stopZoneSpawnTimer: Float = 5.0f
    private var billboardSpawnTimer: Float = 1.4f
    private var distantTrafficHornCooldown: Float = 0.5f
    private var nextEntityId: Int = 1

    // Slower, natural pedal-powered cycle rickshaw tuning
    private val maxTopSpeedKmh: Float = (22f + (progress.speedLevel - 1) * 2.2f) * (challenge?.speedMultiplier ?: 1f)
    private val pedalAccel: Float = 11f + (progress.speedLevel - 1) * 1.6f
    private val brakeDecel: Float = (if (challenge?.weakBrakes == true) 8.5f else 22f) + (progress.brakeLevel - 1) * 3f
    private val steerSpeed: Float = 0.56f + (progress.handlingLevel - 1) * 0.06f

    init {
        for (i in 0..7) {
            spawnBuildingRow(i * 0.16f - 0.10f)
        }
        spawnBillboardAt(0.18f, true)
        spawnBillboardAt(0.52f, false)
    }

    private fun initialWeather(): WeatherType {
        mission?.let { return it.weather }
        challenge?.let { return it.weather }
        return when (settings.weatherPreference) {
            WeatherPreference.DYNAMIC -> WeatherType.DAY
            WeatherPreference.DAY_ONLY -> WeatherType.DAY
            WeatherPreference.NIGHT_ONLY -> WeatherType.NIGHT
            WeatherPreference.RAIN_ONLY -> WeatherType.RAIN
        }
    }

    fun triggerHorn(): List<GameSoundEvent> {
        val events = mutableListOf<GameSoundEvent>()
        bellRingTimer = 0.55f
        events += GameSoundEvent.Bell

        var reacted = false
        for (entity in traffic) {
            val dy = playerY - entity.y
            if (dy in -0.05f..0.46f && abs(entity.x - playerX) < 0.22f) {
                entity.honkedReactionTimer = 0.85f
                if (entity.type == TrafficType.PEDESTRIAN) {
                    entity.lateralDrift = if (entity.x < 0.5f) -0.30f else 0.30f
                    reacted = true
                }
            }
        }
        if (reacted) {
            score += 25
            particles += FloatingParticle(playerX, playerY - 0.10f, -0.08f, 1.0f, "+২৫ জায়গা ছাড়লো!", true)
            maybeTriggerDialogue(DialogueCategory.TRAFFIC_JOKE, events, force = false)
        } else if (speedKmh < 10f && traffic.any { abs(it.y - playerY) < 0.35f } && elapsedSeconds - lastJamJokeTime > 16f) {
            lastJamJokeTime = elapsedSeconds
            maybeTriggerDialogue(DialogueCategory.TRAFFIC_JAM, events, force = false)
        }
        return events
    }

    fun step(dtRaw: Float, controls: InputControls): List<GameSoundEvent> {
        if (isFinished) return emptyList()
        val dt = dtRaw.coerceIn(0.001f, 0.05f)
        val events = mutableListOf<GameSoundEvent>()

        elapsedSeconds += dt
        if (invulnerableTimer > 0f) invulnerableTimer = (invulnerableTimer - dt).coerceAtLeast(0f)
        if (shakeTimer > 0f) shakeTimer = (shakeTimer - dt).coerceAtLeast(0f)
        if (bellRingTimer > 0f) bellRingTimer = (bellRingTimer - dt).coerceAtLeast(0f)
        if (passengerBounceTimer > 0f) passengerBounceTimer = (passengerBounceTimer - dt).coerceAtLeast(0f)
        if (dialogueCooldown > 0f) dialogueCooldown = (dialogueCooldown - dt).coerceAtLeast(0f)
        if (lightningAlpha > 0f) lightningAlpha = (lightningAlpha - dt * 2.2f).coerceAtLeast(0f)
        if (distantTrafficHornCooldown > 0f) distantTrafficHornCooldown = (distantTrafficHornCooldown - dt).coerceAtLeast(0f)

        activeBubble?.let { bubble ->
            val left = bubble.remainingSec - dt
            activeBubble = if (left > 0f) bubble.copy(remainingSec = left) else null
        }

        if (mode == GameMode.ENDLESS && settings.weatherPreference == WeatherPreference.DYNAMIC) {
            val cycle = (distanceMeters / 340) % 3
            val targetWeather = when (cycle) {
                0 -> WeatherType.DAY
                1 -> WeatherType.NIGHT
                else -> WeatherType.RAIN
            }
            if (targetWeather != weather) {
                weather = targetWeather
                maybeTriggerDialogue(DialogueCategory.WEATHER_JOKE, events, force = true)
            }
        }

        val difficultyBoost = (distanceMeters / 600f).coerceAtMost(4f)
        val rainGripPenalty = if (weather == WeatherType.RAIN) 0.90f else 1.0f
        val isTransitioning = passengerTransition != null

        when {
            controls.brake -> {
                speedKmh = (speedKmh - brakeDecel * dt).coerceAtLeast(2.5f)
            }
            isTransitioning -> {
                speedKmh = (speedKmh - 10f * dt).coerceAtLeast(6.5f)
            }
            controls.pedal -> {
                speedKmh = (speedKmh + pedalAccel * dt).coerceAtMost(maxTopSpeedKmh + difficultyBoost * 0.3f)
            }
            else -> {
                val cruiseFloor = 9.5f
                if (speedKmh > cruiseFloor) {
                    speedKmh = (speedKmh - 4.2f * dt).coerceAtLeast(cruiseFloor)
                } else {
                    speedKmh = (speedKmh + 3.2f * dt).coerceAtMost(cruiseFloor)
                }
            }
        }

        // Smooth 2D steering
        var steerDir = 0f
        if (controls.steerLeft) steerDir -= 1f
        if (controls.steerRight) steerDir += 1f
        if (controls.dragDeltaX != 0f) {
            playerX = (playerX + controls.dragDeltaX).coerceIn(roadLeft + 0.07f, roadRight - 0.07f)
            steerDir = (controls.dragDeltaX * 26f).coerceIn(-1f, 1f)
        } else {
            playerX = (playerX + steerDir * steerSpeed * rainGripPenalty * dt)
                .coerceIn(roadLeft + 0.07f, roadRight - 0.07f)
        }

        val targetTilt = steerDir * 10f
        playerTiltDeg += (targetTilt - playerTiltDeg) * (dt * 10f).coerceAtMost(1f)

        // Calm, relaxed 2D downward world scroll
        val scrollPerSec = speedKmh * 0.0155f
        roadScrollOffset = (roadScrollOffset + scrollPerSec * dt) % 0.16f
        wheelAngle += speedKmh * 0.42f * dt
        val pedalRate = if (controls.pedal) speedKmh * 0.38f else speedKmh * 0.22f
        pedalPhase += pedalRate * dt

        val distDelta = speedKmh * 0.28f * dt
        distanceMetersFloat += distDelta
        score += (distDelta * 2.2f).toInt()
        if (hasPassenger) passengerTripDistance += distDelta

        if (speedKmh > maxTopSpeedKmh * 0.9f && distanceMeters - lastSpeedJokeDistance > 250) {
            lastSpeedJokeDistance = distanceMeters
            maybeTriggerDialogue(DialogueCategory.SPEED_JOKE, events, force = false)
        }

        if (weather == WeatherType.RAIN) {
            for (drop in rainDrops) {
                drop.y += drop.speed * dt
                drop.x -= 0.15f * dt
                if (drop.y > 1.05f) {
                    drop.y = -0.05f
                    drop.x = Random.nextFloat() * 1.15f
                }
            }
            if (Random.nextFloat() < 0.004f) {
                lightningAlpha = 0.50f
            }
        }

        spawnEntities(dt, difficultyBoost)
        updateBuildings(dt, scrollPerSec)
        updateBillboards(dt, scrollPerSec)
        updateTraffic(dt, events)
        updateCoins(dt, scrollPerSec, events)
        updatePotholes(dt, scrollPerSec)
        updateStopZones(dt, scrollPerSec, events)
        updatePassengerTransition(dt, scrollPerSec, events)

        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.y += p.vy * dt
            p.life -= dt
            if (p.life <= 0f) pIter.remove()
        }

        val bIter = burstCoins.iterator()
        while (bIter.hasNext()) {
            val b = bIter.next()
            b.x += b.vx * dt
            b.y += b.vy * dt
            b.vy += 0.55f * dt
            b.life -= dt
            if (b.life <= 0f) bIter.remove()
        }

        nextRandomDialogueTimer -= dt
        if (nextRandomDialogueTimer <= 0f) {
            nextRandomDialogueTimer = 15f + Random.nextFloat() * 20f
            if (activeBubble == null) {
                val category = pickAmbientCategory()
                maybeTriggerDialogue(category, events, force = false)
            }
        }

        checkEndConditions(events)
        return events
    }

    private fun pickAmbientCategory(): DialogueCategory {
        val roll = Random.nextFloat()
        return when {
            hasPassenger && roll < 0.42f -> DialogueCategory.PASSENGER_REQUEST
            roll < 0.62f -> DialogueCategory.RANDOM_CHAT
            roll < 0.76f -> DialogueCategory.TRAFFIC_JOKE
            weather != WeatherType.DAY && roll < 0.88f -> DialogueCategory.WEATHER_JOKE
            else -> DialogueCategory.UNEXPECTED_EVENT
        }
    }

    private fun spawnBuildingRow(yPos: Float) {
        val buildingColors = listOf(0xFFE07A5FL, 0xFF5B8DEF, 0xFFF4A261L, 0xFF52B788L, 0xFF9B72CFL)
        val roofColors = listOf(0xFF9E3A24L, 0xFF284B9EL, 0xFFB06024L, 0xFF216847L, 0xFF5E3A8CL)
        for (left in listOf(true, false)) {
            val isTree = Random.nextFloat() < 0.44f
            val idx = Random.nextInt(buildingColors.size)
            buildings += RoadsideBuilding(
                id = nextEntityId++,
                onLeftSide = left,
                y = yPos,
                heightNorm = if (isTree) 0.11f else 0.12f + Random.nextFloat() * 0.04f,
                wallColor = buildingColors[idx],
                roofColor = roofColors[idx],
                isTree = isTree
            )
        }
    }

    private fun spawnBillboardAt(yPos: Float, onLeft: Boolean) {
        val tpl = billboardTemplates[Random.nextInt(billboardTemplates.size)]
        billboards += RoadsideBillboard(
            id = nextEntityId++,
            onLeftSide = onLeft,
            y = yPos,
            kind = tpl.first,
            titleBn = tpl.second.first,
            subtitleBn = tpl.second.second,
            bgColor = tpl.third.first,
            accentColor = tpl.third.second
        )
    }

    private fun pickNextPersona(): PassengerPersona {
        val r = Random.nextFloat()
        return when {
            r < 0.50f -> PassengerPersona.FEMALE
            r < 0.78f -> PassengerPersona.CHILD
            else -> PassengerPersona.MALE
        }
    }

    private fun spawnEntities(dt: Float, difficultyBoost: Float) {
        val diffFactor = (1f + difficultyBoost * 0.08f)
        trafficSpawnTimer -= dt
        if (trafficSpawnTimer <= 0f) {
            trafficSpawnTimer = (1.65f / diffFactor).coerceAtLeast(0.85f) + Random.nextFloat() * 0.7f
            val lane = Random.nextInt(laneCenters.size)
            val laneX = laneCenters[lane]
            val clearTop = traffic.none { it.laneIndex == lane && it.y < 0.24f }
            if (clearTop) {
                val type = when (Random.nextFloat()) {
                    in 0f..0.24f -> TrafficType.BUS
                    in 0.24f..0.52f -> TrafficType.CAR
                    in 0.52f..0.76f -> TrafficType.MOTORCYCLE
                    in 0.76f..0.90f -> TrafficType.PUSHCART
                    else -> TrafficType.PEDESTRIAN
                }
                val spawnX = if (type == TrafficType.PEDESTRIAN) {
                    if (Random.nextBoolean()) roadLeft + 0.04f else roadRight - 0.04f
                } else {
                    laneX
                }
                traffic += TrafficEntity(
                    id = nextEntityId++,
                    type = type,
                    x = spawnX,
                    y = -0.14f,
                    laneIndex = lane,
                    speedKmh = type.baseSpeedKmh + Random.nextFloat() * 2f,
                    colorIndex = Random.nextInt(5)
                )
            }
        }

        coinSpawnTimer -= dt
        if (coinSpawnTimer <= 0f) {
            coinSpawnTimer = 1.9f + Random.nextFloat() * 1.4f
            val lane = Random.nextInt(laneCenters.size)
            val baseX = laneCenters[lane]
            val count = Random.nextInt(3, 6)
            for (i in 0 until count) {
                coins += CoinEntity(
                    id = nextEntityId++,
                    x = baseX,
                    y = -0.08f - i * 0.06f
                )
            }
        }

        potholeSpawnTimer -= dt
        if (potholeSpawnTimer <= 0f) {
            potholeSpawnTimer = 5.4f + Random.nextFloat() * 3.5f
            val lane = Random.nextInt(laneCenters.size)
            potholes += PotholeEntity(
                id = nextEntityId++,
                x = laneCenters[lane] + (Random.nextFloat() - 0.5f) * 0.04f,
                y = -0.06f
            )
        }

        billboardSpawnTimer -= dt
        if (billboardSpawnTimer <= 0f) {
            billboardSpawnTimer = 3.2f + Random.nextFloat() * 1.8f
            if (billboards.size < 4) {
                spawnBillboardAt(-0.14f, Random.nextBoolean())
            }
        }

        stopZoneSpawnTimer -= dt
        if (stopZoneSpawnTimer <= 0f) {
            stopZoneSpawnTimer = if (hasPassenger) 6.5f else 4.5f
            if (stopZones.isEmpty() && passengerTransition == null) {
                val onLeft = Random.nextBoolean()
                val persona = if (hasPassenger) currentPassengerPersona else pickNextPersona()
                stopZones += PassengerStopZone(
                    id = nextEntityId++,
                    type = if (hasPassenger) StopZoneType.DROPOFF else StopZoneType.PICKUP,
                    onLeftSide = onLeft,
                    y = -0.12f,
                    passengerStyleIndex = if (hasPassenger) currentPassengerStyle else Random.nextInt(4),
                    persona = persona
                )
            }
        }
    }

    private fun updateBuildings(dt: Float, scrollPerSec: Float) {
        var topMostY = 1f
        val iter = buildings.iterator()
        while (iter.hasNext()) {
            val b = iter.next()
            b.y += scrollPerSec * dt
            if (b.y < topMostY) topMostY = b.y
            if (b.y > 1.25f) iter.remove()
        }
        while (topMostY > -0.16f) {
            topMostY -= 0.16f
            spawnBuildingRow(topMostY)
        }
    }

    private fun updateBillboards(dt: Float, scrollPerSec: Float) {
        val iter = billboards.iterator()
        while (iter.hasNext()) {
            val b = iter.next()
            b.y += scrollPerSec * dt
            if (b.y > 1.2f) iter.remove()
        }
    }

    private fun updateTraffic(dt: Float, events: MutableList<GameSoundEvent>) {
        val rickshawHalfW = 0.046f
        val rickshawHalfH = 0.062f
        val iter = traffic.iterator()
        while (iter.hasNext()) {
            val e = iter.next()
            val relRate = (speedKmh - e.speedKmh) * 0.0155f
            val effectiveRate = if (e.y < 0.08f && relRate < 0.08f) 0.09f else relRate
            e.y += effectiveRate * dt
            e.wobblePhase += dt * 6f
            if (e.honkedReactionTimer > 0f) {
                e.honkedReactionTimer = (e.honkedReactionTimer - dt).coerceAtLeast(0f)
            }
            if (e.lateralDrift != 0f) {
                e.x = (e.x + e.lateralDrift * dt).coerceIn(0.05f, 0.95f)
            } else if (e.type == TrafficType.MOTORCYCLE) {
                e.x = (laneCenters[e.laneIndex] + sin(e.wobblePhase * 0.45f) * 0.024f)
                    .coerceIn(roadLeft + 0.04f, roadRight - 0.04f)
            }

            val dx = abs(e.x - playerX)
            val dy = abs(e.y - playerY)
            if (distantTrafficHornCooldown <= 0f && e.y in 0.10f..0.28f && (e.type == TrafficType.BUS || e.type == TrafficType.CAR)) {
                distantTrafficHornCooldown = 4.5f + Random.nextFloat() * 4.0f
                events += GameSoundEvent.DistantHorn(isBus = e.type == TrafficType.BUS)
            }
            val hitW = rickshawHalfW + e.type.halfWidthNorm * 0.80f
            val hitH = rickshawHalfH + e.type.halfHeightNorm * 0.78f

            if (dx < hitW && dy < hitH) {
                if (invulnerableTimer <= 0f) {
                    hearts -= 1
                    invulnerableTimer = 1.7f
                    shakeTimer = 0.42f
                    speedKmh = (speedKmh * 0.48f).coerceAtLeast(4.5f)
                    coinStreak = 0
                    events += GameSoundEvent.Crash
                    particles += FloatingParticle(playerX, playerY - 0.08f, -0.10f, 1.1f, "ধাক্কা!", false)
                    maybeTriggerDialogue(DialogueCategory.ACCIDENT_REACTION, events, force = true)
                    if (e.type == TrafficType.PEDESTRIAN || e.type == TrafficType.MOTORCYCLE) {
                        iter.remove()
                        continue
                    }
                }
            } else if (!e.nearMissed && e.y > playerY + 0.07f && dx < hitW * 1.6f && dy < hitH * 1.5f) {
                e.nearMissed = true
                nearMissCount++
                score += 40
                coinsCollected += 2
                particles += FloatingParticle(playerX, playerY - 0.10f, -0.09f, 0.9f, "+৳২ চমৎকার কাট!", true)
            }

            if (e.y > 1.22f || e.y < -0.35f) {
                iter.remove()
            }
        }
    }

    private fun updateCoins(dt: Float, scrollPerSec: Float, events: MutableList<GameSoundEvent>) {
        val iter = coins.iterator()
        while (iter.hasNext()) {
            val c = iter.next()
            c.y += scrollPerSec * dt
            if (!c.collected && abs(c.x - playerX) < 0.075f && abs(c.y - playerY) < 0.085f) {
                c.collected = true
                coinsCollected += 1
                score += 15
                coinStreak++
                events += GameSoundEvent.Coin
                particles += FloatingParticle(c.x, c.y - 0.03f, -0.09f, 0.7f, "+৳১", true)
                if (coinStreak >= 5 && coinStreak % 5 == 0) {
                    maybeTriggerDialogue(DialogueCategory.COIN_REACTION, events, force = false)
                }
                iter.remove()
                continue
            }
            if (c.y > 1.1f) iter.remove()
        }
    }

    private fun updatePotholes(dt: Float, scrollPerSec: Float) {
        val iter = potholes.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.y += scrollPerSec * dt
            if (!p.hit && abs(p.x - playerX) < 0.062f && abs(p.y - playerY) < 0.065f) {
                p.hit = true
                speedKmh = (speedKmh * 0.74f).coerceAtLeast(5.5f)
                shakeTimer = 0.18f
                particles += FloatingParticle(playerX, playerY - 0.06f, -0.08f, 0.8f, "গর্ত! ধীর গতি", false)
            }
            if (p.y > 1.1f) iter.remove()
        }
    }

    private fun updateStopZones(dt: Float, scrollPerSec: Float, events: MutableList<GameSoundEvent>) {
        val iter = stopZones.iterator()
        while (iter.hasNext()) {
            val z = iter.next()
            z.y += scrollPerSec * dt
            val nearCurb = if (z.onLeftSide) playerX < 0.36f else playerX > 0.64f
            val nearY = abs(z.y - playerY) < 0.09f
            if (!z.completed && nearCurb && nearY && passengerTransition == null) {
                z.completed = true
                if (z.type == StopZoneType.PICKUP && !hasPassenger) {
                    currentPassengerStyle = z.passengerStyleIndex
                    currentPassengerPersona = z.persona
                    passengerTransition = PassengerTransitionState(
                        phase = TransitionPhase.BOARDING,
                        persona = z.persona,
                        styleIndex = z.passengerStyleIndex,
                        onLeftSide = z.onLeftSide,
                        progress = 0f,
                        curbX = if (z.onLeftSide) 0.09f else 0.91f,
                        anchorY = z.y
                    )
                    particles += FloatingParticle(playerX, playerY - 0.10f, -0.07f, 1.0f, "যাত্রী উঠছে...", true)
                    events += GameSoundEvent.Pickup
                } else if (z.type == StopZoneType.DROPOFF && hasPassenger) {
                    hasPassenger = false
                    passengersDelivered++
                    val fareBonus = 15 + (if (speedKmh < 14f) 5 else 0)
                    coinsCollected += fareBonus
                    score += 180
                    passengerBounceTimer = 0.45f
                    passengerTransition = PassengerTransitionState(
                        phase = TransitionPhase.ALIGHTING,
                        persona = currentPassengerPersona,
                        styleIndex = currentPassengerStyle,
                        onLeftSide = z.onLeftSide,
                        progress = 0f,
                        curbX = if (z.onLeftSide) 0.09f else 0.91f,
                        anchorY = z.y
                    )
                    for (i in 0 until 7) {
                        burstCoins += BurstCoinParticle(
                            x = playerX,
                            y = playerY,
                            vx = (Random.nextFloat() - 0.5f) * 0.34f,
                            vy = -0.18f - Random.nextFloat() * 0.15f,
                            life = 0.9f
                        )
                    }
                    events += GameSoundEvent.Delivery
                    particles += FloatingParticle(playerX, playerY - 0.12f, -0.09f, 1.3f, "যাত্রী নামলো! +৳$fareBonus", true)
                    maybeTriggerDialogue(DialogueCategory.PASSENGER_DELIVERY, events, force = true)
                    stopZoneSpawnTimer = 4.5f
                }
                iter.remove()
                continue
            }
            if (z.y > 1.15f) iter.remove()
        }
    }

    private fun updatePassengerTransition(
        dt: Float,
        scrollPerSec: Float,
        events: MutableList<GameSoundEvent>
    ) {
        val tr = passengerTransition ?: return
        tr.anchorY = (tr.anchorY + scrollPerSec * dt * 0.25f).coerceIn(playerY - 0.06f, playerY + 0.08f)
        val duration = if (tr.phase == TransitionPhase.BOARDING) 0.85f else 0.90f
        tr.progress = (tr.progress + dt / duration).coerceAtMost(1f)
        if (tr.progress >= 1f) {
            if (tr.phase == TransitionPhase.BOARDING) {
                hasPassenger = true
                passengerTripDistance = 0f
                passengerBounceTimer = 0.45f
                score += 60
                particles += FloatingParticle(playerX, playerY - 0.11f, -0.08f, 1.1f, "${tr.persona.displayTitleBn} উঠেছে!", true)
                maybeTriggerDialogue(DialogueCategory.PASSENGER_PICKUP, events, force = true)
                stopZoneSpawnTimer = 5.5f
            }
            passengerTransition = null
        }
    }

    private fun maybeTriggerDialogue(
        category: DialogueCategory,
        events: MutableList<GameSoundEvent>,
        force: Boolean
    ) {
        if (!force && dialogueCooldown > 0f) return
        val chosen = dialogueSelector.pick(category, hasPassenger || passengerTransition != null)
        val personaForSpeech = currentPassengerPersona
        val voiceRole = if (chosen.speaker == SpeakerType.DRIVER) {
            VoiceRole.DRIVER_MALE
        } else {
            when (personaForSpeech) {
                PassengerPersona.MALE -> VoiceRole.PASSENGER_MALE
                PassengerPersona.FEMALE -> VoiceRole.PASSENGER_FEMALE
                PassengerPersona.CHILD -> VoiceRole.PASSENGER_CHILD
            }
        }
        val speakerTitle = if (chosen.speaker == SpeakerType.DRIVER) {
            chosen.speaker.labelBn
        } else {
            personaForSpeech.displayTitleBn
        }
        activeBubble = ActiveSpeechBubble(chosen, speakerTitle, remainingSec = 4.4f)
        dialogueCooldown = if (force) 2.5f else 6.0f
        events += GameSoundEvent.Speak(chosen, voiceRole)
    }

    private fun checkEndConditions(events: MutableList<GameSoundEvent>) {
        if (hearts <= 0) {
            isFinished = true
            isVictory = false
            return
        }

        mission?.let { m ->
            val met = when (m.goalType) {
                MissionGoalType.DISTANCE -> distanceMeters >= m.targetDistanceMeters
                MissionGoalType.PASSENGERS -> passengersDelivered >= m.targetPassengers && distanceMeters >= m.targetDistanceMeters
                MissionGoalType.COINS -> coinsCollected >= m.targetCoins && distanceMeters >= m.targetDistanceMeters
                MissionGoalType.COMBO_DELIVERY_AND_DISTANCE ->
                    passengersDelivered >= m.targetPassengers &&
                        distanceMeters >= m.targetDistanceMeters &&
                        coinsCollected >= m.targetCoins
            }
            if (met) {
                isFinished = true
                isVictory = true
                events += GameSoundEvent.Victory
                val winLine = dialogueSelector.pick(DialogueCategory.MISSION_COMPLETE, hasPassenger)
                activeBubble = ActiveSpeechBubble(winLine, winLine.speaker.labelBn, 5f)
                events += GameSoundEvent.Speak(winLine, VoiceRole.DRIVER_MALE)
            }
        }

        challenge?.let { c ->
            if (distanceMeters >= c.targetDistanceMeters) {
                isFinished = true
                isVictory = true
                events += GameSoundEvent.Victory
                val winLine = dialogueSelector.pick(DialogueCategory.MISSION_COMPLETE, hasPassenger)
                activeBubble = ActiveSpeechBubble(winLine, winLine.speaker.labelBn, 5f)
                events += GameSoundEvent.Speak(winLine, VoiceRole.DRIVER_MALE)
            } else if (elapsedSeconds >= c.timeLimitSeconds) {
                isFinished = true
                isVictory = false
            }
        }
    }

    fun buildSummary(): RunSummary {
        val bonus = when {
            isVictory && mission != null -> mission.rewardCoins
            isVictory && challenge != null -> challenge.rewardCoins
            else -> 0
        }
        val quote = if (isVictory) {
            dialogueSelector.pick(DialogueCategory.MISSION_COMPLETE, hasPassenger).textBn
        } else {
            dialogueSelector.pick(DialogueCategory.RANDOM_CHAT, false).textBn
        }
        return RunSummary(
            mode = mode,
            distanceMeters = distanceMeters,
            score = score,
            coinsEarned = coinsCollected,
            bonusCoins = bonus,
            passengersDelivered = passengersDelivered,
            nearMissCount = nearMissCount,
            isVictory = isVictory,
            missionTitleBn = mission?.titleBn ?: challenge?.titleBn,
            closingQuoteBn = quote
        )
    }
}
