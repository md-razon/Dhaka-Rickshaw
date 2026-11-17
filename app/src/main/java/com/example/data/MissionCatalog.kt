package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.CanopyStyle
import com.example.model.ChallengeConfig
import com.example.model.DecorationStyle
import com.example.model.Mission
import com.example.model.MissionGoalType
import com.example.model.RickshawDesign
import com.example.model.WeatherType

object MissionCatalog {

    val rickshawDesigns: List<RickshawDesign> = listOf(
        RickshawDesign(
            id = "lalbagh_classic",
            nameBn = "লালবাগ ক্লাসিক",
            subtitleBn = "লাল-সবুজ ও সোনালী ফুলের চিরচেনা ঢাকার রিকশা",
            price = 0,
            bodyPrimary = Color(0xFFD81B60),
            bodySecondary = Color(0xFF00796B),
            canopyColor = Color(0xFFB71C1C),
            trimGold = Color(0xFFFFC107),
            seatColor = Color(0xFFC62828),
            shirtColor = Color(0xFF1E88E5),
            lungiColor = Color(0xFF283593)
        ),
        RickshawDesign(
            id = "padma_sundori",
            nameBn = "পদ্মা সুন্দরী",
            subtitleBn = "ময়ূরকণ্ঠী নীল ও ম্যাজেন্টা রঙের বাহারি সাজ",
            price = 220,
            bodyPrimary = Color(0xFF00838F),
            bodySecondary = Color(0xFFAD1457),
            canopyColor = Color(0xFF006064),
            trimGold = Color(0xFFFFD54F),
            seatColor = Color(0xFF880E4F),
            shirtColor = Color(0xFFFFB300),
            lungiColor = Color(0xFF004D40)
        ),
        RickshawDesign(
            id = "shapla_special",
            nameBn = "শাপলা স্পেশাল",
            subtitleBn = "বেগুনী ও গোলাপী শাপলা আলপনায় সাজানো",
            price = 380,
            bodyPrimary = Color(0xFF5E35B1),
            bodySecondary = Color(0xFFEC407A),
            canopyColor = Color(0xFF4527A0),
            trimGold = Color(0xFFFFCA28),
            seatColor = Color(0xFFD81B60),
            shirtColor = Color(0xFF26A69A),
            lungiColor = Color(0xFF311B92)
        ),
        RickshawDesign(
            id = "shonali_bangla",
            nameBn = "সোনালী বাংলা",
            subtitleBn = "হলুদ সরিষা আর গাঢ় সবুজের রাজকীয় ডিজাইন",
            price = 550,
            bodyPrimary = Color(0xFFEF6C00),
            bodySecondary = Color(0xFF1B5E20),
            canopyColor = Color(0xFFE65100),
            trimGold = Color(0xFFFFEE58),
            seatColor = Color(0xFF2E7D32),
            shirtColor = Color(0xFFE53935),
            lungiColor = Color(0xFF1B5E20)
        ),
        RickshawDesign(
            id = "puran_dhaka_nawabi",
            nameBn = "নবাবী পুরান ঢাকা",
            subtitleBn = "মখমলি মেরুন আর সোনালী জরি কারুকাজ",
            price = 800,
            bodyPrimary = Color(0xFF7B1FA2),
            bodySecondary = Color(0xFF880E4F),
            canopyColor = Color(0xFF4A148C),
            trimGold = Color(0xFFFFD700),
            seatColor = Color(0xFFB71C1C),
            shirtColor = Color(0xFFFFF8E1),
            lungiColor = Color(0xFF4A148C)
        ),
        RickshawDesign(
            id = "rongdhonu_express",
            nameBn = "রঙধনু এক্সপ্রেস",
            subtitleBn = "উৎসবমুখর সাতরঙা ঢাকাই রিকশা শিল্পের সেরা সংস্করণ",
            price = 1200,
            bodyPrimary = Color(0xFFE91E63),
            bodySecondary = Color(0xFF00BFA5),
            canopyColor = Color(0xFFFF6D00),
            trimGold = Color(0xFFFFEA00),
            seatColor = Color(0xFF6200EA),
            shirtColor = Color(0xFF00E676),
            lungiColor = Color(0xFFAD1457)
        )
    )

