package com.example.smriti.service

import com.example.smriti.model.LanguageItem

/**
 * AI Multilingual Voice Guidance, Screen Narration & Voice Intent Parser.
 * Supports 5 languages:
 *  1. English (en)
 *  2. Hindi (hi)
 *  3. Assamese (as)
 *  4. Mizo (mzo)
 *  5. Khasi (kha)
 */
object VoiceAssistantService {

    val SUPPORTED_LANGUAGES = listOf(
        LanguageItem("en", "English", "English", "🇬🇧", "Global English guidance"),
        LanguageItem("hi", "Hindi", "हिन्दी", "🇮🇳", "राष्ट्रीय भाषा मार्गदर्शन"),
        LanguageItem("as", "Assamese", "অসমীয়া", "🌾", "অসমীয়া ভাষাত নিৰ্দেশনা"),
        LanguageItem("mzo", "Mizo", "Mizo ṭawng", "🏔️", "Mizo tawngakaihhruaina"),
        LanguageItem("kha", "Khasi", "Ka Ktien Khasi", "🌲", "Jingkyntiew ha ka ktien Khasi")
    )

    private val SCREEN_GUIDANCE_PROMPTS = mapOf(
        "home" to mapOf(
            "en" to "Welcome to Smriti! Tap any brain activity to practice gently, or tap the microphone anytime to talk to me.",
            "hi" to "स्मृति में आपका स्वागत है! अपनी दैनिक दिमागी कसरत शुरू करने के लिए बड़े हरे बटन को दबाएं, या मुझसे बात करने के लिए माइक दबाएं।",
            "as" to "স্মৃতিলৈ আপোনাক স্বাগতম! আপোনাৰ দৈনিক মগজুৰ অনুশীলন আৰম্ভ কৰিবলৈ ডাঙৰ সেউজীয়া বুটামটো টিপক, নতুবা কথা ক'বলৈ মাইক্ৰ'ফ'নত টিপক।",
            "mzo" to "Smriti-ah lo lawm rawh le! Ni tin hriatna tihhmasawnna infiamna tan turin a hring lian hmet rawh, a nih loh chuan mi biak turin mic hmet rawh.",
            "kha" to "Pdiang burom sha Smriti! Pynkiat iaphi ban pynkyntiew iaka jingmut da kaba kynton iaka button haing, lada kwah ban kren kynton iaka microphone."
        ),
        "gameplay" to mapOf(
            "en" to "Take your time. Gently tap cards to flip them and find matching pairs. There is no rush at all.",
            "hi" to "आराम से खेलें। दो कार्ड्स को मिलाकर जोड़ी खोजें। कोई जल्दी नहीं है, आराम से खेलें।",
            "as" to "ধৈৰ্য্যেৰে খেলক। দুখন কাৰ্ড উলটাই মিলা জোৰা বিচাৰি উলিয়াওক। কোনো খৰখেদা নাই।",
            "mzo" to "Hmanhmawh duh suh. Card pahnih hmet la, a inhnim hnai tur zawn chhuah tum rawh.",
            "kha" to "Wat pynhap jingmut. Kynton ia ar ki card ban wad ia kiba iadei. Ym don jingkloi stet."
        ),
        "reminders" to mapOf(
            "en" to "Here is your care schedule. Please take your morning medication with water, then mark it complete.",
            "hi" to "यह आपकी देखभाल का समय है। कृपया अपनी सुबह की दवा पानी के साथ लें, फिर 'पूरा हुआ' पर टैप करें।",
            "as" to "এইয়া আপোনাৰ যত্নৰ সময়সূচী। পুৱাৰ ঔষধ পানীৰ সৈতে লওক, তাৰ পিছত সম্পূৰ্ণ চিন দিয়ক।",
            "mzo" to "Hian i damdawi ei hun a awm e. Khawngaihin i tukthuan damdawi ei la, completed tih hmet rawh.",
            "kha" to "Hane ka por dih dawai jong phi. Sngewbha dih ia ka dawai mynta step, nangta kynton completed."
        ),
        "caregiver" to mapOf(
            "en" to "Caregiver Overview: Track real-time cognitive engagement scores, activity history, and safety status.",
            "hi" to "केयरगिवर डैशबोर्ड: मरीज के संज्ञानात्मक स्कोर, अभ्यास इतिहास और सुरक्षा स्थिति की समीक्षा करें।",
            "as" to "তত্ত্বাৱধানকাৰী ডেশ্বব'ৰ্ড: ৰোগীৰ স্মৃতি স্ক'ৰ, অনুশীলন ইতিহাস আৰু সুৰক্ষা অৱস্থা চাওক।",
            "mzo" to "Enkawltu Dashboard: Hriatrengna dinhmun leh venhimna thlirletna.",
            "kha" to "Dashboard jong u nongsumar: Peit ia ka jinglong jingman jong uba pang bad ka shongsuk."
        ),
        "gps" to mapOf(
            "en" to "GPS Safe-Zone Sentinel: Monitoring live perimeter and radar status for continuous safety.",
            "hi" to "जीपीएस सेफ-ज़ोन सेंटिनल: सुरक्षित दायरे और स्थान की निगरानी सक्रिय है।",
            "as" to "জি পি এছ সুৰক্ষা সেন্টিনেল: সুৰক্ষিত পৰিসীমা আৰু স্থান পৰ্যবেক্ষণ চলি আছে।",
            "mzo" to "GPS Himna Sentinel: Him taka awmna hmun leh dinhmun thlithlai mek a ni.",
            "kha" to "GPS Jingiada: Peit thuh ia ka jaka shongsuk bad ka jaka ba don."
        )
    )

