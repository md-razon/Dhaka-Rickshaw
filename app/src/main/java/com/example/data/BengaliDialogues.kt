package com.example.data

import com.example.model.CharacterExpression
import com.example.model.DialogueCategory
import com.example.model.DialogueLine
import com.example.model.SpeakerType

object BengaliDialogues {

    private var nextId = 1
    private fun d(
        category: DialogueCategory,
        speaker: SpeakerType,
        textBn: String,
        expression: CharacterExpression = CharacterExpression.HAPPY
    ) = DialogueLine(nextId++, category, speaker, textBn, expression)

    val allLines: List<DialogueLine> = listOf(
        // 1. RANDOM_CHAT (16)
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "এই ঢাকার রাস্তায় রিকশা চালানো মানে যুদ্ধ করা!", CharacterExpression.SWEATING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "সকাল থেকে প্যাডেল মারছি, পায়ের পেশী তো হিরো আলম হয়ে গেল!", CharacterExpression.LAUGHING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.PASSENGER, "মামা, আপনার রিকশার ছবি আর ফুলগুলো তো দারুণ!", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "চা-সিঙ্গারা না খেলে আজ প্যাডেলে জোর পাচ্ছি না রে ভাই!", CharacterExpression.SWEATING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "রাস্তার পাশে মামার দোকানের ঝালমুড়ির ঘ্রাণ নাকে লাগছে!", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.PASSENGER, "মামা, প্রতিদিন কত কিলোমিটার চালান? আপনি তো জিমের চেয়েও ফিট!", CharacterExpression.LAUGHING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "জিম লাগে না মামা, দুইটা প্যাডেল আর ঢাকার ব্রিজই যথেষ্ট!", CharacterExpression.LAUGHING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "আমাদের রিকশার সিট তো সোফার চেয়েও আরামদায়ক, তাই না বস?", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.PASSENGER, "হ্যাঁ মামা, কিন্তু বাম্পার এলে তো আকাশে উড়ে যাচ্ছি!", CharacterExpression.SURPRISED),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "আরে ওইটা ফ্রি রোলার-কোস্টার রাইড, টিকেট লাগবে না!", CharacterExpression.LAUGHING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "শীতকাল হোক আর গরম, ঢাকার রিকশাই আসল রাজা!", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.PASSENGER, "মামা, এই গানটা কোথায় বাজছে? মনটা ভালো হয়ে গেল!", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "মনে মনে গাইতেছি মামা, খুশি থাকলে রিকশা নিজেই দৌড়ায়!", CharacterExpression.LAUGHING),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "আজকে ব্রেকটা একটু টাইট আছে, ভয়ের কোনো কারণ নাই!", CharacterExpression.HAPPY),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.PASSENGER, "মামা, আপনি কি গুগল ম্যাপ দেখেন, নাকি মুখস্থ?", CharacterExpression.SURPRISED),
        d(DialogueCategory.RANDOM_CHAT, SpeakerType.DRIVER, "গুগল ম্যাপ তো আমাদের গলি চেনেই না, আমার মাথাই আসল জিপিএস!", CharacterExpression.LAUGHING),

        // 2. PASSENGER_REQUEST (15)
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, একটু তাড়াতাড়ি যান! অফিসে বসের ঝাড়ি খাবো!", CharacterExpression.PANIC),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "ভাই, ক্লাস শুরু হয়ে যাবে, প্যাডেলে একটু ঝড় তোলেন!", CharacterExpression.SWEATING),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, সামনে ডানে না বামে? একটু সাবধানে চালান তো!", CharacterExpression.SURPRISED),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, আরাম করে যান, আমার কোনো তাড়া নাই আজ!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "এই যে মামা, দোকানের সামনে দিয়ে নিলে ভালো হয়, মিষ্টি কিনবো!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "ভাই, গর্ত বাঁচিয়ে চালান, হাতে কাঁচের বাটি আছে!", CharacterExpression.PANIC),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, হর্নটা বাজান না! সামনের লোক তো শুনতেই পাচ্ছে না!", CharacterExpression.ANNOYED),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "দ্রুত যান মামা, বিরিয়ানি ঠান্ডা হয়ে গেলে মজা থাকবে না!", CharacterExpression.SWEATING),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, শর্টকাট গলি ধরেন, বড় রাস্তায় গেলে আর আজকে ফেরা হবে না!", CharacterExpression.ANNOYED),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "একটু ছায়া দিয়ে চালান মামা, রোদে তো সেদ্ধ হয়ে গেলাম!", CharacterExpression.SWEATING),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, ট্রেন মিস হয়ে যাবে! রিকশা আজকে এক্সপ্রেস বানান!", CharacterExpression.PANIC),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "বস, একটু সোজা চালান, আমার চশমা নাক থেকে পড়ে যাচ্ছে!", CharacterExpression.SURPRISED),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, আরেকটু বামে চাপেন, পাশের বাসটা তো গায়ের উপর উঠছে!", CharacterExpression.PANIC),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "ভাই, তাড়াহুড়ো করে পড়িও না আবার, সময়মতো পৌঁছালেই চলবে!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_REQUEST, SpeakerType.PASSENGER, "মামা, চায়ের দোকানের সামনে দুই মিনিট দাঁড়াবেন নাকি?", CharacterExpression.LAUGHING),

        // 3. TRAFFIC_JOKE (15)
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "বাসের ড্রাইভার ভাবে পুরো রাস্তাই তার শ্বশুরবাড়ি!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "মোটরসাইকেলগুলো এমনভাবে ঢোকে যেন সুঁইয়ের ভেতর দিয়ে হাতি যাবে!", CharacterExpression.SURPRISED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.PASSENGER, "মামা, ওই প্রাইভেট কারের হর্ন শুনলে মনে হয় বিয়েবাড়ির ব্যান্ড!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "ঢাকায় সিগন্যাল সবুজ হওয়ার আগেই পেছন থেকে হর্ন বাজে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "ভাই, ভ্যানগাড়ি যখন মাঝরাস্তায় চলে, তখন মনে হয় স্লো-মোশন সিনেমা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.PASSENGER, "রাস্তায় গাড়ির চেয়ে হর্নের আওয়াজ বেশি জোরে ছোটে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "পাশের বাসটা এত বড়, মনে হচ্ছে চলন্ত দোতলা বাড়ি!", CharacterExpression.SURPRISED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "বাইক মামারা সাইড মিরর ভাঁজ করে রাখে, দেখে মনে হয় ডানা গুটিয়ে উড়বে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.PASSENGER, "মামা, এই রাস্তায় ট্রাফিক আইন হলো— যে আগে ঢুকতে পারে সেই রাজা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "পথচারীরা কানে হেডফোন দিয়ে এমনভাবে হাঁটে যেন বসার ঘরে হাঁটছে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "আমি ঘণ্টা বাজাই ক্রিং-ক্রিং, আর বাস বাজায় ভোঁ-ভোঁ! অসম যুদ্ধ!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.PASSENGER, "মামা, ওই লোক তো ডানদিকে হাত দেখিয়ে বামদিকে ঘুরলো!", CharacterExpression.SURPRISED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "ঢাকার ট্রাফিক হলো ধৈর্যের সবচেয়ে বড় পরীক্ষা, পাস করলে নোবেল!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.DRIVER, "ফাঁকা রাস্তা দেখলেই মনে হয় আজকে কোনো হরতাল নাকি স্বপ্ন দেখছি!", CharacterExpression.SURPRISED),
        d(DialogueCategory.TRAFFIC_JOKE, SpeakerType.PASSENGER, "মামা, কারটা তো চকচকে, কিন্তু ধুলা জমে সাদা হয়ে গেছে!", CharacterExpression.HAPPY),

        // 4. SPEED_JOKE (14)
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "মামা, আস্তে! রিকশা চালাই, বিমান না!", CharacterExpression.PANIC),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "ভাই, প্যাডেল আমার পায়ে, রকেট লাগানো নাই!", CharacterExpression.SWEATING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.PASSENGER, "ওরে বাবা! এই স্পিডে গেলে তো সোজা চাঁদে গিয়ে নামবো!", CharacterExpression.PANIC),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "হুডের বাতাস শুনে মনে হচ্ছে তুফান এক্সপ্রেস চালাচ্ছি!", CharacterExpression.LAUGHING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.PASSENGER, "মামা, লুঙ্গি সামলান! বাতাসে উড়ে যাচ্ছে কিন্তু!", CharacterExpression.LAUGHING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "এত জোরে প্যাডেল ঘুরছে যে চেনই বলে উঠছে— ভাই মাফ কর!", CharacterExpression.SWEATING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.PASSENGER, "আপনি কি ট্যুর ডি ফ্রান্সে যাওয়ার প্র্যাকটিস করছেন নাকি মামা?", CharacterExpression.SURPRISED),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "ট্যুর ডি ফ্রান্স কী জিনিস? আমাদের এটা হলো ট্যুর ডি ঢাকা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "চাকা গরম হয়ে আগুন ধরে যাবে নাকি! একটু দম নেই!", CharacterExpression.SWEATING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.PASSENGER, "মামা, এই গতিতে ব্রেক ধরলে আমরা দুজনেই সুপারম্যান হয়ে উড়বো!", CharacterExpression.PANIC),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "হাওয়ার বেগে ছুটছি আজ! পেছনের মোটরসাইকেলও লজ্জা পাচ্ছে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "স্পিড মিটার কাঁপছে! পা দুটো এখন টার্বো ইঞ্জিন!", CharacterExpression.HAPPY),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.PASSENGER, "আস্তে মামা আস্তে, আমার কলিজা হাতে চলে আসছে!", CharacterExpression.PANIC),
        d(DialogueCategory.SPEED_JOKE, SpeakerType.DRIVER, "ভয় নাই মামা, ঢাকা রিকশার স্ট্যান্ডার্ড ব্যালেন্স শতভাগ পাকা!", CharacterExpression.HAPPY),

        // 5. COIN_REACTION (14)
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "আজকে কয়েন ভালোই উঠতেছে, চায়ের টাকা হয়ে যাবে!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "ঝনঝন কয়েন! পকেটে এখন রাজকীয় আওয়াজ!", CharacterExpression.LAUGHING),
        d(DialogueCategory.COIN_REACTION, SpeakerType.PASSENGER, "বাহ মামা! আপনি তো রাস্তার কয়েন চুম্বকের মতো টেনে নিচ্ছেন!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "একটা, দুইটা, তিনটা— আজকে তো ডাবল বিরিয়ানি পাক্কা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "নতুন রিকশার রঙ কেনার টাকা জমে যাচ্ছে!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.PASSENGER, "এই কয়েনগুলো কি কেউ রাস্তা দিয়ে ছড়িয়ে দিয়েছে নাকি মামা?", CharacterExpression.SURPRISED),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "সবই পরিশ্রমের ফল মামা! চোখ আর হ্যান্ডেল ঠিক থাকলে লক্ষ্মী আসে!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "আহ! সোনালী কয়েন দেখলেই প্যাডেলের জোর দ্বিগুণ হয়ে যায়!", CharacterExpression.LAUGHING),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "আজকে ব্রেক আর টায়ার আপগ্রেড করেই ছাড়বো!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.PASSENGER, "মামা, কয়েন তুলতে গিয়ে আবার বাসের দিকে তাকানো ভুলবেন না!", CharacterExpression.SURPRISED),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "এক কয়েনে এক কাপ দুধ-চা, দশ কয়েনে জমজমাট নাস্তা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "বাহ বাহ! কয়েনের বৃষ্টি হচ্ছে মনে হয়!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.DRIVER, "বাড়িতে বাচ্চার জন্য আজকে জিলাপি নিয়ে যাবো এই টাকায়!", CharacterExpression.HAPPY),
        d(DialogueCategory.COIN_REACTION, SpeakerType.PASSENGER, "দারুণ কাট মারলেন মামা, একদম লাইনে সব কয়েন পেলেন!", CharacterExpression.HAPPY),

        // 6. ACCIDENT_REACTION (14)
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "ওরে বাবা! অল্পের জন্য চাকা বেঁচে গেল!", CharacterExpression.PANIC),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.PASSENGER, "হায় আল্লাহ! আমার কোমর গেল রে মামা!", CharacterExpression.PANIC),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "আরে ভাই, চোখ কি পকেটে রেখে গাড়ি চালান নাকি?!", CharacterExpression.ANNOYED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "ধাক্কা খেয়ে রিকশার ঘণ্টাটাই টাল হয়ে গেল!", CharacterExpression.SURPRISED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.PASSENGER, "মামা, হাত-পা ঠিক আছে তো? একটু দেখে চালান!", CharacterExpression.PANIC),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "কিসের সাথে লাগলো রে! হ্যান্ডেল তো কাঁপছে থরথর করে!", CharacterExpression.SURPRISED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "ধাক্কা লাগার পরও রিকশা দাঁড়িয়ে আছে— একেই বলে দেশি লোহা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.PASSENGER, "উফ! আরেকটু হলে তো রাস্তা মাপতে হতো!", CharacterExpression.PANIC),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "ক্ষমা করেন মামা, সামনের গাড়ি হঠাৎ ব্রেক মারছে!", CharacterExpression.SWEATING),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "রং-চটা উঠে গেল নাকি? গ্যারেজে গিয়ে পালিশ করতে হবে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.PASSENGER, "মামা, এটা কিন্তু ভিডিও গেম না— আসল রাস্তা ভাবেন!", CharacterExpression.ANNOYED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "মাফ করবেন ভাই, এইরকম আর হবে না— চোখ এখন ১০০% রাস্তায়!", CharacterExpression.SWEATING),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.DRIVER, "ইশ! নতুন মালা থেকে দুটো ফুল পড়ে গেল ধাক্কায়!", CharacterExpression.SURPRISED),
        d(DialogueCategory.ACCIDENT_REACTION, SpeakerType.PASSENGER, "জানটা বেঁচে গেল ভাই, ধীরে সুস্থে আগান!", CharacterExpression.PANIC),

        // 7. PASSENGER_PICKUP (14)
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "উঠে পড়েন মামা! এসি নাই, তবে প্রাকৃতিক হাওয়া ফ্রি!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "মামা, যাবেন তো? ভাড়া কিন্তু ন্যায্য দেবো!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "আসেন আসেন, রিকশা একদম ঝকঝকে, বসেন আরাম করে!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "অবশেষে একটা খালি রিকশা পেলাম! ধন্যবাদ মামা!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "ব্যাগটা সাবধানে রাখেন, আমরা এখন উড়াল দেবো!", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "মামা, ছাউনিটা একটু তুলে দেন না, রোদ লাগছে!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "এই তো তুলে দিলাম! যাত্রা শুরু, বিসমিল্লাহ!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "রিকশায় না উঠলে ঢাকা শহরের মজা বোঝা যায় না!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "শক্ত হয়ে বসেন ভাই, রাস্তায় আজ বেশ খেলা আছে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "মামা, ভাড়া নিয়ে পরে ঝগড়া করবেন না তো?", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "আরে না না, খুশি মনে যা দেবেন তাতেই আমি রাজি!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "বাহ, ভেতরের গদি তো বেশ নরম মামা!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.DRIVER, "স্বাগতম ঢাকা রিকশা এয়ারলাইন্সে! সিট বেল্ট লাগবে না!", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_PICKUP, SpeakerType.PASSENGER, "চলেন মামা, যত তাড়াতাড়ি পারা যায় গন্তব্যে পৌঁছাই!", CharacterExpression.HAPPY),

        // 8. PASSENGER_DELIVERY (14)
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "অসংখ্য ধন্যবাদ মামা! এই নেন ভাড়ার সাথে বকশিশ!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "আসসালামু আলাইকুম! আবার রিকশা লাগলে ডাক দেবেন!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "দারুণ চালিয়েছেন মামা, ফাইভ-স্টার রেটিং দিলাম!", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "ব্যাগ-ছাতা সব নামিয়েছেন তো? কিছু ফেলে যাবেন না যেন!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "সময়ের আগেই পৌঁছে গেলাম! আপনি তো সেরা রিকশাওয়ালা!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "ট্রিপ কমপ্লিট! পকেটে কয়েন ঢুকলো খটাখট!", CharacterExpression.LAUGHING),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "এই নেন এক্সট্রা টিপস— গরম চা খাবেন!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "দোয়া করবেন মামা, আপনার মতো যাত্রী যেন রোজ পাই!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "বাঁচা গেল, ঠিক টাইমে পৌঁছেছি! ভালো থাকবেন ভাই!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "একজন নামলো, এবার পরের যাত্রীর খোঁজে চোখ রাখি!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "এমন আরামের রাইড অনেকদিন পর পেলাম!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "আপনার হাসিটাই তো আমাদের আসল পাওনা মামা!", CharacterExpression.HAPPY),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.PASSENGER, "মামা, মোবাইলটা হাতে আছে তো— হ্যাঁ পেয়েছি, চলি!", CharacterExpression.SURPRISED),
        d(DialogueCategory.PASSENGER_DELIVERY, SpeakerType.DRIVER, "যাক, নির্বিঘ্নে নামিয়ে দিলাম! এবার নেক্সট স্টপ!", CharacterExpression.LAUGHING),

        // 9. MISSION_COMPLETE (14)
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "মিশন সফল! আজকে তো মহল্লায় মিষ্টি বিতরণ হবে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "সাবাশ! ঢাকা রিকশার চ্যাম্পিয়ন এখন আমিই!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.PASSENGER, "দারুণ কীর্তি মামা! পুরো ঢাকা আপনাকে চিনে গেল!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "লক্ষ্য পূরণ! গ্যারেজে গিয়ে এখন রিকশায় নতুন সাজ লাগাবো!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "পরিশ্রম আর হ্যান্ডেলিং ঠিক থাকলে কোনো মিশনই কঠিন না!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "বস! মিশনের সব কয়েন ঝুলিতে চলে এসেছে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.PASSENGER, "অসাধারণ ড্রাইভিং! এমন দক্ষ রিকশাচালক বিরল!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "আজকের টার্গেট শেষ, তবে রাস্তা এখনও ডাকছে!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "আহা! পুরস্কারের কয়েন দেখে মনটা নাচছে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "একদম নিখুঁত ফিনিশ! প্যাডেলের রাজা ফিরে এসেছে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.PASSENGER, "জয় বাংলা রিকশা! দারুণ জিতলেন মামা!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "গামছা উড়িয়ে জয় উদযাপন! পরের মিশনে চলি!", CharacterExpression.LAUGHING),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "চাকা থামে নাই, মনোবল ভাঙে নাই— মিশন ডান!", CharacterExpression.HAPPY),
        d(DialogueCategory.MISSION_COMPLETE, SpeakerType.DRIVER, "বকশিশ আর মিশন বোনাস— দুটোই আজ পকেটে!", CharacterExpression.LAUGHING),

        // 10. WEATHER_JOKE (14)
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "ঝমঝম বৃষ্টি! পলিথিন ছাড়া হুডের নিচেও ছাঁট আসছে!", CharacterExpression.SWEATING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.PASSENGER, "মামা, এই বৃষ্টিতে রিকশা না, নৌকা চালালে ভালো হতো!", CharacterExpression.LAUGHING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "ঢাকার রাস্তায় বৃষ্টি মানেই অর্ধেক নদী, অর্ধেক খাল!", CharacterExpression.LAUGHING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.PASSENGER, "মামা, খিচুড়ি আর ইলিশ ভাজার কথা মনে পড়ে যাচ্ছে এই বৃষ্টিতে!", CharacterExpression.HAPPY),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "বৃষ্টি এলে চাকা পিছলায়, তাই ব্রেক একটু আগে থেকেই ধরতে হবে!", CharacterExpression.SWEATING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "রাতের ঢাকায় স্ট্রিটলাইটের আলোয় রিকশা দেখতে কী সুন্দর লাগে!", CharacterExpression.HAPPY),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.PASSENGER, "রাতে বাতাসটা বেশ ঠান্ডা মামা, চালাইতে মজা লাগছে!", CharacterExpression.HAPPY),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "সামনের হেডলাইট না থাকলে রাতের অন্ধকারে গর্ত দেখাই যায় না!", CharacterExpression.SURPRISED),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.PASSENGER, "বজ্রপাতের শব্দে তো একটু চমকে উঠলাম মামা!", CharacterExpression.PANIC),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "ভয় নাই, মেঘের গর্জন শুনে রিকশা আরও জোরে দৌড়ায়!", CharacterExpression.LAUGHING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "রোদের তাপে গামছা ভিজিয়ে মাথায় দিয়ে নিলাম, এখন আর গরম নাই!", CharacterExpression.SWEATING),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.PASSENGER, "মামা, জমে থাকা পানিতে চাকা ডুবিয়ে স্প্ল্যাশ করবেন না যেন!", CharacterExpression.PANIC),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "রাতের কুয়াশায় চায়ের ধোঁয়া আর রিকশার ঘণ্টা— দারুণ জুটি!", CharacterExpression.HAPPY),
        d(DialogueCategory.WEATHER_JOKE, SpeakerType.DRIVER, "বৃষ্টি থামলেই আকাশ পরিষ্কার হবে, আমরা থামছি না!", CharacterExpression.HAPPY),

        // 11. TRAFFIC_JAM (14)
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "আরে সামনে তো জ্যাম! ঘুমায়ে পড়বো নাকি এখানেই?!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.PASSENGER, "মামা, এই জ্যামে তো একটা উপন্যাস পড়ে শেষ করা যাবে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "রাস্তা পুরো জাম! পায়ে হেঁটে গেলে মনে হয় আগে পৌঁছাতাম!", CharacterExpression.SWEATING),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "ক্রিং ক্রিং! ভাই একটু সাইড দেন, ছোট্ট রিকশাটা বেরিয়ে যাক!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.PASSENGER, "মামা, ঘড়ি দেখেন— দশ মিনিট ধরে একই দোকানের সামনে!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "রিকশার সাইজ ছোট বলেই তো ফাঁকফোকর দিয়ে গলে যেতে পারি!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "বাস দুইটা পাশাপাশি দাঁড়ালে পুরো রাস্তা ব্লক হয়ে যায়!", CharacterExpression.ANNOYED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.PASSENGER, "জ্যামে বসে বাদাম ভাজা খেলে কেমন হয় মামা?", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "একদম! জ্যাম না ছাড়লে চা-বাদামের পার্টি শুরু করি!", CharacterExpression.LAUGHING),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "সামনের গাড়ি নড়লেই আমরা রকেট গতিতে বের হবো!", CharacterExpression.HAPPY),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.PASSENGER, "আরে! ওই বাইকটা তো ফুটপাথে উঠে পড়লো!", CharacterExpression.SURPRISED),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "আমরা ভদ্র মানুষ, ফুটপাথে উঠবো না— রাস্তায় রাস্তা বানাবো!", CharacterExpression.HAPPY),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.DRIVER, "ঘণ্টা বাজাতে বাজাতে আঙুল ব্যথা হয়ে গেল, তবু জ্যাম সরে না!", CharacterExpression.SWEATING),
        d(DialogueCategory.TRAFFIC_JAM, SpeakerType.PASSENGER, "ধৈর্য ধরেন মামা, জ্যাম খুললেই আবার ফুল স্পিড!", CharacterExpression.HAPPY),

        // 12. UNEXPECTED_EVENT (14)
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "আরে! রাস্তার মাঝখানে বিড়াল বসে আছে— ব্রেক ধরো!", CharacterExpression.SURPRISED),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "হায় হায়! এক পাটির স্যান্ডেল তো প্যাডেল থেকে খুলে যাচ্ছিল!", CharacterExpression.PANIC),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.PASSENGER, "মামা, পাশের বাস থেকে কে যেন হাত নাড়ছে— আপনার চেনা নাকি?", CharacterExpression.SURPRISED),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "হ্যাঁ! ওইটা আমার মামাতো ভাইয়ের শ্যালকের রিকশা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "ওরে! বাতাসে কার কাগজের ঠোঙা উড়ে এসে গায়ে পড়লো!", CharacterExpression.SURPRISED),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.PASSENGER, "মামা, পেছন থেকে কাক ডেকে যাচ্ছে— মাথায় বসবে নাকি!", CharacterExpression.PANIC),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "কাকটাও মনে হয় ফ্রি রাইড নিতে চায়! হা হা!", CharacterExpression.LAUGHING),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "হঠাৎ চেন একটু ঢিলা মনে হচ্ছে— ওহ, ঠিক আছে, সব মজবুত!", CharacterExpression.SWEATING),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.PASSENGER, "মামা, কেউ একজন ঝুড়ি ভর্তি আম নিয়ে যাচ্ছে— গন্ধে পাগল হলাম!", CharacterExpression.HAPPY),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "রাস্তার পাশে বরফওয়ালা ঘণ্টি বাজাচ্ছে, ছোটবেলার কথা মনে পড়ে গেল!", CharacterExpression.HAPPY),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "আরে! ঘণ্টার নাট ঢিলা হয়ে এমন আওয়াজ করলো যেন ডিজে বাজে!", CharacterExpression.LAUGHING),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.PASSENGER, "মামা, আমার ছাতাটা প্রায় উড়ে যাচ্ছিল— ধরে ফেলছি!", CharacterExpression.SURPRISED),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "হঠাৎ বাঁশি বাজলো! ওহ, ওইটা ট্রাফিক পুলিশের হুইসেল, ভয় নাই!", CharacterExpression.SURPRISED),
        d(DialogueCategory.UNEXPECTED_EVENT, SpeakerType.DRIVER, "রাস্তার মাঝে কলার খোসা?! মারিও কার্ট খেলতেছি নাকি ঢাকায়!", CharacterExpression.LAUGHING)
    )
}

class DialogueSelector {
    private val recentIds = ArrayDeque<Int>(40)
    private val maxHistory = 36

    fun pick(
        category: DialogueCategory,
        hasPassenger: Boolean
    ): DialogueLine {
        val candidates = BengaliDialogues.allLines.filter { line ->
            line.category == category && (hasPassenger || line.speaker == SpeakerType.DRIVER)
        }.ifEmpty {
            BengaliDialogues.allLines.filter { it.category == category }
        }.ifEmpty {
            BengaliDialogues.allLines
        }

        val fresh = candidates.filter { it.id !in recentIds }
        val pool = fresh.ifEmpty { candidates }
        val chosen = pool.random()

        recentIds.addLast(chosen.id)
        while (recentIds.size > maxHistory) {
            recentIds.removeFirst()
        }
        return chosen
    }
}
