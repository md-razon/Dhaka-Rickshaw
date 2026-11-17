package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppScreen {
    MAIN_MENU,
    MISSIONS,
    GARAGE,
    SHOP,
    SETTINGS,
    GAMEPLAY
}

enum class GameMode(val titleBn: String, val subtitleBn: String) {
    ENDLESS("অসীম রাইড", "যত দূর পারেন চালিয়ে রেকর্ড গড়ুন!"),
    MISSION("মিশন মোড", "ঢাকার মজার মিশন সম্পন্ন করে পুরস্কার জিতুন"),
    CHALLENGE("চ্যালেঞ্জ মোড", "কঠিন শর্ত ও সময়ের সাথে লড়াই!")
}

enum class WeatherType(val titleBn: String) {
    DAY("রৌদ্রোজ্জ্বল দিন"),
    NIGHT("ঝলমলে রাত"),
    RAIN("বর্ষার বৃষ্টি")
}

enum class WeatherPreference(val titleBn: String) {
    DYNAMIC("স্বয়ংক্রিয় (দিন-রাত-বৃষ্টি)"),
    DAY_ONLY("শুধু দিন"),
    NIGHT_ONLY("শুধু রাত"),
    RAIN_ONLY("শুধু বৃষ্টি")
}

enum class SteeringMode(val titleBn: String) {
    BUTTONS("বাটন স্টিয়ারিং"),
    BOTH("বাটন + টাচ ড্র্যাগ")
}

data class RickshawDesign(
    val id: String,
    val nameBn: String,
    val subtitleBn: String,
    val price: Int,
    val bodyPrimary: Color,
    val bodySecondary: Color,
    val canopyColor: Color,
    val trimGold: Color,
    val seatColor: Color,
    val shirtColor: Color,
    val lungiColor: Color
)

data class CanopyStyle(
    val id: String,
    val nameBn: String,
    val descriptionBn: String,
    val price: Int,
    val hasTassels: Boolean,
    val hasFloralArch: Boolean,
    val hasRainShield: Boolean
)

data class DecorationStyle(
    val id: String,
    val nameBn: String,
    val descriptionBn: String,
    val price: Int,
    val hasMarigoldGarland: Boolean,
    val hasExtraBells: Boolean,
    val hasFestivalLights: Boolean
)

enum class MissionGoalType {
    DISTANCE,
    PASSENGERS,
    COINS,
    COMBO_DELIVERY_AND_DISTANCE
}

data class Mission(
    val id: String,
    val number: Int,
    val titleBn: String,
    val routeBn: String,
    val descriptionBn: String,
    val weather: WeatherType,
    val goalType: MissionGoalType,
    val targetDistanceMeters: Int,
    val targetPassengers: Int,
    val targetCoins: Int,
    val rewardCoins: Int,
    val difficultyLevel: Int // 1..4
)

data class ChallengeConfig(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val ruleBn: String,
    val weather: WeatherType,
    val timeLimitSeconds: Int,
    val targetDistanceMeters: Int,
    val maxHearts: Int,
    val speedMultiplier: Float,
    val weakBrakes: Boolean,
    val rewardCoins: Int
)

enum class SpeakerType(val labelBn: String) {
    DRIVER("রিকশাওয়ালা মামা"),
    PASSENGER("যাত্রী")
}

enum class PassengerPersona(val labelBn: String, val displayTitleBn: String) {
    MALE("ভাই/চাচা", "যাত্রী (ভাই)"),
    FEMALE("আপা/খালাম্মা", "যাত্রী (আপা)"),
    CHILD("ছোট্ট বাবু", "ছোট্ট যাত্রী")
}

enum class VoiceRole {
    DRIVER_MALE,
    PASSENGER_MALE,
    PASSENGER_FEMALE,
    PASSENGER_CHILD
}

enum class CharacterExpression {
    HAPPY,
    SURPRISED,
    SWEATING,
    ANNOYED,
    LAUGHING,
    PANIC
}

enum class DialogueCategory {
    RANDOM_CHAT,
    PASSENGER_REQUEST,
    TRAFFIC_JOKE,
    SPEED_JOKE,
    COIN_REACTION,
    ACCIDENT_REACTION,
    PASSENGER_PICKUP,
    PASSENGER_DELIVERY,
    MISSION_COMPLETE,
    WEATHER_JOKE,
    TRAFFIC_JAM,
    UNEXPECTED_EVENT
}

data class DialogueLine(
    val id: Int,
    val category: DialogueCategory,
    val speaker: SpeakerType,
    val textBn: String,
    val expression: CharacterExpression = CharacterExpression.HAPPY
)

data class PlayerProgress(
    val coins: Int = 150,
    val bestDistanceMeters: Int = 0,
    val bestScore: Int = 0,
    val totalPassengersDelivered: Int = 0,
    val unlockedDesigns: Set<String> = setOf("lalbagh_classic"),
    val unlockedCanopies: Set<String> = setOf("classic_fold"),
    val unlockedDecorations: Set<String> = setOf("none"),
    val equippedDesignId: String = "lalbagh_classic",
    val equippedCanopyId: String = "classic_fold",
    val equippedDecorationId: String = "none",
    val speedLevel: Int = 1,
    val brakeLevel: Int = 1,
    val handlingLevel: Int = 1,
    val completedMissions: Set<String> = emptySet(),
    val completedChallenges: Set<String> = emptySet(),
    val lastBonusDayKey: String = ""
)

data class GameSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val weatherPreference: WeatherPreference = WeatherPreference.DYNAMIC,
    val steeringMode: SteeringMode = SteeringMode.BOTH
)

data class ActiveLaunchConfig(
    val mode: GameMode = GameMode.ENDLESS,
    val mission: Mission? = null,
    val challenge: ChallengeConfig? = null
)

data class RunSummary(
    val mode: GameMode,
    val distanceMeters: Int,
    val score: Int,
    val coinsEarned: Int,
    val bonusCoins: Int,
    val passengersDelivered: Int,
    val nearMissCount: Int,
    val isVictory: Boolean,
    val missionTitleBn: String? = null,
    val closingQuoteBn: String
)