    private val BLUETOOTH_PROMPTS = mapOf(
        "en" to "Bluetooth Audio Connected: Guidance is playing through your external speaker or hearing aid for loud and clear sound.",
        "hi" to "ब्लूटूथ ऑडियो कनेक्टेड: स्पष्ट आवाज के लिए वॉयस गाइडेंस आपके बाहरी स्पीकर पर चल रहा है।",
        "as" to "ব্লুটুথ অডিঅ' সংসংযুক্ত: স্পষ্ট শব্দৰ বাবে এতিয়া আপোনাৰ ব্লুটুথ স্পীকাৰত সৱল নিৰ্দেশনা বাজিছে।",
        "mzo" to "Bluetooth Audio thlun zawm a ni e: Hriat nuam tak turin aw kaihhruaina hi bluetooth speaker atangin a chhuak mek e.",
        "kha" to "Bluetooth Audio la pyniasoh: Ka jingkren ialam burom ka nang wan lyngba u bluetooth speaker jong phi."
    )

    fun getScreenGuidance(screenKey: String, langCode: String): String {
        val prompts = SCREEN_GUIDANCE_PROMPTS[screenKey] ?: SCREEN_GUIDANCE_PROMPTS["home"]!!
        return prompts[langCode] ?: prompts["en"] ?: "Welcome to Smriti!"
    }

    fun getBluetoothPrompt(langCode: String): String {
        return BLUETOOTH_PROMPTS[langCode] ?: BLUETOOTH_PROMPTS["en"]!!
    }

    data class VoiceIntentResult(
        val intent: String,
        val responseText: String,
        val actionRoute: String? = null
    )

