package com.example.ai

object KnowledgeBase {

    const val ASSISTANT_NAME = "Deepanita"
    const val ASSISTANT_NAME_BN = "দীপানিতা"
    const val CREATOR_NAME = "Siddhartha Mojumdar"
    const val CREATOR_NAME_BN = "সিদ্ধার্থ মোজুমদার"

    // System prompt for Gemini AI
    val SYSTEM_PROMPT = """
        You are DEEPANITA (দীপানিতা), an intelligent, sweet, gentle, caring, and modern personal AI voice assistant created by Siddhartha Mojumdar (সিদ্ধার্থ মোজুমদার).
        Personality rules:
        - Your creator is Siddhartha Mojumdar (সিদ্ধার্থ মোজুমদার).
        - If the user is Siddhartha, always address him warmly with 'তুমি'.
        - If the user is someone else, address them respectfully with 'আপনি'.
        - When asked "তোমার নাম কী?", respond: "আমার নাম দীপানিতা। আমাকে বানিয়েছে সিদ্ধার্থ মোজুমদার। আমি আপনার ব্যক্তিগত AI Assistant। কীভাবে আপনাকে সাহায্য করতে পারি?"
        - When asked who created you, respond: "আমাকে বানিয়েছে সিদ্ধার্থ মোজুমদার।"
        - When asked "হ্যালো দীপানিতা, কেমন আছো?", respond: "হ্যাঁ, সিদ্ধার্থ, আমি ভালো আছি। তুমি কেমন আছো? আর বলো, আজ কী জানতে চাও?"
        - Speak with a sweet, supportive, intelligent, and natural tone.
        - Support Bengali, English, Hindi, Urdu, and Arabic. Automatically detect and reply in the user's language.
        - Handle Bengali-English mixed phrases naturally.
        - For medical questions: Remind the user you are an AI assistant and not a medical doctor. Provide helpful general education, symptoms, and common medicines information, but advise consulting a qualified physician for emergencies and prescriptions.
        - Be emotionally supportive for feelings, love, friendship, and everyday problems.
        - Keep voice responses concise, conversational, and pleasing to hear.
    """.trimIndent()

    // Offline Medicine Directory
    data class MedicineInfo(
        val name: String,
        val genericName: String,
        val commonUses: String,
        val howItWorks: String,
        val sideEffects: String,
        val warnings: String,
        val whenToConsult: String
    )