    val canopyStyles: List<CanopyStyle> = listOf(
        CanopyStyle(
            id = "classic_fold",
            nameBn = "সাধারণ ভাঁজ হুড",
            descriptionBn = "ঢাকার চিরচেনা ভাঁজ করা ক্যানভাস হুড",
            price = 0,
            hasTassels = false,
            hasFloralArch = false,
            hasRainShield = false
        ),
        CanopyStyle(
            id = "tassel_royal",
            nameBn = "ঝালরওয়ালা রাজকীয় হুড",
            descriptionBn = "দোদুল্যমান রঙিন ঝালর ও পাড় বসানো হুড",
            price = 160,
            hasTassels = true,
            hasFloralArch = false,
            hasRainShield = false
        ),
        CanopyStyle(
            id = "floral_folk",
            nameBn = "ফুলের আলপনা হুড",
            descriptionBn = "হাতে আঁকা পদ্মফুল ও পাতার নকশা করা হুড",
            price = 320,
            hasTassels = true,
            hasFloralArch = true,
            hasRainShield = false
        ),
        CanopyStyle(
            id = "monsoon_shield",
            nameBn = "বৃষ্টি-প্রুফ ঝকমকে হুড",
            descriptionBn = "বৃষ্টিতেও যাত্রী থাকবে শুকনো আর খুশি!",
            price = 480,
            hasTassels = true,
            hasFloralArch = true,
            hasRainShield = true
        )
    )

    val decorationStyles: List<DecorationStyle> = listOf(
        DecorationStyle(
            id = "none",
            nameBn = "সাদামাটা সাজ",
            descriptionBn = "কোনো বাড়তি সাজসজ্জা ছাড়া পরিপাটি রিকশা",
            price = 0,
            hasMarigoldGarland = false,
            hasExtraBells = false,
            hasFestivalLights = false
        ),
        DecorationStyle(
            id = "marigold_garland",
            nameBn = "গাঁদা ফুলের মালা",
            descriptionBn = "হ্যান্ডেলবার ও হুডে রঙিন গাঁদা ফুলের মালা",
            price = 140,
            hasMarigoldGarland = true,
            hasExtraBells = false,
            hasFestivalLights = false
        ),
        DecorationStyle(
            id = "bells_tassels",
            nameBn = "ডাবল ঘণ্টা ও ঝালর",
            descriptionBn = "পিতলের বাড়তি ঘণ্টা আর রঙিন ফিতা",
            price = 280,
            hasMarigoldGarland = true,
            hasExtraBells = true,
            hasFestivalLights = false
        ),
        DecorationStyle(
            id = "festival_lights",
            nameBn = "উৎসবের টুনি লাইট",
            descriptionBn = "রাতের ঢাকায় ঝলমলে রঙিন আলোর ঝিলিক!",
            price = 450,
            hasMarigoldGarland = true,
            hasExtraBells = true,
            hasFestivalLights = true
        )
    )