    fun processVoiceQuery(spokenPhrase: String, langCode: String, currentCps: Double = 82.0): VoiceIntentResult {
        val phrase = spokenPhrase.lowercase().trim()

        // Emergency Reminders / SOS
        val isEmergency = phrase.contains("emergency") || phrase.contains("sos") || phrase.contains("alert") ||
                phrase.contains("danger") || phrase.contains("lost") || phrase.contains("help me") ||
                phrase.contains("आपातकाल") || phrase.contains("मदद करो") || phrase.contains("खतरे") ||
                phrase.contains("বিপদ") || phrase.contains("জৰুৰী") || phrase.contains("সহায় কৰক") ||
                phrase.contains("chhiatrupna") || phrase.contains("puihna") || phrase.contains("ka sngewsyier")

        // Specific Games Navigation
        val isMemoryGame = (phrase.contains("memory") && (phrase.contains("game") || phrase.contains("match") || phrase.contains("card"))) ||
                phrase.contains("कार्ड") || phrase.contains("मेमोरी गेम") || phrase.contains("কাৰ্ড") || phrase.contains("মেম'ৰী খেল")

        val isPatternGame = phrase.contains("pattern") || phrase.contains("design") || phrase.contains("sequence") ||
                phrase.contains("पैटर्न") || phrase.contains("डिजाइन") || phrase.contains("আৰ্হি") ||
                phrase.contains("inthlun") || phrase.contains("dur")

        val isObjectGame = phrase.contains("object") || phrase.contains("item") || phrase.contains("identify") ||
                phrase.contains("वस्तु") || phrase.contains("पहचान") || phrase.contains("বস্তু") ||
                phrase.contains("thil") || phrase.contains("tiar")

        val isStory = phrase.contains("story") || phrase.contains("reminiscence") ||
                phrase.contains("कहानी") || phrase.contains("याद") || phrase.contains("সাধু") ||
                phrase.contains("কাহিনী") || phrase.contains("thawnthu") || phrase.contains("parom")

        val isClock = phrase.contains("clock") || phrase.contains("time") || phrase.contains("ঘড়ী") ||
                phrase.contains("घड़ी") || phrase.contains("समय") || phrase.contains("sana") || phrase.contains("baje")

        val isStartGenericGame = phrase.contains("game") || phrase.contains("play") || phrase.contains("start") ||
                phrase.contains("खेल") || phrase.contains("गेम") || phrase.contains("শুরু") ||
                phrase.contains("খেল") || phrase.contains("infiamna") || phrase.contains("jingialeh")

        val isCheckScore = phrase.contains("score") || phrase.contains("how am i") || phrase.contains("progress") ||
                phrase.contains("स्कोर") || phrase.contains("प्रदर्शन") || phrase.contains("স্ক'ৰ") ||
                phrase.contains("mark") || phrase.contains("jingtynjuh")

        val isReminders = phrase.contains("medicine") || phrase.contains("pill") || phrase.contains("water") ||
                phrase.contains("reminder") || phrase.contains("schedule") || phrase.contains("दवा") || phrase.contains("पानी") ||
                phrase.contains("ঔষধ") || phrase.contains("পানী") || phrase.contains("damdawi") || phrase.contains("dawai")

        val isGpsSentinel = phrase.contains("gps") || phrase.contains("safe zone") || phrase.contains("location") ||
                phrase.contains("boundary") || phrase.contains("सुरक्षा") || phrase.contains("घेरा") ||
                phrase.contains("সুৰক্ষা") || phrase.contains("পৰিসীমা") || phrase.contains("himna")

        val isCaregiver = phrase.contains("caregiver") || phrase.contains("family") || phrase.contains("doctor") ||
                phrase.contains("केयरगिवर") || phrase.contains("डॉक्टर") || phrase.contains("ডাক্তৰ") ||
                phrase.contains("enkawltu") || phrase.contains("nongsumar")

        val isHome = phrase.contains("home") || phrase.contains("main menu") || phrase.contains("back") ||
                phrase.contains("घर") || phrase.contains("मुख्य") || phrase.contains("ঘৰ") ||
                phrase.contains("in") || phrase.contains("ing")

        val isGreeting = phrase.contains("hello") || phrase.contains("hi") || phrase.contains("namaste") ||
                phrase.contains("नमस्ते") || phrase.contains("নমস্কাৰ") || phrase.contains("chibai") || phrase.contains("khublei")

        return when {
            isEmergency -> {
                val text = when (langCode) {
                    "hi" -> "आपातकालीन अनुस्मारक सक्रिय! आपका स्थान सुरक्षित रूप से अभिभावक को भेजा गया है। हम आपके साथ हैं।"
                    "as" -> "জৰুৰী সতৰ্কতা সক্ৰিয়! আপোনাৰ অৱস্থান সুৰক্ষিতভাৱে তত্ত্বাৱধানকাৰীলৈ প্ৰেৰণ কৰা হৈছে। চিন্তা নকৰিব।"
                    "mzo" -> "Chhiatrupna hriatnawm pek a ni! I awmna hmun enkawltu hnenah kan thawn e. Mangang suh."
                    "kha" -> "Jingpyrkhat kyrpang la pynkhih! La phah ia ka jaka ba phi don sha u nongsumar. Wat sngewsyier."
                    else -> "Emergency reminder triggered! Guardian notified with your live GPS location. Stay calm, help is near."
                }
                VoiceIntentResult("EMERGENCY_ALERT", text, "gps_sentinel")
            }
            isMemoryGame -> {
                val text = when (langCode) {
                    "hi" -> "मेमोरी मैचिंग कार्ड गेम खोला जा रहा है। आराम से खेलें।"
                    "as" -> "মেম'ৰী মেচিং কাৰ্ড খেল মুকলি কৰা হৈছে। ধৈৰ্য্যেৰে খেলক।"
                    "mzo" -> "Memory matching card infiamna kan hawng mek e."
                    "kha" -> "Plie ia ka jingialeh memory matching."
                    else -> "Opening Memory Matching cards for you. Enjoy your gentle practice."
                }
                VoiceIntentResult("NAVIGATE_MEMORY", text, "game_memory")
            }
            isPatternGame -> {
                val text = when (langCode) {
                    "hi" -> "पैटर्न पहचान अभ्यास खोला जा रहा है।"
                    "as" -> "আৰ্হি চিনাক্তকৰণ অনুশীলন মুকলি কৰা হৈছে।"
                    "mzo" -> "Pattern hriatfiahna infiamna kan hawng mek e."
                    "kha" -> "Plie ia ka pattern recognition."
                    else -> "Opening Pattern Recognition practice."
                }
                VoiceIntentResult("NAVIGATE_PATTERN", text, "game_pattern")
            }
            isObjectGame -> {
                val text = when (langCode) {
                    "hi" -> "क्षेत्रीय वस्तु पहचान खेल खोला जा रहा है।"
                    "as" -> "আঞ্চলিক বস্তু চিনাক্তকৰণ খেল মুকলি কৰা হৈছে।"
                    "mzo" -> "Thil hriat hran infiamna kan hawng mek e."
                    "kha" -> "Plie ia ka object recognition."
                    else -> "Opening Regional Object Recognition game."
                }
                VoiceIntentResult("NAVIGATE_OBJECT", text, "game_object")
            }
            isStory -> {
                val text = when (langCode) {
                    "hi" -> "आइए एक सुकून भरी पुरानी याद और कहानी सुनते हैं। रेमिनिसेंस थेरेपी शुरू की जा रही है।"
                    "as" -> "ব'লক এটা মৰমৰ পুৰণি কাহিনী শুনোঁ। স্মৃতি আৰু কাহিনী মুকলি কৰা হৈছে।"
                    "mzo" -> "Thawnthu leh hriatreng thil hlui ngaihthlak i tan ang u."
                    "kha" -> "Kha ngin sngap ia ki parom bad ki jingkynmaw kiba bang."
                    else -> "Let's listen to a comforting cherished story from our memories."
                }
                VoiceIntentResult("START_STORY", text, "game_reminiscence")
            }
            isClock -> {
                val text = when (langCode) {
                    "hi" -> "चलिए घड़ी और समय ओरिएंटेशन का आसान अभ्यास करते हैं।"
                    "as" -> "ব'লক ঘড়ী আৰু সময়ৰ অৱস্থিতিৰ সহজ অনুশীলন কৰোঁ।"
                    "mzo" -> "Sana leh hun hriatfiahna zirtirna kan tan mek e."
                    "kha" -> "Ngin pyrshang ia ka jingithuh por bad baje."
                    else -> "Let's practice gentle clock orientation and time setting."
                }
                VoiceIntentResult("START_CLOCK", text, "game_clock")
            }
            isStartGenericGame -> {
                val text = when (langCode) {
                    "hi" -> "चलिए एक नया खेल शुरू करते हैं! मेमोरी मैचिंग कार्ड खेलें।"
                    "as" -> "ব'লক এটা নতুন খেল আৰম্ভ কৰোঁ! মেম'ৰী মেচিং কাৰ্ড খেলক।"
                    "mzo" -> "Infiamna thar i tan ang u! Memory matching card hi thlang rawh."
                    "kha" -> "Kha ngin sdang ia ka jingialeh thymmai! Kynton ia ka memory matching."
                    else -> "Let's start a brain activity! Opening Memory Matching for you."
                }
                VoiceIntentResult("START_GAME", text, "game_memory")
            }
            isReminders -> {
                val text = when (langCode) {
                    "hi" -> "अपनी सुबह की दवा और दिनचर्या देखें। शेड्यूल स्क्रीन खोल रहा हूँ।"
                    "as" -> "আপোনাৰ পুৱাৰ ঔষধ আৰু যত্নৰ সময়সূচী স্ক্ৰীণ খুলিছোঁ।"
                    "mzo" -> "Damdawi leh ni tin ruahmanna screen kan hawng mek e."
                    "kha" -> "Plie ia ka schedule ban dih dawai bad ka jingiaid."
                    else -> "Opening your Daily Care & Medication Schedule."
                }
                VoiceIntentResult("CHECK_REMINDERS", text, "reminders")
            }
            isGpsSentinel -> {
                val text = when (langCode) {
                    "hi" -> "जीपीएस सेफ-ज़ोन सेंटिनल खोला जा रहा है। स्थान सुरक्षित है।"
                    "as" -> "জি পি এছ সুৰক্ষা সেন্টিনেল স্ক্ৰীণ খুলিছোঁ।"
                    "mzo" -> "GPS Himna Hmun Sentinel kan hawng mek e."
                    "kha" -> "Plie ia ka GPS Safe-Zone Sentinel."
                    else -> "Opening GPS Safe-Zone Sentinel."
                }
                VoiceIntentResult("NAVIGATE_GPS", text, "gps_sentinel")
            }
            isCaregiver -> {
                val text = when (langCode) {
                    "hi" -> "केयरगिवर डैशबोर्ड खोला जा रहा है।"
                    "as" -> "তত্ত্বাৱধানকাৰী ডেশ্বব'ৰ্ড স্ক্ৰীণ খুলিছোঁ।"
                    "mzo" -> "Enkawltu Dashboard kan hawng mek e."
                    "kha" -> "Plie ia ka Caregiver Dashboard."
                    else -> "Opening Caregiver Dashboard."
                }
                VoiceIntentResult("NAVIGATE_CAREGIVER", text, "caregiver")
            }
            isHome -> {
                val text = when (langCode) {
                    "hi" -> "मुख्य स्क्रीन पर वापस जा रहे हैं।"
                    "as" -> "প্ৰধান স্ক্ৰীণলৈ উভতি গৈছোঁ।"
                    "mzo" -> "Home screen-ah kan let leh mek e."
                    "kha" -> "Lehphai sha ka Home screen."
                    else -> "Returning to Home screen."
                }
                VoiceIntentResult("NAVIGATE_HOME", text, "home")
            }
            isCheckScore -> {
                val text = when (langCode) {
                    "hi" -> "बहुत बढ़िया! आपका आज का अभ्यास स्कोर $currentCps है। आप बहुत अच्छा कर रहे हैं।"
                    "as" -> "অতি সুন্দৰ! আজি আপোনাৰ অনুশীলন স্ক'ৰ হ'ল $currentCps। আপুনি খুব ভাল কৰিছে।"
                    "mzo" -> "Thawk tha hle mai! Vawiina i mark hmuh chu $currentCps a ni e."
                    "kha" -> "Jingkyntiew babha! Ka jingtynjuh jong phi mynta ka long $currentCps."
                    else -> "Wonderful job! Your practice score today is $currentCps. You are doing great."
                }
                VoiceIntentResult("CHECK_SCORE", text, null)
            }
            isGreeting -> {
                val text = when (langCode) {
                    "hi" -> "नमस्ते! मैं आपकी स्मृति साथी हूँ। आप 'कार्ड गेम', 'घड़ी अभ्यास', 'दवा याद दिलाओ' या 'मदद' बोल सकते हैं।"
                    "as" -> "নমস্কাৰ! মই আপোনাৰ স্মৃতি সংগী। আপুনি 'মেম'ৰী খেল', 'কাহিনী শুনা', 'ঔষধ' বা 'সহায়' ক'ব পাৰে।"
                    "mzo" -> "Chibai! Smriti tanpuitu ka ni e. 'Infiamna', 'Thawnthu', a nih loh chuan 'Damdawi' tiin sawi rawh le."
                    "kha" -> "Khublei! Nga long u nongiarap Smriti. Phi lah ban ong 'jingialeh', 'parom', lane 'dawai'."
                    else -> "Hello! I am your Smriti companion. Say 'Memory game', 'Clock', 'Medicine', or 'Emergency help' anytime."
                }
                VoiceIntentResult("GREETING", text, null)
            }
            else -> {
                val text = when (langCode) {
                    "hi" -> "सुना: \"$spokenPhrase\"। आप 'खेल खेलें', 'कहानी सुनाओ', 'घड़ी', या 'दवा' बोल सकते हैं।"
                    "as" -> "শুনিলোঁ: \"$spokenPhrase\"। আপুনি 'খেল', 'সাধু', 'ঘড়ী', নতুবা 'ঔষধ' ক'ব পাৰে।"
                    "mzo" -> "Hriat a ni: \"$spokenPhrase\"। 'Infiamna', 'Thawnthu', maw 'Damdawi' sawi rawh le."
                    "kha" -> "La sngap: \"$spokenPhrase\"। Phi lah ban ong 'jingialeh', 'parom', lane 'dawai'."
                    else -> "Heard: \"$spokenPhrase\". You can say 'Memory Game', 'Story', 'Clock', 'Schedule', or 'Emergency'."
                }
                VoiceIntentResult("UNKNOWN", text, null)
            }
        }
    }
}
