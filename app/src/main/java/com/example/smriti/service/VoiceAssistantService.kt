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

        val isStartGame = phrase.contains("game") || phrase.contains("play") || phrase.contains("start") ||
                phrase.contains("खेल") || phrase.contains("गेम") || phrase.contains("শুরু") ||
                phrase.contains("খেল") || phrase.contains("infiamna") || phrase.contains("jingialeh")

        val isCheckScore = phrase.contains("score") || phrase.contains("how am i") || phrase.contains("progress") ||
                phrase.contains("स्कोर") || phrase.contains("प्रदर्शन") || phrase.contains("স্ক'ৰ") ||
                phrase.contains("mark") || phrase.contains("jingtynjuh")

        val isReminders = phrase.contains("medicine") || phrase.contains("pill") || phrase.contains("water") ||
                phrase.contains("reminder") || phrase.contains("दवा") || phrase.contains("पानी") ||
                phrase.contains("ঔষধ") || phrase.contains("পানী") || phrase.contains("damdawi") || phrase.contains("dawai")

        val isCaregiver = phrase.contains("help") || phrase.contains("caregiver") || phrase.contains("call") ||
                phrase.contains("doctor") || phrase.contains("मदद") || phrase.contains("सहायता") ||
                phrase.contains("সহায়") || phrase.contains("ডাক্তৰ") || phrase.contains("iarap")

        val isGreeting = phrase.contains("hello") || phrase.contains("hi") || phrase.contains("namaste") ||
                phrase.contains("नमस्ते") || phrase.contains("নমস্কাৰ") || phrase.contains("chibai") || phrase.contains("khublei")

        return when {
            isStartGame -> {
                val text = when (langCode) {
                    "hi" -> "चलिए एक नया खेल शुरू करते हैं! मेमोरी मैचिंग कार्ड खेलें।"
                    "as" -> "ব'লক এটা নতুন খেল আৰম্ভ কৰোঁ! মেম'ৰী মেচিং কাৰ্ড খেলক।"
                    "mzo" -> "Infiamna thar i tan ang u! Memory matching card hi thlang rawh."
                    "kha" -> "Kha ngin sdang ia ka jingialeh thymmai! Kynton ia ka memory matching."
                    else -> "Let's start a brain activity! Opening Memory Matching for you."
                }
                VoiceIntentResult("START_GAME", text, "game_memory")
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
            isReminders -> {
                val text = when (langCode) {
                    "hi" -> "अपनी सुबह की दवा पानी के साथ अवश्य लें। शेड्यूल स्क्रीन खोल रहा हूँ।"
                    "as" -> "আপোনাৰ পুৱাৰ ঔষধ পানীৰ সৈতে লওক। সময়সূচী স্ক্ৰীণ খুলিছোঁ।"
                    "mzo" -> "I tukthuan damdawi ei rawh le. Reminders screen kan hawng mek e."
                    "kha" -> "Dih ia ka dawai mynta step. Ngin plie ia ka reminders screen."
                    else -> "Please remember your morning medication with water. Opening your schedule."
                }
                VoiceIntentResult("CHECK_REMINDERS", text, "reminders")
            }
            isCaregiver -> {
                val text = when (langCode) {
                    "hi" -> "चिंता न करें, हमने आपके केयरगिवर को तुरंत संदेश भेजा है।"
                    "as" -> "চিন্তা নকৰিব, আমি আপোনাৰ তত্ত্বাৱধানকাৰীলৈ বাৰ্তা প্ৰেৰণ কৰিছোঁ।"
                    "mzo" -> "Mangang suh, i enkawltu hnena thuthawn kan thawn mek e."
                    "kha" -> "Wat sngewsyier, ngi phah khubor sha u nongsumar mynta."
                    else -> "Don't worry, we are alerting your caregiver right now."
                }
                VoiceIntentResult("CALL_CAREGIVER", text, null)
            }
            isGreeting -> {
                val text = when (langCode) {
                    "hi" -> "नमस्ते! मैं आपकी स्मृति साथी हूँ। आज आप क्या करना चाहेंगे?"
                    "as" -> "নমস্কাৰ! মই আপোনাৰ স্মৃতি সংগী। আজি আপুনি কি কৰিব বিচাৰিব?"
                    "mzo" -> "Chibai! Smriti tanpuitu ka ni e. Vawiin hian eng nge tih i duh ang?"
                    "kha" -> "Khublei! Nga long u nongiarap Smriti. Kiei ba phi kwah ban leh mynta?"
                    else -> "Hello! I am your Smriti companion. How can I assist you today?"
                }
                VoiceIntentResult("GREETING", text, null)
            }
            else -> {
                val text = when (langCode) {
                    "hi" -> "मैं समझ गई! आप होम स्क्रीन पर खेल खेल सकते हैं या अपना स्कोर देख सकते हैं।"
                    "as" -> "মই বুজি পালোঁ! আপুনি যিকোনো সময়তে খেল খেলিব পাৰে নতুবা স্ক'ৰ চাব পাৰে।"
                    "mzo" -> "Ka hrethiam e! Hun eng tik ah pawh infiamna i khel thei a ni."
                    "kha" -> "Nga sngewthuh! Phi lah ban ialeh jingialeh lane peit ia ka jingtynjuh jong phi."
                    else -> "I understand! You can practice daily brain activities or check your reminders anytime."
                }
                VoiceIntentResult("UNKNOWN", text, null)
            }
        }
    }
}
