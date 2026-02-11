package com.example.aroura.data

import androidx.compose.ui.graphics.Color
import com.example.aroura.ui.theme.*

/**
 * Uplifting Quiz Data
 * 
 * Contains 3 beautiful, positive quizzes designed to:
 * 1. Help users discover their hidden strengths
 * 2. Provide uplifting insights
 * 3. Encourage self-compassion and growth
 * 
 * Each quiz has 10 questions with multiple choice answers.
 * Results are encouraging and strength-based.
 */

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZ DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════════

data class UpliftingQuiz(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val accentColor: Long,
    val description: String,
    val questions: List<QuizQuestion>,
    val resultCategories: List<QuizResultCategory>
)

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<QuizOption>
)

data class QuizOption(
    val text: String,
    val category: String, // Maps to result category
    val points: Int = 1
)

data class QuizResultCategory(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val strengths: List<String>,
    val affirmation: String,
    val accentColor: Long
)

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZ REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

object UpliftingQuizRepository {
    
    val allQuizzes: List<UpliftingQuiz> = listOf(
        // ─────────────────────────────────────────────────────────────────────────
        // QUIZ 1: WHAT'S YOUR SUPERPOWER?
        // ─────────────────────────────────────────────────────────────────────────
        UpliftingQuiz(
            id = "superpower",
            title = "What's Your Superpower?",
            subtitle = "Discover your hidden strength",
            emoji = "✨",
            accentColor = 0xFFFFB74D, // CalmingPeach
            description = "Everyone has a unique gift that makes them special. This quiz will help you discover your inner superpower – the natural strength that you bring to the world.",
            questions = listOf(
                QuizQuestion(
                    id = 1,
                    text = "When a friend is going through a tough time, you typically...",
                    options = listOf(
                        QuizOption("Listen patiently and offer comfort", "empathy"),
                        QuizOption("Help them see the bright side", "optimism"),
                        QuizOption("Come up with practical solutions", "problem_solving"),
                        QuizOption("Make them laugh to lighten the mood", "humor")
                    )
                ),
                QuizQuestion(
                    id = 2,
                    text = "In a group project, you naturally take on the role of...",
                    options = listOf(
                        QuizOption("The one who makes sure everyone feels included", "empathy"),
                        QuizOption("The cheerleader who keeps spirits high", "optimism"),
                        QuizOption("The organizer who creates the action plan", "problem_solving"),
                        QuizOption("The one who keeps things fun and light", "humor")
                    )
                ),
                QuizQuestion(
                    id = 3,
                    text = "What do people most often come to you for?",
                    options = listOf(
                        QuizOption("A shoulder to lean on", "empathy"),
                        QuizOption("Words of encouragement", "optimism"),
                        QuizOption("Advice on how to fix things", "problem_solving"),
                        QuizOption("A good laugh and fun times", "humor")
                    )
                ),
                QuizQuestion(
                    id = 4,
                    text = "When facing a challenge, your first instinct is to...",
                    options = listOf(
                        QuizOption("Consider how it affects everyone involved", "empathy"),
                        QuizOption("Focus on what good could come from it", "optimism"),
                        QuizOption("Break it down into manageable steps", "problem_solving"),
                        QuizOption("Find the absurdity in the situation", "humor")
                    )
                ),
                QuizQuestion(
                    id = 5,
                    text = "Your ideal way to spend free time would be...",
                    options = listOf(
                        QuizOption("Having deep, meaningful conversations", "empathy"),
                        QuizOption("Planning exciting future adventures", "optimism"),
                        QuizOption("Working on a puzzle or creative project", "problem_solving"),
                        QuizOption("Watching comedy or playing games", "humor")
                    )
                ),
                QuizQuestion(
                    id = 6,
                    text = "When someone shares good news with you, you...",
                    options = listOf(
                        QuizOption("Feel genuinely happy for them", "empathy"),
                        QuizOption("Get excited and celebrate with them", "optimism"),
                        QuizOption("Ask about their plans and next steps", "problem_solving"),
                        QuizOption("Crack a joke to add to the joy", "humor")
                    )
                ),
                QuizQuestion(
                    id = 7,
                    text = "What quality do you value most in yourself?",
                    options = listOf(
                        QuizOption("My ability to understand others", "empathy"),
                        QuizOption("My hopeful outlook on life", "optimism"),
                        QuizOption("My logical thinking", "problem_solving"),
                        QuizOption("My ability to bring joy", "humor")
                    )
                ),
                QuizQuestion(
                    id = 8,
                    text = "In a difficult conversation, you tend to...",
                    options = listOf(
                        QuizOption("Focus on understanding the other person's feelings", "empathy"),
                        QuizOption("Look for common ground and positive outcomes", "optimism"),
                        QuizOption("Stick to facts and find solutions", "problem_solving"),
                        QuizOption("Use gentle humor to ease tension", "humor")
                    )
                ),
                QuizQuestion(
                    id = 9,
                    text = "What kind of compliment makes you feel best?",
                    options = listOf(
                        QuizOption("'You really understand me'", "empathy"),
                        QuizOption("'You always make things feel possible'", "optimism"),
                        QuizOption("'You're so smart and capable'", "problem_solving"),
                        QuizOption("'You always make me smile'", "humor")
                    )
                ),
                QuizQuestion(
                    id = 10,
                    text = "If you could give one gift to the world, it would be...",
                    options = listOf(
                        QuizOption("More compassion and understanding", "empathy"),
                        QuizOption("More hope and positivity", "optimism"),
                        QuizOption("Better solutions to problems", "problem_solving"),
                        QuizOption("More laughter and joy", "humor")
                    )
                )
            ),
            resultCategories = listOf(
                QuizResultCategory(
                    id = "empathy",
                    title = "The Compassionate Soul",
                    emoji = "💜",
                    description = "Your superpower is your deep capacity for empathy. You have an extraordinary ability to sense what others are feeling and make them feel truly seen and understood.",
                    strengths = listOf(
                        "Deep emotional intelligence",
                        "Natural ability to comfort others",
                        "Creates safe spaces for vulnerability",
                        "Builds genuine, lasting connections"
                    ),
                    affirmation = "Your ability to understand others is a rare and beautiful gift. The world needs more compassionate souls like you.",
                    accentColor = 0xFFCE93D8
                ),
                QuizResultCategory(
                    id = "optimism",
                    title = "The Light Bearer",
                    emoji = "☀️",
                    description = "Your superpower is your infectious optimism. You have the remarkable ability to see possibility where others see obstacles, spreading hope wherever you go.",
                    strengths = listOf(
                        "Inspires hope in difficult times",
                        "Sees opportunities in challenges",
                        "Naturally uplifting presence",
                        "Resilient in the face of setbacks"
                    ),
                    affirmation = "Your bright spirit illuminates the path for others. Never underestimate the power of your hope.",
                    accentColor = 0xFFFFD54F
                ),
                QuizResultCategory(
                    id = "problem_solving",
                    title = "The Wise Navigator",
                    emoji = "🧭",
                    description = "Your superpower is your brilliant problem-solving mind. You have an amazing ability to see through complexity and find paths forward that others might miss.",
                    strengths = listOf(
                        "Analytical thinking",
                        "Calm under pressure",
                        "Creates effective strategies",
                        "Helps others find clarity"
                    ),
                    affirmation = "Your clear thinking is a beacon in stormy seas. Your ability to solve problems brings peace to many.",
                    accentColor = 0xFF81D4FA
                ),
                QuizResultCategory(
                    id = "humor",
                    title = "The Joy Bringer",
                    emoji = "🌈",
                    description = "Your superpower is your gift of humor and joy. You have the magical ability to lighten hearts and bring smiles to faces, even in the darkest moments.",
                    strengths = listOf(
                        "Transforms tense situations",
                        "Brings people together through laughter",
                        "Helps others cope with stress",
                        "Creates memorable, happy moments"
                    ),
                    affirmation = "Your laughter is medicine for the soul. The joy you bring is more powerful than you know.",
                    accentColor = 0xFFA5D6A7
                )
            )
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // QUIZ 2: SELF-CARE STYLE
        // ─────────────────────────────────────────────────────────────────────────
        UpliftingQuiz(
            id = "selfcare",
            title = "Self-Care Style",
            subtitle = "Find your path to peace",
            emoji = "🌿",
            accentColor = 0xFF81C784, // CalmingGreen
            description = "Understanding how you naturally recharge can transform your well-being. Discover which self-care approach aligns best with your soul.",
            questions = listOf(
                QuizQuestion(
                    id = 1,
                    text = "After a long, tiring day, you feel most restored by...",
                    options = listOf(
                        QuizOption("Going for a walk or doing yoga", "physical"),
                        QuizOption("Reading, journaling, or meditating", "mindful"),
                        QuizOption("Calling a friend or spending time with loved ones", "social"),
                        QuizOption("Doing something creative like art or music", "creative")
                    )
                ),
                QuizQuestion(
                    id = 2,
                    text = "When you're feeling overwhelmed, what helps most?",
                    options = listOf(
                        QuizOption("Physical activity to release tension", "physical"),
                        QuizOption("Quiet time alone to process", "mindful"),
                        QuizOption("Talking it out with someone you trust", "social"),
                        QuizOption("Expressing yourself through a creative outlet", "creative")
                    )
                ),
                QuizQuestion(
                    id = 3,
                    text = "Your ideal self-care Sunday would include...",
                    options = listOf(
                        QuizOption("A hike, swim, or home workout", "physical"),
                        QuizOption("A bubble bath and good book", "mindful"),
                        QuizOption("Brunch with close friends", "social"),
                        QuizOption("A creative project or crafting", "creative")
                    )
                ),
                QuizQuestion(
                    id = 4,
                    text = "What type of vacation sounds most refreshing?",
                    options = listOf(
                        QuizOption("Adventure trip with hiking or sports", "physical"),
                        QuizOption("Peaceful retreat with meditation", "mindful"),
                        QuizOption("Trip with friends or family", "social"),
                        QuizOption("Cultural trip visiting museums and galleries", "creative")
                    )
                ),
                QuizQuestion(
                    id = 5,
                    text = "When you need a quick mood boost, you...",
                    options = listOf(
                        QuizOption("Dance, stretch, or go outside", "physical"),
                        QuizOption("Take deep breaths and center yourself", "mindful"),
                        QuizOption("Text or call someone you love", "social"),
                        QuizOption("Listen to music or doodle", "creative")
                    )
                ),
                QuizQuestion(
                    id = 6,
                    text = "What environment makes you feel most at peace?",
                    options = listOf(
                        QuizOption("Nature - forests, beaches, mountains", "physical"),
                        QuizOption("Cozy, quiet spaces at home", "mindful"),
                        QuizOption("Warm gatherings with loved ones", "social"),
                        QuizOption("Inspiring spaces full of art and beauty", "creative")
                    )
                ),
                QuizQuestion(
                    id = 7,
                    text = "How do you prefer to express gratitude?",
                    options = listOf(
                        QuizOption("Through actions - helping or doing", "physical"),
                        QuizOption("Through reflection and journaling", "mindful"),
                        QuizOption("Through words and meaningful conversations", "social"),
                        QuizOption("Through gifts or creative expressions", "creative")
                    )
                ),
                QuizQuestion(
                    id = 8,
                    text = "What helps you sleep better?",
                    options = listOf(
                        QuizOption("Evening exercise or stretching", "physical"),
                        QuizOption("Meditation or calming routines", "mindful"),
                        QuizOption("Feeling connected to loved ones", "social"),
                        QuizOption("Listening to ambient music or sounds", "creative")
                    )
                ),
                QuizQuestion(
                    id = 9,
                    text = "When stressed, what's your go-to comfort?",
                    options = listOf(
                        QuizOption("Physical comfort - warm bath, cozy blanket", "physical"),
                        QuizOption("Mental comfort - breathwork, visualization", "mindful"),
                        QuizOption("Emotional comfort - support from others", "social"),
                        QuizOption("Creative comfort - music, art, games", "creative")
                    )
                ),
                QuizQuestion(
                    id = 10,
                    text = "What does 'treating yourself' look like?",
                    options = listOf(
                        QuizOption("A spa day or massage", "physical"),
                        QuizOption("A peaceful day of doing nothing", "mindful"),
                        QuizOption("A special outing with favorite people", "social"),
                        QuizOption("Buying art supplies or attending a show", "creative")
                    )
                )
            ),
            resultCategories = listOf(
                QuizResultCategory(
                    id = "physical",
                    title = "The Body Nurturer",
                    emoji = "🏃",
                    description = "You recharge best through physical self-care. Your body and mind are deeply connected, and movement is your medicine.",
                    strengths = listOf(
                        "Naturally attuned to your body's needs",
                        "Finds peace through physical activity",
                        "Uses movement to process emotions",
                        "Appreciates the healing power of nature"
                    ),
                    affirmation = "Your body is wise. Trust its need for movement, rest, and connection with the physical world.",
                    accentColor = 0xFFFFB74D
                ),
                QuizResultCategory(
                    id = "mindful",
                    title = "The Peaceful Seeker",
                    emoji = "🧘",
                    description = "You recharge best through mindful self-care. Quiet reflection and inner peace are essential to your well-being.",
                    strengths = listOf(
                        "Deep self-awareness",
                        "Natural ability to find inner calm",
                        "Appreciates stillness and silence",
                        "Processes life through reflection"
                    ),
                    affirmation = "Your inner world is rich and beautiful. The peace you cultivate within radiates outward to others.",
                    accentColor = 0xFFB39DDB
                ),
                QuizResultCategory(
                    id = "social",
                    title = "The Connection Seeker",
                    emoji = "💕",
                    description = "You recharge best through social self-care. Meaningful connections feed your soul and restore your energy.",
                    strengths = listOf(
                        "Thrives on genuine connection",
                        "Feels energized by loved ones",
                        "Builds supportive relationships",
                        "Heals through sharing and vulnerability"
                    ),
                    affirmation = "Your heart needs connection, and that's beautiful. The love you give returns to you multiplied.",
                    accentColor = 0xFFF48FB1
                ),
                QuizResultCategory(
                    id = "creative",
                    title = "The Creative Soul",
                    emoji = "🎨",
                    description = "You recharge best through creative self-care. Expression and beauty are essential to your well-being.",
                    strengths = listOf(
                        "Processes emotions through creation",
                        "Finds joy in artistic expression",
                        "Appreciates beauty in all forms",
                        "Transforms feelings into art"
                    ),
                    affirmation = "Your creative spirit is a gift. Every time you create, you heal yourself and inspire others.",
                    accentColor = 0xFF4DD0E1
                )
            )
        ),
        
        // ─────────────────────────────────────────────────────────────────────────
        // QUIZ 3: YOUR INNER ANIMAL
        // ─────────────────────────────────────────────────────────────────────────
        UpliftingQuiz(
            id = "inner_animal",
            title = "Your Inner Animal",
            subtitle = "Meet your spirit guide",
            emoji = "🦋",
            accentColor = 0xFFCE93D8, // CalmingLavender
            description = "In many traditions, spirit animals represent our inner nature and guide our path. Discover which animal energy resonates with your soul.",
            questions = listOf(
                QuizQuestion(
                    id = 1,
                    text = "In social situations, you're most likely to...",
                    options = listOf(
                        QuizOption("Lead the conversation and energize the room", "lion"),
                        QuizOption("Observe quietly before engaging", "owl"),
                        QuizOption("Adapt to whatever the group needs", "dolphin"),
                        QuizOption("Stay cozy in your small circle", "bear")
                    )
                ),
                QuizQuestion(
                    id = 2,
                    text = "When making important decisions, you rely on...",
                    options = listOf(
                        QuizOption("Courage and gut instinct", "lion"),
                        QuizOption("Careful analysis and wisdom", "owl"),
                        QuizOption("Intuition and emotional intelligence", "dolphin"),
                        QuizOption("Patient consideration and grounding", "bear")
                    )
                ),
                QuizQuestion(
                    id = 3,
                    text = "Your ideal role in life would be...",
                    options = listOf(
                        QuizOption("A leader inspiring others to greatness", "lion"),
                        QuizOption("A wise guide helping others find answers", "owl"),
                        QuizOption("A connector bringing joy and harmony", "dolphin"),
                        QuizOption("A protector creating safe, nurturing spaces", "bear")
                    )
                ),
                QuizQuestion(
                    id = 4,
                    text = "What quality are you most proud of?",
                    options = listOf(
                        QuizOption("My courage to face challenges head-on", "lion"),
                        QuizOption("My ability to see what others miss", "owl"),
                        QuizOption("My playful spirit and emotional depth", "dolphin"),
                        QuizOption("My strength and ability to nurture", "bear")
                    )
                ),
                QuizQuestion(
                    id = 5,
                    text = "When stressed, you need...",
                    options = listOf(
                        QuizOption("Space to take charge and act", "lion"),
                        QuizOption("Solitude to think and reflect", "owl"),
                        QuizOption("Connection and playful distraction", "dolphin"),
                        QuizOption("Comfort, rest, and grounding", "bear")
                    )
                ),
                QuizQuestion(
                    id = 6,
                    text = "People often describe you as...",
                    options = listOf(
                        QuizOption("Confident and inspiring", "lion"),
                        QuizOption("Wise and insightful", "owl"),
                        QuizOption("Joyful and empathetic", "dolphin"),
                        QuizOption("Warm and protective", "bear")
                    )
                ),
                QuizQuestion(
                    id = 7,
                    text = "Your communication style is...",
                    options = listOf(
                        QuizOption("Direct and assertive", "lion"),
                        QuizOption("Thoughtful and precise", "owl"),
                        QuizOption("Warm and expressive", "dolphin"),
                        QuizOption("Gentle and reassuring", "bear")
                    )
                ),
                QuizQuestion(
                    id = 8,
                    text = "What motivates you most?",
                    options = listOf(
                        QuizOption("Achieving goals and leading by example", "lion"),
                        QuizOption("Understanding the deeper meaning of things", "owl"),
                        QuizOption("Creating joy and connection", "dolphin"),
                        QuizOption("Protecting and nurturing those I love", "bear")
                    )
                ),
                QuizQuestion(
                    id = 9,
                    text = "Your approach to conflict is...",
                    options = listOf(
                        QuizOption("Face it directly with strength", "lion"),
                        QuizOption("Analyze and strategize carefully", "owl"),
                        QuizOption("Seek harmony and understanding", "dolphin"),
                        QuizOption("Set firm boundaries but avoid aggression", "bear")
                    )
                ),
                QuizQuestion(
                    id = 10,
                    text = "What gift do you bring to your relationships?",
                    options = listOf(
                        QuizOption("Strength and protection", "lion"),
                        QuizOption("Wisdom and perspective", "owl"),
                        QuizOption("Joy and emotional support", "dolphin"),
                        QuizOption("Comfort and unwavering loyalty", "bear")
                    )
                )
            ),
            resultCategories = listOf(
                QuizResultCategory(
                    id = "lion",
                    title = "The Brave Lion",
                    emoji = "🦁",
                    description = "Your inner animal is the majestic Lion. You carry the energy of courage, leadership, and noble strength within you.",
                    strengths = listOf(
                        "Natural leader who inspires others",
                        "Courageous in facing challenges",
                        "Protective of those you love",
                        "Confident in your convictions"
                    ),
                    affirmation = "Like the lion, you are born to lead with courage and heart. Your strength uplifts everyone around you.",
                    accentColor = 0xFFFFB74D
                ),
                QuizResultCategory(
                    id = "owl",
                    title = "The Wise Owl",
                    emoji = "🦉",
                    description = "Your inner animal is the mystical Owl. You carry the energy of wisdom, intuition, and deep seeing within you.",
                    strengths = listOf(
                        "Sees beyond surface appearances",
                        "Thoughtful decision maker",
                        "Wise counselor to others",
                        "Comfortable with mystery and the unknown"
                    ),
                    affirmation = "Like the owl, you see what others cannot. Your wisdom is a gift that lights the way in darkness.",
                    accentColor = 0xFF90A4AE
                ),
                QuizResultCategory(
                    id = "dolphin",
                    title = "The Joyful Dolphin",
                    emoji = "🐬",
                    description = "Your inner animal is the playful Dolphin. You carry the energy of joy, connection, and emotional intelligence within you.",
                    strengths = listOf(
                        "Brings joy wherever you go",
                        "Deeply empathetic and understanding",
                        "Naturally builds community",
                        "Balances playfulness with depth"
                    ),
                    affirmation = "Like the dolphin, you bring light and connection to the world. Your joy is contagious and healing.",
                    accentColor = 0xFF4FC3F7
                ),
                QuizResultCategory(
                    id = "bear",
                    title = "The Nurturing Bear",
                    emoji = "🐻",
                    description = "Your inner animal is the powerful Bear. You carry the energy of protection, groundedness, and nurturing strength within you.",
                    strengths = listOf(
                        "Strong protector of loved ones",
                        "Grounded and stable presence",
                        "Nurturing and caring nature",
                        "Knows when to rest and when to act"
                    ),
                    affirmation = "Like the bear, you are both strong and gentle. Your protective love creates safe havens for others.",
                    accentColor = 0xFFA1887F
                )
            )
        )
    )
    
    /**
     * Get a quiz by its ID
     */
    fun getQuizById(id: String): UpliftingQuiz? = allQuizzes.find { it.id == id }
    
    /**
     * Calculate quiz result based on answers
     * Returns the category with the highest score
     */
    fun calculateResult(quiz: UpliftingQuiz, answers: Map<Int, String>): QuizResultCategory? {
        if (answers.isEmpty()) return null
        
        val categoryScores = mutableMapOf<String, Int>()
        
        answers.forEach { (questionId, categoryId) ->
            categoryScores[categoryId] = (categoryScores[categoryId] ?: 0) + 1
        }
        
        val winningCategoryId = categoryScores.maxByOrNull { it.value }?.key
        return quiz.resultCategories.find { it.id == winningCategoryId }
    }
}
