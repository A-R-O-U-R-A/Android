package com.example.aroura.data

import androidx.compose.ui.graphics.Color

/**
 * Reflect Test Data Models
 * 
 * Comprehensive data architecture for the 37+ psychological assessments
 * organized into 7 sections. Each test follows a standardized structure
 * for consistency and maintainability.
 */

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION DEFINITIONS
// ═══════════════════════════════════════════════════════════════════════════════

enum class ReflectSection(
    val title: String,
    val subtitle: String,
    val emoji: String
) {
    PERSONALITY("Personality & Identity", "Discover who you are", "🧬"),
    EMOTIONAL("Emotional & Psychological Health", "Understand your inner world", "🧠"),
    CHILDHOOD("Childhood & Inner World", "Explore your formative years", "🧩"),
    RELATIONSHIPS("Relationships & Attachment", "Understand how you connect", "❤️"),
    TRAUMA("Trauma, Patterns & Coping", "Recognize your patterns", "🌀"),
    TOXICITY("Toxicity, Manipulation & Narcissism", "Identify harmful dynamics", "⚠️"),
    GROWTH("Life Direction & Growth", "Chart your path forward", "🎓")
}

// ═══════════════════════════════════════════════════════════════════════════════
// TEST DEFINITIONS
// ═══════════════════════════════════════════════════════════════════════════════

enum class ReflectTestId {
    // Section 1: Personality & Identity
    PERSONALITY_TYPE,
    TEMPERAMENT_TYPE,
    JUNGIAN_ARCHETYPE,
    MASCULINE_FEMININE_BALANCE,
    CHARISMA_LEVEL,
    SOCIAL_STYLE,
    EMPATHY_LEVEL,
    
    // Section 2: Emotional & Psychological Health
    EMOTIONAL_INTELLIGENCE,
    ANGER_SCALE,
    MOOD_SWINGS,
    OVERWHELM_SCALE,
    EMOTIONAL_BURNOUT,
    LOW_MOOD_CHECK,
    DEPRESSION_LEVEL,
    HAPPINESS_LEVEL,
    LONELINESS_SCALE,
    
    // Section 3: Childhood & Inner World
    CHILDHOOD_EXPERIENCES,
    INNER_CHILD,
    WOUNDED_INNER_CHILD,
    FATHER_IMPACT,
    MOTHER_IMPACT,
    
    // Section 4: Relationships & Attachment
    ATTACHMENT_STYLE,
    LOVE_LANGUAGE,
    PAST_RELATIONSHIP_IMPACT,
    TOXIC_PARTNER_CHECK,
    GASLIGHTING_CHECK,
    TOXIC_SIBLING_DYNAMICS,
    
    // Section 5: Trauma, Patterns & Coping
    PAST_TRAUMA_ASSESSMENT,
    IMPOSTER_SYNDROME,
    PEOPLE_PLEASING_CHECK,
    BODY_IMAGE_ISSUES,
    FOCUS_SKILLS,
    
    // Section 6: Toxicity, Manipulation & Narcissism
    MANIPULATION_TENDENCY,
    NARCISSISTIC_TRAITS,
    NARCISSISTIC_TYPE,
    NARCISSISTIC_IMPACT,
    TOXIC_TRAITS_ASSESSMENT,
    
