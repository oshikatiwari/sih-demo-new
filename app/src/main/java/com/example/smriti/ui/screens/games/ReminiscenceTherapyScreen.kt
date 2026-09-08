package com.example.smriti.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smriti.data.SmritiRepository
import com.example.smriti.model.CpsResult
import com.example.smriti.model.SessionMetrics
import com.example.smriti.service.AppStrings
import com.example.smriti.service.TtsManager
import com.example.smriti.ui.theme.EmeraldGreen
import com.example.smriti.ui.theme.ForestGreen
import com.example.smriti.ui.theme.MintPastel
import com.example.smriti.ui.theme.PineGreen
import kotlinx.coroutines.launch

data class ReminiscenceStory(
    val id: String,
    val title: String,
    val era: String,
    val category: String,
    val iconEmoji: String,
    val narrativeEn: String,
    val narrativeHi: String,
    val narrativeAs: String,
    val narrativeMzo: String,
    val narrativeKha: String,
    val gentleQuestion: String,
    val pleasantOptions: List<String>,
    val comfortingReflection: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminiscenceTherapyScreen(
    ttsManager: TtsManager,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentLang by SmritiRepository.currentLanguage.collectAsState()

    val stories = remember {
        listOf(
            ReminiscenceStory(
                id = "story-1",
                title = "The Morning Chai in the Courtyard",
                era = "1960s - 1980s",
                category = "Family & Togetherness",
                iconEmoji = "☕",
                narrativeEn = "The sun rose gently over the tall betel trees. Steam curled up from a hot clay cup of malty tea blended with fresh ginger and crushed cardamom. Family gathered on the wooden verandah to share warm laughter before the day began.",
                narrativeHi = "सुबह की पहली किरण के साथ आंगन में ताज़ी अदरक और इलायची वाली कड़क चाय की खुशबू फैल जाती थी। घर के सभी बड़े-बुजुर्ग बरामदे पर बैठकर पुरानी बातें करते और सुकून भरी धूप सेकते थे।",
                narrativeAs = "ৰাতিপুৱাৰ কোমল ৰ'দত চোতালত আদা আৰু ইলাচী দিয়া গৰম চাহৰ কাপৰ পৰা ধোঁৱা ওলাইছিল। বৰঘৰৰ পিৰালিত বহি সকলোৱে আত্মীয়তাৰে মন জুৰোৱা কথা পাতিছিল।",
                narrativeMzo = "Zing khawvar rualin thingpui lum sa ver vawr, thakthing leh thinghmarcha inpawlh rim chu a nam chem chem a. Veranda-ah chhungkua an thu khawm a, an titi ho thin.",
                narrativeKha = "Haba dang step, ka jingiwbih jong ka sha sharak bad u sopti ka la pynithuh ia ka shnong baroh. Ki kmie ki kpa ki shong lang ha phyllaw ban pynpyngngad bad kren parom.",
                gentleQuestion = "What warm aroma does this memory bring to mind?",
                pleasantOptions = listOf("Fresh Ginger & Spiced Tea", "Cool Morning Dew & Rain", "Jasmine & Garden Flowers"),
                comfortingReflection = "Cherishing these peaceful morning rituals stimulates long-term autobiographical memory and calms the emotional nervous system."
            ),
            ReminiscenceStory(
                id = "story-2",
                title = "The Spring Bihu Harvest Rhythms",
                era = "Festival of Life",
                category = "Music & Heritage",
                iconEmoji = "🪘",
                narrativeEn = "The rhythm of the dhol drum echoed across the blooming mustard fields. Young and old gathered under the sprawling banyan tree, wearing traditional red-and-white handwoven muga silk, celebrating growth and kindness.",
                narrativeHi = "खेतों में सरसों के पीले फूल खिले थे और ढोलक की मधुर थाप गूंज रही थी। पारंपरिक रेशमी परिधानों में सजे सभी लोग प्रेम, आनंद और नई फसल का स्वागत करने के लिए साथ आए थे।",
                narrativeAs = "বসন্তৰ আগমনত ঢোলৰ চাপৰি সমগ্ৰ সৰিয়হ ডৰাত বাজি উঠিছিল। ৰঙা আৰু বগা মুগা ৰেচমৰ সাজ পিন্ধি সকলোৱে মিলি-জুলি হেঁপাহেৰে নতুন দিনৰ আদৰণি জনাইছিল।",
                narrativeMzo = "Kut ropui lai chuan khuang leh khepchep ri chuan huan zawng zawng a luah khat a. Mipui chuan puanchhe hmun leh ropui tak inbelin lawmna an nei thin.",
                narrativeKha = "Ha ka por shad lehkmen, ki ksing bad ki nakra ki pynkhih ia ka jingim. Baroh ki kup da ki jainsem ksiar ban lehkmen ia ka jingroi jong ka spah mariang.",
                gentleQuestion = "Which traditional instrument brought families to dance together?",
                pleasantOptions = listOf("The Handcrafted Drum (Dhol)", "The Bamboo Flute (Pepa)", "The Silver Chimes"),
                comfortingReflection = "Musical memories tap into preserved procedural and emotional memory circuits, bringing deep comfort and joy to elderly minds."
            ),
            ReminiscenceStory(
                id = "story-3",
                title = "The Handwoven Loom of Affection",
                era = "Heritage Craft",
                category = "Creativity & Touch",
                iconEmoji = "🌸",
                narrativeEn = "The rhythmic click-clack of the wooden loom resounded in the quiet afternoon. Mother carefully guided the golden threads to weave a traditional Gamosa, an enduring symbol of respect and welcoming love.",
                narrativeHi = "दोपहर के समय हथकरघे की खट-खट की आवाज़ बहुत सुकून देती थी। सुनहरे धागों से प्रेम और सम्मान का प्रतीक पारंपरिक अंगवस्त्र बुना जाता था, जिसे अपनों को आदरपूर्वक भेंट किया जाता था।",
                narrativeAs = "দুপৰীয়া তাতশালৰ খটখট শব্দই পৰিৱেশটো শুৱনি কৰি তুলিছিল। মৰমেৰে সোণালী সূতাৰে বাতি কাঢ়ি মৰমৰ গামোচাখন বোৱা হৈছিল, যি শ্ৰদ্ধাৰ এক অমলিন প্ৰতীক।",
                narrativeMzo = "Chawhnu reh takah chuan puan tah khawl ri rap rap chu ngaihthlak a nuam hle. Hmangaihna lantirna puan mawi tak chu duat takin an tah thin.",
                narrativeKha = "Ha ka por nohphyrngap, u thyrnia thain jain u sawa sngewtynnad. Ki kmie ki thain ia ki jain ba bha tam na bynta ban burom ia ki nongshong shnong bad kiba wan jngoh.",
                gentleQuestion = "What handcrafted token of love and respect was woven on the loom?",
                pleasantOptions = listOf("A Handwoven Gamosa Shawl", "A Soft Cotton Quilt", "A Decorative Wall Tapestry"),
                comfortingReflection = "Familiar sensory imagery of textures and sounds provides a reassuring anchor against disorientation and anxiety."
            )
        )
    }

    var currentStoryIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var showReflection by remember { mutableStateOf(false) }
    var isReadingAloud by remember { mutableStateOf(false) }
    var completedCount by remember { mutableIntStateOf(0) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }

    val currentStory = stories[currentStoryIndex]

    val narrative = when (currentLang) {
        "hi" -> currentStory.narrativeHi
        "as" -> currentStory.narrativeAs
        "mzo" -> currentStory.narrativeMzo
        "kha" -> currentStory.narrativeKha
        else -> currentStory.narrativeEn
    }

    fun readStoryAloud() {
        isReadingAloud = true
        ttsManager.speak(narrative, currentLang)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.get("reminiscence_therapy", currentLang),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.get("back", currentLang), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isReadingAloud) {
                                ttsManager.stop()
                                isReadingAloud = false
                            } else {
                                readStoryAloud()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isReadingAloud) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Read Aloud",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PineGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F6F4))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dementia Therapeutic Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintPastel)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MintPastel),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Calming Reminiscence Therapy • No Wrong Answers • Pure Comfort & Joy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForestGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Story Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MintPastel),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentStory.iconEmoji, fontSize = 28.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Memory ${currentStoryIndex + 1} of ${stories.size}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PineGreen
                            )
                            Text(
                                text = currentStory.era,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentStory.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = narrative,
                        fontSize = 16.sp,
                        color = Color(0xFF2D3748),
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio Playback Bar
                    Button(
                        onClick = { readStoryAloud() },
                        colors = ButtonDefaults.buttonColors(containerColor = MintPastel),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("listen_narrative_button")
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isReadingAloud) "Replay Audio Narration" else "Listen to Story in Native Voice",
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gentle Reflection Question
            Text(
                text = currentStory.gentleQuestion,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options
            currentStory.pleasantOptions.forEachIndexed { idx, opt ->
                val isSelected = selectedOptionIndex == idx

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            selectedOptionIndex = idx
                            showReflection = true
                            ttsManager.playChime()
                            ttsManager.speak("Beautiful recollection. $opt", currentLang)
                        }
                        .testTag("reminiscence_option_$idx"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFD8F3DC) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) EmeraldGreen else Color(0xFFE2E8F0)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) EmeraldGreen else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = opt,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PineGreen else Color(0xFF2D3748)
                        )
                    }
                }
            }

            // Clinical Explanation & Comfort
            AnimatedVisibility(visible = showReflection) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MintPastel)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentStory.comfortingReflection,
                                fontSize = 13.sp,
                                color = ForestGreen,
                                lineHeight = 19.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            completedCount++
                            if (currentStoryIndex < stories.size - 1) {
                                currentStoryIndex++
                                selectedOptionIndex = null
                                showReflection = false
                                isReadingAloud = false
                            } else {
                                val elapsedMs = (System.currentTimeMillis() - startTime).toDouble()
                                val metrics = SessionMetrics(
                                    accuracy = 100.0, // In reminiscence therapy, active engagement is 100% successful
                                    responseTimeMs = elapsedMs / stories.size,
                                    completionRate = 100.0,
                                    attempts = stories.size,
                                    errors = 0,
                                    isMemoryGame = true,
                                    memorySpecificAccuracy = 100.0
                                )
                                SmritiRepository.recordGameSession("Reminiscence Therapy", metrics)
                                showCompletionDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("next_reminiscence_button")
                    ) {
                        Text(
                            text = if (currentStoryIndex < stories.size - 1) "Next Cherished Memory" else "Complete Memory Session",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = {
                showCompletionDialog = false
                onBack()
            },
            title = {
                Text(
                    text = "Warm Memories Cherished!",
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "You completed today's Reminiscence Therapy session with loving reflection.",
                        fontSize = 15.sp,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        text = "• Engaging personal memories boosts serotonin and emotional calm.\n• Preserves autobiographical identity in dementia care.\n• 100% positive reinforcement awarded.",
                        fontSize = 13.sp,
                        color = ForestGreen,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompletionDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PineGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(AppStrings.get("back_to_home", currentLang))
                }
            }
        )
    }
}