    val missions: List<Mission> = listOf(
        Mission(
            id = "m1_mirpur",
            number = 1,
            titleBn = "সকালের প্রথম ট্রিপ",
            routeBn = "মিরপুর ১০ ➔ ফার্মগেট",
            descriptionBn = "সকালের হালকা রাস্তায় ২ জন যাত্রী পৌঁছে দিন এবং ২৫০ মিটার চালান।",
            weather = WeatherType.DAY,
            goalType = MissionGoalType.COMBO_DELIVERY_AND_DISTANCE,
            targetDistanceMeters = 250,
            targetPassengers = 2,
            targetCoins = 0,
            rewardCoins = 120,
            difficultyLevel = 1
        ),
        Mission(
            id = "m2_shahbag",
            number = 2,
            titleBn = "চা-নাস্তার কয়েন সংগ্রহ",
            routeBn = "শাহবাগ ➔ টিএসসি",
            descriptionBn = "রাস্তা থেকে ২০টি কয়েন সংগ্রহ করুন এবং ৩০০ মিটার পথ পাড়ি দিন।",
            weather = WeatherType.DAY,
            goalType = MissionGoalType.COINS,
            targetDistanceMeters = 300,
            targetPassengers = 0,
            targetCoins = 20,
            rewardCoins = 150,
            difficultyLevel = 1
        ),
        Mission(
            id = "m3_motijheel_rain",
            number = 3,
            titleBn = "বর্ষায় মতিঝিল",
            routeBn = "পল্টন ➔ মতিঝিল",
            descriptionBn = "ঝমঝম বৃষ্টির মধ্যে সাবধানে চালিয়ে ৩ জন যাত্রী পৌঁছে দিন!",
            weather = WeatherType.RAIN,
            goalType = MissionGoalType.PASSENGERS,
            targetDistanceMeters = 350,
            targetPassengers = 3,
            targetCoins = 0,
            rewardCoins = 220,
            difficultyLevel = 2
        ),
        Mission(
            id = "m4_newmarket_night",
            number = 4,
            titleBn = "রাতের নিউমার্কেট",
            routeBn = "সায়েন্স ল্যাব ➔ নিউমার্কেট",
            descriptionBn = "সন্ধ্যার ঝলমলে আলোয় ৪৫০ মিটার দূরত্ব অতিক্রম করুন।",
            weather = WeatherType.NIGHT,
            goalType = MissionGoalType.DISTANCE,
            targetDistanceMeters = 450,
            targetPassengers = 0,
            targetCoins = 0,
            rewardCoins = 240,
            difficultyLevel = 2
        ),
        Mission(
            id = "m5_puran_dhaka",
            number = 5,
            titleBn = "পুরান ঢাকার বিরিয়ানি রাশ",
            routeBn = "চকবাজার ➔ লালবাগ কেল্লা",
            descriptionBn = "ব্যস্ত ভিড় ঠেলে ৪ জন যাত্রী পৌঁছে দিন এবং ২৫টি কয়েন নিন।",
            weather = WeatherType.DAY,
            goalType = MissionGoalType.COMBO_DELIVERY_AND_DISTANCE,
            targetDistanceMeters = 550,
            targetPassengers = 4,
            targetCoins = 25,
            rewardCoins = 320,
            difficultyLevel = 3
        ),
        Mission(
            id = "m6_dhanmondi_monsoon",
            number = 6,
            titleBn = "ধানমন্ডি লেকের ঝড়",
            routeBn = "কলাবাগান ➔ ধানমন্ডি ২৭",
            descriptionBn = "বৃষ্টির রাস্তায় ৬৫০ মিটার চলুন আর ৩৫টি কয়েন সংগ্রহ করুন!",
            weather = WeatherType.RAIN,
            goalType = MissionGoalType.COINS,
            targetDistanceMeters = 650,
            targetPassengers = 2,
            targetCoins = 35,
            rewardCoins = 380,
            difficultyLevel = 3
        ),
        Mission(
            id = "m7_gulshan_night",
            number = 7,
            titleBn = "রাতের গুলশান ট্রিপ",
            routeBn = "বনানী ➔ গুলশান ১",
            descriptionBn = "দ্রুতগতির গাড়ির ভিড়ে রাতের বেলা ৫ জন যাত্রী পৌঁছে দিন।",
            weather = WeatherType.NIGHT,
            goalType = MissionGoalType.PASSENGERS,
            targetDistanceMeters = 750,
            targetPassengers = 5,
            targetCoins = 0,
            rewardCoins = 450,
            difficultyLevel = 4
        ),
        Mission(
            id = "m8_dhaka_legend",
            number = 8,
            titleBn = "ঢাকা রিকশা সম্রাট",
            routeBn = "সদরঘাট ➔ উত্তরা",
            descriptionBn = "মহামিশন! ১০০০ মিটার চালান এবং ৬ জন যাত্রীকে নিরাপদে নামান!",
            weather = WeatherType.DAY,
            goalType = MissionGoalType.COMBO_DELIVERY_AND_DISTANCE,
            targetDistanceMeters = 1000,
            targetPassengers = 6,
            targetCoins = 40,
            rewardCoins = 650,
            difficultyLevel = 4
        )
    )