    // Section 7: Life Direction & Growth
    CAREER_GUIDANCE
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION TYPES
// ═══════════════════════════════════════════════════════════════════════════════

enum class QuestionType {
    YES_NO_SOMETIMES,      // Three options: Yes, No, Sometimes
    LIKERT_5,              // Scale of 1-5
    LIKERT_7,              // Scale of 1-7 (for nuanced tests)
    MULTIPLE_CHOICE,       // 4-6 options
    SHORT_TEXT             // Optional reflective input
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION DATA
// ═══════════════════════════════════════════════════════════════════════════════

data class TestQuestion(
    val id: Int,
    val text: String,
    val type: QuestionType,
    val options: List<String> = emptyList(), // For multiple choice
    val likertLabels: Pair<String, String> = "" to "", // Low/High labels for Likert
    val category: String = "" // For scoring categorization
)

// ═══════════════════════════════════════════════════════════════════════════════
// TEST METADATA
// ═══════════════════════════════════════════════════════════════════════════════

data class ReflectTest(
    val id: ReflectTestId,
    val section: ReflectSection,
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val questionCount: Int,
    val estimatedMinutes: IntRange,
    val accentColorHex: Long,
    val iconName: String,
    val safetyNote: String? = null, // For sensitive tests
    val questions: List<TestQuestion> = emptyList()
)

// ═══════════════════════════════════════════════════════════════════════════════
// RESULT DATA
// ═══════════════════════════════════════════════════════════════════════════════

data class TestResult(
    val testId: ReflectTestId,
    val primaryScore: Float, // 0-100 scale
    val categories: Map<String, Float> = emptyMap(), // Category scores for charts
    val primaryLabel: String, // e.g., "Secure Attachment", "High Empathy"
    val description: String,
    val insights: List<String> = emptyList(),
    val reflection: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ═══════════════════════════════════════════════════════════════════════════════
// CHART TYPES FOR RESULTS
// ═══════════════════════════════════════════════════════════════════════════════

enum class ResultChartType {
    PIE_CHART,           // For category distributions
    RADIAL_CHART,        // For multi-dimensional scores
    BAR_CHART,           // For comparative metrics
    SEGMENTED_SCALE,     // For position on spectrum
    GAUGE_METER          // For single-value representation
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPLETE TEST DEFINITIONS
// ═══════════════════════════════════════════════════════════════════════════════

object ReflectTestRepository {
    
    val allTests: List<ReflectTest> = listOf(
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 1: PERSONALITY & IDENTITY
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.PERSONALITY_TYPE,
            section = ReflectSection.PERSONALITY,
            title = "Personality Type",
            shortDescription = "Discover your core personality traits",
            longDescription = "This assessment explores how you perceive the world, make decisions, and interact with others. Understanding your personality type can help you appreciate your unique strengths.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFB4A7D6,
            iconName = "person"
        ),
        
        ReflectTest(
            id = ReflectTestId.TEMPERAMENT_TYPE,
            section = ReflectSection.PERSONALITY,
            title = "Temperament Type",
            shortDescription = "Understand your natural disposition",
            longDescription = "Explore your innate temperament—the biological foundation of your personality that influences how you respond to situations and regulate emotions.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFE6B8AF,
            iconName = "psychology"
        ),
        
        ReflectTest(
            id = ReflectTestId.JUNGIAN_ARCHETYPE,
            section = ReflectSection.PERSONALITY,
            title = "Jungian Archetype",
            shortDescription = "Find your inner archetype",
            longDescription = "Based on Carl Jung's theory, discover which universal archetype most resonates with your inner self and life journey.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFD5A6BD,
            iconName = "theater_comedy"
        ),
        
        ReflectTest(
            id = ReflectTestId.MASCULINE_FEMININE_BALANCE,
            section = ReflectSection.PERSONALITY,
            title = "Masculine & Feminine Energy",
            shortDescription = "Explore your energy balance",
            longDescription = "Everyone has both masculine and feminine energies. This assessment helps you understand your current balance and how it influences your life.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFB7B7B7,
            iconName = "balance"
        ),
        
        ReflectTest(
            id = ReflectTestId.CHARISMA_LEVEL,
            section = ReflectSection.PERSONALITY,
            title = "Charisma Level",
            shortDescription = "Measure your natural magnetism",
            longDescription = "Charisma isn't just charm—it's presence, warmth, and power combined. Discover your unique charismatic strengths.",
            questionCount = 15,
            estimatedMinutes = 5..7,
            accentColorHex = 0xFFF9CB9C,
            iconName = "auto_awesome"
        ),
        
        ReflectTest(
            id = ReflectTestId.SOCIAL_STYLE,
            section = ReflectSection.PERSONALITY,
            title = "Social Style",
            shortDescription = "How do you navigate social situations?",
            longDescription = "Understanding your social style helps you communicate more effectively and build stronger relationships.",
            questionCount = 18,
            estimatedMinutes = 6..8,
            accentColorHex = 0xFF9FC5E8,
            iconName = "groups"
        ),
        
        ReflectTest(
            id = ReflectTestId.EMPATHY_LEVEL,
            section = ReflectSection.PERSONALITY,
            title = "Empathy Level",
            shortDescription = "Understand your empathic nature",
            longDescription = "Empathy is the ability to understand and share others' feelings. This assessment explores both cognitive and emotional empathy.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFB6D7A8,
            iconName = "favorite"
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 2: EMOTIONAL & PSYCHOLOGICAL HEALTH
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.EMOTIONAL_INTELLIGENCE,
            section = ReflectSection.EMOTIONAL,
            title = "Emotional Intelligence",
            shortDescription = "Assess your EQ",
            longDescription = "Emotional intelligence is the ability to recognize, understand, and manage emotions—both your own and others'. This is a key predictor of success and wellbeing.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFF93C47D,
            iconName = "psychology"
        ),
        
        ReflectTest(
            id = ReflectTestId.ANGER_SCALE,
            section = ReflectSection.EMOTIONAL,
            title = "Anger Scale",
            shortDescription = "Understand your anger patterns",
            longDescription = "Anger is a natural emotion, but understanding how you experience and express it can help you respond more skillfully.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFE06666,
            iconName = "whatshot"
        ),
        
        ReflectTest(
            id = ReflectTestId.MOOD_SWINGS,
            section = ReflectSection.EMOTIONAL,
            title = "Mood Swings Screening",
            shortDescription = "Explore emotional variability",
            longDescription = "This assessment helps you understand patterns in your mood changes and their potential impact on daily life.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFFFD966,
            iconName = "trending_up"
        ),
        
        ReflectTest(
            id = ReflectTestId.OVERWHELM_SCALE,
            section = ReflectSection.EMOTIONAL,
            title = "Overwhelm Scale",
            shortDescription = "How overwhelmed are you feeling?",
            longDescription = "Modern life can feel overwhelming. This assessment helps you gauge your current stress levels and identify areas for support.",
            questionCount = 15,
            estimatedMinutes = 5..7,
            accentColorHex = 0xFFCFE2F3,
            iconName = "waves"
        ),
        
        ReflectTest(
            id = ReflectTestId.EMOTIONAL_BURNOUT,
            section = ReflectSection.EMOTIONAL,
            title = "Emotional Burnout",
            shortDescription = "Assess emotional exhaustion",
            longDescription = "Burnout affects your energy, motivation, and wellbeing. Understanding where you stand can help you take restorative action.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFD9D2E9,
            iconName = "battery_alert"
        ),
        
        ReflectTest(
            id = ReflectTestId.LOW_MOOD_CHECK,
            section = ReflectSection.EMOTIONAL,
            title = "Low Mood Check",
            shortDescription = "A gentle check-in",
            longDescription = "This is a supportive assessment to help you understand your current emotional state. Remember, seeking understanding is a sign of strength.",
            questionCount = 15,
            estimatedMinutes = 5..7,
            accentColorHex = 0xFFB4A7D6,
            iconName = "cloud",
            safetyNote = "This is not a diagnostic tool. If you're struggling, please reach out to a mental health professional."
        ),
        
        ReflectTest(
            id = ReflectTestId.DEPRESSION_LEVEL,
            section = ReflectSection.EMOTIONAL,
            title = "Depression Level",
            shortDescription = "Screen for depressive symptoms",
            longDescription = "This assessment helps you understand if you might be experiencing symptoms of depression. Early awareness can lead to meaningful support.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFF6D9EEB,
            iconName = "healing",
            safetyNote = "This is a screening tool, not a diagnosis. Please consult a healthcare professional for proper evaluation."
        ),
        
        ReflectTest(
            id = ReflectTestId.HAPPINESS_LEVEL,
            section = ReflectSection.EMOTIONAL,
            title = "Happiness Level",
            shortDescription = "Measure your current wellbeing",
            longDescription = "Happiness is multifaceted. This assessment explores life satisfaction, positive emotions, and sense of purpose.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFF6B26B,
            iconName = "emoji_emotions"
        ),
        
        ReflectTest(
            id = ReflectTestId.LONELINESS_SCALE,
            section = ReflectSection.EMOTIONAL,
            title = "Loneliness Scale",
            shortDescription = "Understand your connection needs",
            longDescription = "Loneliness is about perceived social isolation, not just being alone. Understanding your experience can help you seek meaningful connection.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFA4C2F4,
            iconName = "person_outline"
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 3: CHILDHOOD & INNER WORLD
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.CHILDHOOD_EXPERIENCES,
            section = ReflectSection.CHILDHOOD,
            title = "Childhood Experiences",
            shortDescription = "Reflect on formative years",
            longDescription = "Our childhood shapes who we become. This gentle assessment helps you reflect on key experiences that may still influence you today.",
            questionCount = 20,
            estimatedMinutes = 8..12,
            accentColorHex = 0xFFEAD1DC,
            iconName = "child_care",
            safetyNote = "Take your time with these questions. It's okay to pause if memories feel difficult."
        ),
        
        ReflectTest(
            id = ReflectTestId.INNER_CHILD,
            section = ReflectSection.CHILDHOOD,
            title = "Inner Child",
            shortDescription = "Connect with your younger self",
            longDescription = "Your inner child carries your early emotions and needs. Understanding this part of yourself can bring healing and self-compassion.",
            questionCount = 18,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFF4CCCC,
            iconName = "favorite_border"
        ),
        
        ReflectTest(
            id = ReflectTestId.WOUNDED_INNER_CHILD,
            section = ReflectSection.CHILDHOOD,
            title = "Wounded Inner Child",
            shortDescription = "Explore unhealed childhood wounds",
            longDescription = "Sometimes our inner child carries pain from the past. This compassionate assessment helps you gently explore these areas for healing.",
            questionCount = 20,
            estimatedMinutes = 8..12,
            accentColorHex = 0xFFD9D2E9,
            iconName = "healing",
            safetyNote = "These questions may bring up difficult emotions. Be gentle with yourself and take breaks if needed."
        ),
        
        ReflectTest(
            id = ReflectTestId.FATHER_IMPACT,
            section = ReflectSection.CHILDHOOD,
            title = "Father Impact",
            shortDescription = "Understand paternal influence",
            longDescription = "Our relationship with our father or father figure shapes our sense of self, security, and relationships. This assessment explores that impact.",
            questionCount = 18,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFB7B7B7,
            iconName = "person",
            safetyNote = "This assessment is about understanding, not judging. Your experience is valid."
        ),
        
        ReflectTest(
            id = ReflectTestId.MOTHER_IMPACT,
            section = ReflectSection.CHILDHOOD,
            title = "Mother Impact",
            shortDescription = "Understand maternal influence",
            longDescription = "The mother-child bond is foundational. This assessment helps you understand how this relationship has shaped your emotional patterns.",
            questionCount = 18,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFEAD1DC,
            iconName = "person",
            safetyNote = "This assessment is about understanding, not judging. Your experience is valid."
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 4: RELATIONSHIPS & ATTACHMENT
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.ATTACHMENT_STYLE,
            section = ReflectSection.RELATIONSHIPS,
            title = "Attachment Style",
            shortDescription = "How do you bond with others?",
            longDescription = "Attachment styles—developed in childhood—affect how we connect in adult relationships. Understanding yours can transform your connections.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFF6B26B,
            iconName = "link"
        ),
        
        ReflectTest(
            id = ReflectTestId.LOVE_LANGUAGE,
            section = ReflectSection.RELATIONSHIPS,
            title = "Love Language",
            shortDescription = "How do you give and receive love?",
            longDescription = "We all express and feel love differently. Discover your primary love language to improve your relationships.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFE06666,
            iconName = "favorite"
        ),
        
        ReflectTest(
            id = ReflectTestId.PAST_RELATIONSHIP_IMPACT,
            section = ReflectSection.RELATIONSHIPS,
            title = "Past Relationship Impact",
            shortDescription = "How have past relationships shaped you?",
            longDescription = "Our past relationships leave imprints. This assessment helps you understand patterns and areas for growth.",
            questionCount = 18,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFD5A6BD,
            iconName = "history"
        ),
        
        ReflectTest(
            id = ReflectTestId.TOXIC_PARTNER_CHECK,
            section = ReflectSection.RELATIONSHIPS,
            title = "Toxic Partner Check",
            shortDescription = "Recognize unhealthy dynamics",
            longDescription = "It can be hard to see toxic patterns when you're in them. This supportive assessment helps you identify concerning behaviors in relationships.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFCC4125,
            iconName = "report_problem",
            safetyNote = "If you're in an unsafe situation, please reach out to a domestic violence helpline."
        ),
        
        ReflectTest(
            id = ReflectTestId.GASLIGHTING_CHECK,
            section = ReflectSection.RELATIONSHIPS,
            title = "Gaslighting Check",
            shortDescription = "Trust your reality",
            longDescription = "Gaslighting is a form of manipulation that makes you question your own perceptions. This assessment helps you identify if you've experienced this.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFE69138,
            iconName = "visibility_off",
            safetyNote = "Your perceptions are valid. Seeking clarity is a sign of strength."
        ),
        
        ReflectTest(
            id = ReflectTestId.TOXIC_SIBLING_DYNAMICS,
            section = ReflectSection.RELATIONSHIPS,
            title = "Toxic Sibling Dynamics",
            shortDescription = "Understand sibling relationships",
            longDescription = "Sibling relationships can be complex. This assessment helps you explore any unhealthy patterns that may still affect you.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFF9CB9C,
            iconName = "group"
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 5: TRAUMA, PATTERNS & COPING
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.PAST_TRAUMA_ASSESSMENT,
            section = ReflectSection.TRAUMA,
            title = "Past Trauma Assessment",
            shortDescription = "Gently explore past experiences",
            longDescription = "Trauma affects us in many ways. This compassionate assessment helps you understand if past experiences may still be impacting you.",
            questionCount = 20,
            estimatedMinutes = 8..12,
            accentColorHex = 0xFFB4A7D6,
            iconName = "healing",
            safetyNote = "These questions may bring up difficult feelings. Take your time and be gentle with yourself."
        ),
        
        ReflectTest(
            id = ReflectTestId.IMPOSTER_SYNDROME,
            section = ReflectSection.TRAUMA,
            title = "Imposter Syndrome",
            shortDescription = "Do you doubt your achievements?",
            longDescription = "Imposter syndrome makes us feel like frauds despite our accomplishments. Understanding this pattern is the first step to overcoming it.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFB7B7B7,
            iconName = "masks"
        ),
        
        ReflectTest(
            id = ReflectTestId.PEOPLE_PLEASING_CHECK,
            section = ReflectSection.TRAUMA,
            title = "People-Pleasing Check",
            shortDescription = "Do you put others first too much?",
            longDescription = "While kindness is beautiful, chronic people-pleasing can lead to burnout and resentment. Explore your patterns here.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFD5A6BD,
            iconName = "volunteer_activism"
        ),
        
        ReflectTest(
            id = ReflectTestId.BODY_IMAGE_ISSUES,
            section = ReflectSection.TRAUMA,
            title = "Body Image Issues",
            shortDescription = "How do you see yourself?",
            longDescription = "Body image affects self-esteem and daily life. This gentle assessment explores your relationship with your physical self.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFEAD1DC,
            iconName = "accessibility",
            safetyNote = "Your worth is not defined by appearance. This is about understanding, not judgment."
        ),
        
        ReflectTest(
            id = ReflectTestId.FOCUS_SKILLS,
            section = ReflectSection.TRAUMA,
            title = "Focus Skills",
            shortDescription = "Assess your concentration ability",
            longDescription = "In our distracted world, focus is a superpower. This assessment helps you understand your attention patterns and challenges.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFCFE2F3,
            iconName = "center_focus_strong"
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 6: TOXICITY, MANIPULATION & NARCISSISM
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.MANIPULATION_TENDENCY,
            section = ReflectSection.TOXICITY,
            title = "Manipulation Tendency",
            shortDescription = "Self-reflect on influence patterns",
            longDescription = "We all influence others, but some patterns can be harmful. This honest self-assessment helps you identify any concerning tendencies.",
            questionCount = 18,
            estimatedMinutes = 6..9,
            accentColorHex = 0xFFE06666,
            iconName = "psychology_alt",
            safetyNote = "Self-awareness is courageous. Identifying patterns is the first step to change."
        ),
        
        ReflectTest(
            id = ReflectTestId.NARCISSISTIC_TRAITS,
            section = ReflectSection.TOXICITY,
            title = "Narcissistic Traits",
            shortDescription = "Explore self-focused patterns",
            longDescription = "Everyone has some narcissistic traits—they exist on a spectrum. This assessment helps you understand where you fall.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFCC4125,
            iconName = "person_pin",
            safetyNote = "Healthy self-regard is important. This is about identifying problematic patterns, not shaming."
        ),
        
        ReflectTest(
            id = ReflectTestId.NARCISSISTIC_TYPE,
            section = ReflectSection.TOXICITY,
            title = "Narcissistic Type",
            shortDescription = "Understand different expressions",
            longDescription = "Narcissism presents in different ways—grandiose, vulnerable, communal. If you scored high on traits, understand your pattern.",
            questionCount = 16,
            estimatedMinutes = 5..8,
            accentColorHex = 0xFFE69138,
            iconName = "category"
        ),
        
        ReflectTest(
            id = ReflectTestId.NARCISSISTIC_IMPACT,
            section = ReflectSection.TOXICITY,
            title = "Narcissistic Impact",
            shortDescription = "How have narcissists affected you?",
            longDescription = "Being close to someone with narcissistic traits can leave lasting marks. This assessment helps you understand and heal from that impact.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFD5A6BD,
            iconName = "heart_broken",
            safetyNote = "Your experiences are valid. Healing is possible."
        ),
        
        ReflectTest(
            id = ReflectTestId.TOXIC_TRAITS_ASSESSMENT,
            section = ReflectSection.TOXICITY,
            title = "Toxic Traits Assessment",
            shortDescription = "Honest self-reflection",
            longDescription = "We all have shadow aspects. This brave self-assessment helps you identify behaviors that may be harming you or others.",
            questionCount = 20,
            estimatedMinutes = 7..10,
            accentColorHex = 0xFFB7B7B7,
            iconName = "self_improvement",
            safetyNote = "Recognizing our shadows is the path to growth. This takes courage."
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // SECTION 7: LIFE DIRECTION & GROWTH
        // ─────────────────────────────────────────────────────────────────────────
        
        ReflectTest(
            id = ReflectTestId.CAREER_GUIDANCE,
            section = ReflectSection.GROWTH,
            title = "Career Guidance",
            shortDescription = "Find your professional path",
            longDescription = "Align your career with your values, strengths, and aspirations. This comprehensive assessment helps you discover fulfilling directions.",
            questionCount = 25,
            estimatedMinutes = 10..15,
            accentColorHex = 0xFF93C47D,
            iconName = "work"
        )
    )
    
    // Helper functions
    fun getTestById(id: ReflectTestId): ReflectTest? = allTests.find { it.id == id }
    
    fun getTestsBySection(section: ReflectSection): List<ReflectTest> = 
        allTests.filter { it.section == section }
    
    fun getSections(): List<ReflectSection> = ReflectSection.entries
}