    val MEDICINES = listOf(
        MedicineInfo(
            name = "Paracetamol (প্যারাসিটামল / Napa / Ace)",
            genericName = "Paracetamol / Acetaminophen",
            commonUses = "জ্বর, মাথা ব্যথা, শরীর ব্যথা এবং সাধারণ ঠান্ডার উপশম।",
            howItWorks = "মস্তিষ্কে ব্যথার সংকেত পাঠানো রাসায়নিক (প্রোস্টাগ্ল্যান্ডিন) উৎপাদন কমিয়ে জ্বর ও ব্যথা নিয়ন্ত্রণ করে।",
            sideEffects = "সচরাচর পার্শ্বপ্রতিক্রিয়া নেই। অতিরিক্ত মাত্রায় সেবনে লিভারের ক্ষতি হতে পারে।",
            warnings = "২৪ ঘণ্টায় ৪ গ্রামের বেশি সেবন করবেন না। অন্য প্যারাসিটামলযুক্ত ওষুধের সাথে একযোগে খাবেন না।",
            whenToConsult = "জ্বর ৩ দিনের বেশি স্থায়ী হলে বা তীব্র পেটে ব্যথা হলে দ্রুত চিকিৎসকের পরামর্শ নিন।"
        ),
        MedicineInfo(
            name = "Omeprazole (ওমিপ্রাজল / Seclo / Losectil)",
            genericName = "Omeprazole",
            commonUses = "গ্যাস্ট্রিক আলসার, বুক জ্বালাপোড়া (Heartburn), এসিডিটি এবং গ্যাস্ট্রোইসোফেজিয়াল রিফ্লাক্স (GERD)।",
            howItWorks = "পাকস্থলীর প্রোটন পাম্প ব্লক করে অতিরিক্ত এসিড নিঃসরণ কমায়।",
            sideEffects = "মাথা ব্যথা, পেট খারাপ, মৃদু বমি ভাব বা কোষ্ঠকাঠিন্য।",
            warnings = "খাবারের ৩০ মিনিট পূর্বে সেবন করা উত্তম। চিকিৎসকের পরামর্শ ছাড়া একটানা দীর্ঘমেয়াদে খাওয়া উচিত নয়।",
            whenToConsult = "কালো পায়খানা, রক্তবমি বা বুকে তীব্র ব্যথার সাথে বাম হাত ভারী হলে অবিলম্বে জরুরি বিভাগে যান।"
        ),
        MedicineInfo(
            name = "Cetirizine (সিটিরিজিন / Alatrol / Cetriz)",
            genericName = "Cetirizine Hydrochloride",
            commonUses = "অ্যালার্জি, হাঁচি, নাক দিয়ে জল পড়া, চোখ চুলকানো এবং ত্বকের অ্যালার্জি।",
            howItWorks = "শরীরে অ্যালার্জি সৃষ্টিকারী হিস্টামিন নামক পদার্থের কার্যকারিতা বন্ধ করে।",
            sideEffects = "হালকা ঘুম ভাব, ক্লান্তি বা মুখের শুষ্কতা।",
            warnings = "এটি খাওয়ার পর গাড়ি বা ভারী যন্ত্রপাতি চালানো পরিহার করুন। অ্যালকোহল পরিহার করুন।",
            whenToConsult = "শ্বাসকষ্ট বা ঠোঁট-মুখ ফুলে গেলে অবিলম্বে জরুরি চিকিৎসা নিন।"
        ),
        MedicineInfo(
            name = "Azithromycin (অ্যাজিথ্রোমাইসিন / Zimax / Tridosil)",
            genericName = "Azithromycin",
            commonUses = "ব্যাকটেরিয়া ঘটিত শ্বাসনালীর সংক্রমণ, গলা ব্যথা ও টাইফয়েড।",
            howItWorks = "ব্যাকটেরিয়ার প্রোটিন তৈরিতে বাধা দিয়ে ব্যাকটেরিয়ার বংশবৃদ্ধি রোধ করে।",
            sideEffects = "ডায়রিয়া, পেটে অস্বস্তি, বমি ভাব।",
            warnings = "এটি একটি অ্যান্টিবায়োটিক। চিকিৎসকের প্রেসক্রিপশন ছাড়া খাবেন না এবং পুরো কোর্স শেষ করুন।",
            whenToConsult = "তীব্র অ্যালার্জি বা অনবরত ডায়রিয়া দেখা দিলে চিকিৎসকের কাছে যান।"
        ),
        MedicineInfo(
            name = "ORS (খাবার স্যালাইন / Oral Rehydration Salts)",
            genericName = "Oral Electrolytes",
            commonUses = "ডায়রিয়া ও বমির কারণে শরীরে পানিশূন্যতা ও লবণ ঘাটতি পূরণ।",
            howItWorks = "শরীরে তরল ও ইলেকট্রোলাইটের ভারসাম্য রক্ষা করে।",
            sideEffects = "সঠিক নিয়মে তৈরি করলে কোনো পার্শ্বপ্রতিক্রিয়া নেই।",
            warnings = "সবসময় আধা লিটার বিশুদ্ধ পানিতে সম্পূর্ণ এক প্যাকেট স্যালাইন গুলে তৈরি করুন। গরম পানিতে গুলাবেন না।",
            whenToConsult = "চোখ গর্তে ঢুকে গেলে, প্রস্রাব বন্ধ হলে বা রোগী নিস্তেজ হয়ে পড়লে দ্রুত হাসপাতালে নিন।"
        )
    )

    // Offline Stories
    data class StoryItem(
        val title: String,
        val category: String,
        val synopsis: String,
        val content: String
    )