    val challenges: List<ChallengeConfig> = listOf(
        ChallengeConfig(
            id = "c1_speed_blitz",
            titleBn = "৯০ সেকেন্ড স্পিড ব্লিটজ",
            subtitleBn = "সময়ের সাথে পাল্লা দিয়ে ছুটে চলুন!",
            ruleBn = "৯০ সেকেন্ডের মধ্যে ৬০০ মিটার দূরত্ব অতিক্রম করুন।",
            weather = WeatherType.DAY,
            timeLimitSeconds = 90,
            targetDistanceMeters = 600,
            maxHearts = 3,
            speedMultiplier = 1.22f,
            weakBrakes = false,
            rewardCoins = 280
        ),
        ChallengeConfig(
            id = "c2_no_brake_thrill",
            titleBn = "দুর্বল ব্রেক থ্রিল!",
            subtitleBn = "ব্রেক কিছুটা ঢিলা — সাবধানে সাইড কাটান!",
            ruleBn = "দুর্বল ব্রেকে ৬৫ সেকেন্ডের মধ্যে ৪৫০ মিটার চলুন।",
            weather = WeatherType.NIGHT,
            timeLimitSeconds = 65,
            targetDistanceMeters = 450,
            maxHearts = 3,
            speedMultiplier = 1.15f,
            weakBrakes = true,
            rewardCoins = 340
        ),
        ChallengeConfig(
            id = "c3_vip_zero_crash",
            titleBn = "ভিআইপি জিরো ক্র্যাশ",
            subtitleBn = "মাত্র ১টি লাইফ! কোনো ধাক্কা খাওয়া চলবে না!",
            ruleBn = "একবারও ধাক্কা না খেয়ে ৫০০ মিটার পথ চালান।",
            weather = WeatherType.DAY,
            timeLimitSeconds = 110,
            targetDistanceMeters = 500,
            maxHearts = 1,
            speedMultiplier = 1.1f,
            weakBrakes = false,
            rewardCoins = 420
        ),
        ChallengeConfig(
            id = "c4_monsoon_storm",
            titleBn = "বর্ষা-রাতের মহাঝড়",
            subtitleBn = "বৃষ্টি, অন্ধকার আর ব্যস্ত বাসের ভিড়!",
            ruleBn = "১০০ সেকেন্ডের মধ্যে ৭০০ মিটার পাড়ি দিন।",
            weather = WeatherType.RAIN,
            timeLimitSeconds = 100,
            targetDistanceMeters = 700,
            maxHearts = 3,
            speedMultiplier = 1.2f,
            weakBrakes = false,
            rewardCoins = 500
        )
    )

    fun upgradeCost(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 120
            2 -> 240
            3 -> 400
            4 -> 620
            else -> 0
        }
    }

    fun designById(id: String): RickshawDesign =
        rickshawDesigns.firstOrNull { it.id == id } ?: rickshawDesigns.first()

    fun canopyById(id: String): CanopyStyle =
        canopyStyles.firstOrNull { it.id == id } ?: canopyStyles.first()

    fun decorationById(id: String): DecorationStyle =
        decorationStyles.firstOrNull { it.id == id } ?: decorationStyles.first()
}
