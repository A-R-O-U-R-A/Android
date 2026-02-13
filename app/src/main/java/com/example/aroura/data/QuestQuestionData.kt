package com.example.aroura.data

/**
 * Quest Question Data
 *
 * Self-discovery quest questions organized into 3 quests × 3 sections × 8 questions.
 * Uses the existing QuestionType system from ReflectTestData.kt.
 */

data class QuestQuestion(
    val index: Int,
    val text: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val likertLabels: Pair<String, String> = "" to ""
)

data class QuestSectionInfo(
    val sectionId: String,
    val title: String,
    val description: String,
    val emoji: String,
    val questions: List<QuestQuestion>
)

data class QuestInfo(
    val questId: String,
    val title: String,
    val sections: List<QuestSectionInfo>
)

// ═══════════════════════════════════════════════════════════════════════════════
// ALL QUEST DATA
// ═══════════════════════════════════════════════════════════════════════════════

object QuestQuestionRepository {

    val allQuests: List<QuestInfo> = listOf(

        // ─────────────────────────────────────────────────────────────────────
        // QUEST 1: EMOTIONAL AWARENESS
        // ─────────────────────────────────────────────────────────────────────
        QuestInfo(
            questId = "emotional_awareness",
            title = "Emotional Awareness",
            sections = listOf(

                // Section 1.1: Emotional Clarity
                QuestSectionInfo(
                    sectionId = "emotional_clarity",
                    title = "Emotional Clarity",
                    description = "Identify your dominant emotion and triggers",
                    emoji = "🔍",
                    questions = listOf(
                        QuestQuestion(0, "How easily can you name what you're feeling right now?", QuestionType.LIKERT_5, likertLabels = "Very difficult" to "Very easy"),
                        QuestQuestion(1, "When something upsets you, do you know why?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(2, "Which emotion do you experience most frequently?", QuestionType.MULTIPLE_CHOICE, listOf("Joy", "Anxiety", "Sadness", "Anger", "Numbness")),
                        QuestQuestion(3, "How often do you feel emotions you can't explain?", QuestionType.LIKERT_5, likertLabels = "Never" to "Very often"),
                        QuestQuestion(4, "Do you tend to suppress your emotions?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "Can you tell when your mood shifts throughout the day?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(6, "What typically triggers a strong emotional reaction in you?", QuestionType.MULTIPLE_CHOICE, listOf("Conflict with others", "Feeling ignored", "Unexpected change", "Being criticized", "Feeling overwhelmed")),
                        QuestQuestion(7, "Describe in a few words the emotion you feel most often.", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 1.2: Pattern Recognition
                QuestSectionInfo(
                    sectionId = "pattern_recognition",
                    title = "Pattern Recognition",
                    description = "Discover when you feel most stressed",
                    emoji = "🧩",
                    questions = listOf(
                        QuestQuestion(0, "At what time of day do you feel most stressed?", QuestionType.MULTIPLE_CHOICE, listOf("Morning", "Afternoon", "Evening", "Late night", "It varies")),
                        QuestQuestion(1, "Do certain people consistently affect your mood?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(2, "How often do you notice repeating emotional patterns?", QuestionType.LIKERT_5, likertLabels = "Never" to "All the time"),
                        QuestQuestion(3, "When stressed, what's your go-to response?", QuestionType.MULTIPLE_CHOICE, listOf("Withdraw and isolate", "Talk to someone", "Distract myself", "Exercise or move", "Overthink everything")),
                        QuestQuestion(4, "Do weekdays vs weekends significantly change your mood?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "How aware are you of your stress triggers?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Extremely aware"),
                        QuestQuestion(6, "Do you notice physical signs of stress before emotional ones?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(7, "What situation repeatedly causes you stress?", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 1.3: Emotional Strength Mapping
                QuestSectionInfo(
                    sectionId = "emotional_strength",
                    title = "Emotional Strength Mapping",
                    description = "Find your coping strengths",
                    emoji = "💪",
                    questions = listOf(
                        QuestQuestion(0, "How quickly can you recover from a bad mood?", QuestionType.LIKERT_5, likertLabels = "Very slowly" to "Very quickly"),
                        QuestQuestion(1, "Do you have healthy ways to process difficult emotions?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(2, "Which coping mechanism works best for you?", QuestionType.MULTIPLE_CHOICE, listOf("Talking it out", "Physical activity", "Creative expression", "Meditation/breathing", "Journaling")),
                        QuestQuestion(3, "How confident are you in handling emotional challenges?", QuestionType.LIKERT_5, likertLabels = "Not confident" to "Very confident"),
                        QuestQuestion(4, "Can you stay calm when others around you are upset?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "How well do you set emotional boundaries?", QuestionType.LIKERT_5, likertLabels = "Poorly" to "Very well"),
                        QuestQuestion(6, "Do you ask for help when emotionally overwhelmed?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(7, "What's your greatest emotional strength?", QuestionType.SHORT_TEXT)
                    )
                )
            )
        ),

        // ─────────────────────────────────────────────────────────────────────
        // QUEST 2: MINDSET & GROWTH
        // ─────────────────────────────────────────────────────────────────────
        QuestInfo(
            questId = "mindset_growth",
            title = "Mindset & Growth",
            sections = listOf(

                // Section 2.1: Self-Talk Analysis
                QuestSectionInfo(
                    sectionId = "self_talk",
                    title = "Self-Talk Analysis",
                    description = "Understand your inner critic",
                    emoji = "🗣️",
                    questions = listOf(
                        QuestQuestion(0, "How would you describe your inner voice most of the time?", QuestionType.MULTIPLE_CHOICE, listOf("Encouraging", "Critical", "Neutral", "Anxious", "Mixed")),
                        QuestQuestion(1, "Do you often criticize yourself after making a mistake?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(2, "How kind is the way you talk to yourself?", QuestionType.LIKERT_5, likertLabels = "Very harsh" to "Very kind"),
                        QuestQuestion(3, "Do you compare yourself to others frequently?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(4, "When you fail at something, what do you usually think?", QuestionType.MULTIPLE_CHOICE, listOf("I'm not good enough", "Everyone fails sometimes", "I'll try again differently", "I should give up", "I blame circumstances")),
                        QuestQuestion(5, "How often do you celebrate your own achievements?", QuestionType.LIKERT_5, likertLabels = "Never" to "Always"),
                        QuestQuestion(6, "Can you catch yourself when negative self-talk starts?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(7, "What does your inner critic say most often?", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 2.2: Fear & Risk Mapping
                QuestSectionInfo(
                    sectionId = "fear_risk",
                    title = "Fear & Risk Mapping",
                    description = "Explore your comfort zones",
                    emoji = "🎯",
                    questions = listOf(
                        QuestQuestion(0, "How comfortable are you with uncertainty?", QuestionType.LIKERT_5, likertLabels = "Very uncomfortable" to "Very comfortable"),
                        QuestQuestion(1, "What's your biggest fear right now?", QuestionType.MULTIPLE_CHOICE, listOf("Failure", "Rejection", "Being alone", "Losing control", "The unknown")),
                        QuestQuestion(2, "Do you avoid situations that feel risky?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(3, "How often do you step outside your comfort zone?", QuestionType.LIKERT_5, likertLabels = "Never" to "Very often"),
                        QuestQuestion(4, "When facing a fear, do you push through or retreat?", QuestionType.MULTIPLE_CHOICE, listOf("Push through defiantly", "Slowly work my way in", "Avoid it entirely", "Depends on the fear", "Freeze and panic")),
                        QuestQuestion(5, "Do past failures prevent you from trying new things?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(6, "How would you rate your overall courage?", QuestionType.LIKERT_5, likertLabels = "Very low" to "Very high"),
                        QuestQuestion(7, "What would you do if you weren't afraid?", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 2.3: Growth Indicator
                QuestSectionInfo(
                    sectionId = "growth_indicator",
                    title = "Growth Indicator",
                    description = "Measure your adaptability",
                    emoji = "🌱",
                    questions = listOf(
                        QuestQuestion(0, "How open are you to changing your opinions?", QuestionType.LIKERT_5, likertLabels = "Very closed" to "Very open"),
                        QuestQuestion(1, "Do you actively seek feedback from others?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(2, "How do you typically respond to constructive criticism?", QuestionType.MULTIPLE_CHOICE, listOf("Appreciate it", "Get defensive", "Feel hurt but process it", "Ignore it", "Reflect on it later")),
                        QuestQuestion(3, "How often do you learn something new intentionally?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Daily"),
                        QuestQuestion(4, "Can you adapt quickly when plans change?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "Do you believe you can improve your core personality traits?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(6, "How resilient do you feel when life gets difficult?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Extremely"),
                        QuestQuestion(7, "What area of your life are you most eager to grow in?", QuestionType.SHORT_TEXT)
                    )
                )
            )
        ),

        // ─────────────────────────────────────────────────────────────────────
        // QUEST 3: CORE PERSONALITY & ENERGY
        // ─────────────────────────────────────────────────────────────────────
        QuestInfo(
            questId = "core_personality",
            title = "Core Personality & Energy",
            sections = listOf(

                // Section 3.1: Social Energy
                QuestSectionInfo(
                    sectionId = "social_energy",
                    title = "Social Energy",
                    description = "Discover your social style",
                    emoji = "👥",
                    questions = listOf(
                        QuestQuestion(0, "Do social interactions energize or drain you?", QuestionType.LIKERT_5, likertLabels = "Totally drain me" to "Totally energize me"),
                        QuestQuestion(1, "How large is your ideal social circle?", QuestionType.MULTIPLE_CHOICE, listOf("1-2 close friends", "3-5 close friends", "A medium group", "A large social network", "I prefer being alone")),
                        QuestQuestion(2, "Do you enjoy meeting new people?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(3, "How comfortable are you in large group settings?", QuestionType.LIKERT_5, likertLabels = "Very uncomfortable" to "Very comfortable"),
                        QuestQuestion(4, "Do you need alone time to recharge after socializing?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "How authentic are you in social situations?", QuestionType.LIKERT_5, likertLabels = "I mask a lot" to "Fully authentic"),
                        QuestQuestion(6, "What role do you typically take in a group?", QuestionType.MULTIPLE_CHOICE, listOf("Leader", "Mediator", "Observer", "Entertainer", "Supporter")),
                        QuestQuestion(7, "What does your ideal social life look like?", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 3.2: Emotional Energy Type
                QuestSectionInfo(
                    sectionId = "emotional_energy",
                    title = "Emotional Energy Type",
                    description = "How you process decisions and feelings",
                    emoji = "⚡",
                    questions = listOf(
                        QuestQuestion(0, "Do you make decisions with your head or your heart?", QuestionType.LIKERT_5, likertLabels = "Always head" to "Always heart"),
                        QuestQuestion(1, "How quickly do you make important decisions?", QuestionType.MULTIPLE_CHOICE, listOf("Instantly", "A few hours", "A few days", "Weeks of deliberation", "I avoid deciding")),
                        QuestQuestion(2, "Do other people's emotions strongly affect yours?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(3, "How emotionally available are you to others?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Completely"),
                        QuestQuestion(4, "Do you trust your gut feelings when making choices?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(5, "How do you handle emotionally charged conversations?", QuestionType.MULTIPLE_CHOICE, listOf("Stay calm and logical", "Get emotionally involved", "Try to keep balance", "Avoid them entirely", "Shut down")),
                        QuestQuestion(6, "How emotionally resilient do you consider yourself?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Extremely"),
                        QuestQuestion(7, "What gives you emotional energy?", QuestionType.SHORT_TEXT)
                    )
                ),

                // Section 3.3: Core Motivation
                QuestSectionInfo(
                    sectionId = "core_motivation",
                    title = "Core Motivation",
                    description = "What truly drives you",
                    emoji = "🔥",
                    questions = listOf(
                        QuestQuestion(0, "What motivates you most in life?", QuestionType.MULTIPLE_CHOICE, listOf("Achievement", "Connection", "Freedom", "Security", "Purpose")),
                        QuestQuestion(1, "Do you feel driven by passion or duty?", QuestionType.LIKERT_5, likertLabels = "Entirely duty" to "Entirely passion"),
                        QuestQuestion(2, "How aligned is your daily life with your core values?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Perfectly aligned"),
                        QuestQuestion(3, "Do you have a clear sense of purpose?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(4, "What would you do with your life if money weren't a factor?", QuestionType.MULTIPLE_CHOICE, listOf("Travel and explore", "Create art or music", "Help others", "Build something", "Learn endlessly")),
                        QuestQuestion(5, "How often do you feel genuinely excited about your future?", QuestionType.LIKERT_5, likertLabels = "Never" to "Always"),
                        QuestQuestion(6, "Do you feel you're living authentically?", QuestionType.YES_NO_SOMETIMES),
                        QuestQuestion(7, "What's the one thing you want to be remembered for?", QuestionType.SHORT_TEXT)
                    )
                )
            )
        )
    )

    fun getQuestInfo(questId: String): QuestInfo? =
        allQuests.find { it.questId == questId }

    fun getSectionInfo(questId: String, sectionId: String): QuestSectionInfo? =
        getQuestInfo(questId)?.sections?.find { it.sectionId == sectionId }

    fun getQuestions(questId: String, sectionId: String): List<QuestQuestion> =
        getSectionInfo(questId, sectionId)?.questions ?: emptyList()
}