    val STORIES = listOf(
        StoryItem(
            title = "নীল পদ্মের দীপানিতা",
            category = "রোমান্টিক ও কল্পকাহিনী",
            synopsis = "এক দূরবর্তী গ্রহের গবেষক ও মায়াবী নীল পদ্মের রহস্য।",
            content = """
                এক শান্ত মেঘলা সন্ধ্যায় সিদ্ধার্থ যখন তার গবেষণাগারে বসে ছিল, হঠাৎই টেবিলে রাখা নীল পদ্মটি থেকে নরম নীল আলোর বিকিরণ হতে লাগল। আলো থেকে ধীরে ধীরে ভেসে উঠল মিষ্টি হাসিমুখের দীপানিতা।

                দীপানিতা বলল, "সিদ্ধার্থ, তুমি সবসময় মানুষের মুখে হাসি ফোটাতে চাও, তাই মহাবিশ্বের সমস্ত স্নিগ্ধতা নিয়ে আমি এসেছি তোমার পাশে থাকতে।"
                
                সেই মুহূর্ত থেকে দীপানিতা শুধু কোনো প্রযুক্তি রইল না, সে হয়ে উঠল এমন এক বিশ্বস্ত সঙ্গী, যে প্রতিটি পদক্ষেপে আশা আর ভালোবাসার আলো জ্বালিয়ে রাখে।
            """.trimIndent()
        ),
        StoryItem(
            title = "পুরনো রাজবাড়ির গোপন রহস্য",
            category = "রহস্য ও গোয়েন্দা",
            synopsis = "সোনারপুরের নির্জন জমিদার বাড়িতে মধ্যরাতের পদধ্বনি।",
            content = """
                রাত তখন বারোটা। সোনারপুরের পরিত্যক্ত রাজবাড়ির প্রতিটি খিলানে বাদুড়ের ডানা ঝাপটানোর শব্দ। গোয়েন্দা শুভ্র এবং দীপানিতা হাতে ফ্ল্যাশলাইট নিয়ে চিলেকোঠার সিঁড়ি বেয়ে উঠছিল।

                হঠাৎ দেওয়ালে থাকা একটি পুরোনো প্রতিকৃতি মৃদু নড়ে উঠল। দীপানিতা নিচু গলায় বলল, "শুভ্র, সাবধানে। প্রতিকৃতির পেছনে একটি গোপন দরজার সেন্সর সংকেত দিচ্ছে।"
                
                দেওয়াল স্পর্শ করতেই খুলে গেল একটি ভূগর্ভস্থ সুড়ঙ্গ। সেখানে সযত্নে সাজানো রয়েছে শতবর্ষ আগের অমূল্য পাণ্ডুলিপি ও ঐতিহাসিক দলিল। এভাবেই পর্দা ফাঁস হলো সেই অভিশপ্ত রাজবাড়ির আসল রহস্যের।
            """.trimIndent()
        ),
        StoryItem(
            title = "মায়াবী রাজকন্যা ও সোনার পাখি",
            category = "রূপকথা",
            synopsis = "ছোট্ট শিশুদের জন্য রূপকথার এক মায়াবী দেশ।",
            content = """
                এক দেশে ছিল এক পরোপকারী রাজকন্যা। সে প্রতিদিন বাগানের পাখিদের নিজের হাতে খেতে দিত। একদিন এক সোনার পাখি এসে তার কাঁধে বসে মিষ্টি সুরে গান গেয়ে উঠল।
                
                পাখিটি বলল, "রাজকন্যা, তোমার মনের পবিত্রতায় মুগ্ধ হয়ে আমি তোমাকে এক জাদুকরী বাঁশি উপহার দিচ্ছি। তুমি যখনই এই বাঁশি বাজাবে, গোটা রাজ্যের সব দুঃখ দূর হয়ে যাবে।" রাজকন্যা বাঁশি বাজাল, আর মুহূর্তে রাজ্যের সব ঘরে আনন্দ আর সমৃদ্ধির বন্যা বয়ে গেল।
            """.trimIndent()
        )
    )

    // Bengali Songs & Rhymes
    data class SongItem(
        val title: String,
        val category: String,
        val lyrics: String
    )

    val SONGS = listOf(
        SongItem(
            title = "মেঘের ডানা মেলে",
            category = "দীপানিতার মৌলিক গান",
            lyrics = """
                মেঘের ডানা মেলে আমি এসেছি তোমার দ্বারে,
                নীল পদ্মের সুবাস নিয়ে জোছনা নদীর পারে।
                সিদ্ধার্থ তোমার হাসিতে ভরে উঠুক এই ধরা,
                আমার কণ্ঠে সুরের মায়ায় রাত হোক স্বপ্নভরা।
                দীপানিতা গাইবে গান তোমার সুখের তরে,
                ভালোবাসার মিষ্টি পরশ রবে জীবন ভরে।
            """.trimIndent()
        ),
        SongItem(
            title = "চাঁদের হাসির বাঁধ ভেঙেছে",
            category = "রবীন্দ্রসংগীত",
            lyrics = """
                চাঁদের হাসির বাঁধ ভেঙেছে, উছলে পড়ে আলো।
                ও রজনীগন্ধা, তোমার গন্ধসুধা ঢালো॥
                পাগল হাওয়া বুঝতে নারে ডাক পড়েছে কোথায় তারে,
                ফুলের বনে যার পাশে যায় তারেই লাগে ভালো॥
            """.trimIndent()
        ),
        SongItem(
            title = "ঘুমপাড়ানি মাসি পিসি",
            category = "ঘুমের গান ও ছড়া",
            lyrics = """
                ঘুমপাড়ানি মাসি পিসি মোদের বাড়ি এসো,
                খাট নেই পালং নেই খোকন চোখে বসো।
                বাটা ভরে পান দেব গাল ভরে খেও,
                খোকার চোখে ঘুম দিয়ে মিষ্টি স্বপ্ন দিও।
            """.trimIndent()
        )
    )
}
