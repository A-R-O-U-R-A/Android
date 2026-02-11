package com.example.aroura.data.api

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

/**
 * Reflect API Service - Retrofit Interface
 * 
 * Handles:
 * - Home mood check-ins
 * - Routine tracking
 * - Quest progress
 * - Test results
 * - Quiz results
 * - Liked songs
 * - Daily affirmations
 */
interface ReflectApiService {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Daily Affirmation
    // ═══════════════════════════════════════════════════════════════════════════
    
    @GET("affirmation/daily")
    suspend fun getDailyAffirmation(
        @Query("mood") mood: String? = null
    ): Response<AffirmationResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Home Mood Check-In
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/home-mood")
    suspend fun saveHomeMood(@Body request: SaveHomeMoodRequest): Response<HomeMoodResponse>
    
    @GET("reflect/home-mood")
    suspend fun getHomeMoodHistory(
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): Response<HomeMoodHistoryResponse>
    
    @GET("reflect/home-mood/today")
    suspend fun getTodayMood(): Response<TodayMoodResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Routine Tracking
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/routine/complete")
    suspend fun completeRoutineTask(@Body request: CompleteRoutineRequest): Response<RoutineCompletionResponse>
    
    @GET("reflect/routine/completions")
    suspend fun getRoutineCompletions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<RoutineCompletionsResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Calm Anxiety Entries
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/calm-anxiety")
    suspend fun saveCalmAnxietyEntry(@Body request: SaveCalmAnxietyRequest): Response<CalmAnxietyResponse>
    
    @GET("reflect/calm-anxiety")
    suspend fun getCalmAnxietyHistory(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): Response<CalmAnxietyHistoryResponse>
    
    @GET("reflect/calm-anxiety/{entryId}")
    suspend fun getCalmAnxietyEntry(@Path("entryId") entryId: String): Response<CalmAnxietyResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Self-Discovery Quest
    // ═══════════════════════════════════════════════════════════════════════════
    
    @GET("reflect/quest/progress")
    suspend fun getQuestProgress(): Response<QuestProgressResponse>
    
    @POST("reflect/quest/complete-test")
    suspend fun completeQuestTest(@Body request: CompleteQuestTestRequest): Response<QuestProgressResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Test Results
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/test-results")
    suspend fun saveTestResult(@Body request: SaveTestResultRequest): Response<TestResultResponse>
    
    @GET("reflect/test-results")
    suspend fun getTestResults(
        @Query("testId") testId: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<TestResultsResponse>
    
    @GET("reflect/test-results/summary")
    suspend fun getTestResultsSummary(): Response<TestResultsSummaryResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Quiz Results
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/quiz-results")
    suspend fun saveQuizResult(@Body request: SaveQuizResultRequest): Response<QuizResultResponse>
    
    @GET("reflect/quiz-results")
    suspend fun getQuizResults(): Response<QuizResultsResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Liked Songs
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("reflect/liked-songs")
    suspend fun likeSong(@Body request: LikeSongRequest): Response<LikedSongResponse>
    
    @DELETE("reflect/liked-songs/{songId}")
    suspend fun unlikeSong(
        @Path("songId") songId: String,
        @Query("source") source: String? = null
    ): Response<SuccessResponse>
    
    @GET("reflect/liked-songs")
    suspend fun getLikedSongs(
        @Query("source") source: String? = null
    ): Response<LikedSongsResponse>
    
    @GET("reflect/liked-songs/random")
    suspend fun getRandomLikedSong(): Response<RandomLikedSongResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Combined Reflect Summary
    // ═══════════════════════════════════════════════════════════════════════════
    
    @GET("reflect/summary")
    suspend fun getReflectSummary(): Response<ReflectSummaryResponse>
}

// ═══════════════════════════════════════════════════════════════════════════════
// Request/Response Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class AffirmationResponse(
    val success: Boolean,
    val affirmation: String,
    val cached: Boolean = false,
    val generatedAt: Long = 0,
    val ai: Boolean = false
)

@Serializable
data class SaveHomeMoodRequest(
    val moodIndex: Int,
    val moodLabel: String,
    val moodEmoji: String,
    val note: String = ""
)

@Serializable
data class HomeMoodData(
    val id: String,
    val moodIndex: Int,
    val moodLabel: String,
    val moodEmoji: String,
    val note: String,
    val createdAt: String
)

@Serializable
data class HomeMoodResponse(
    val success: Boolean,
    val mood: HomeMoodData
)

@Serializable
data class HomeMoodHistoryResponse(
    val success: Boolean,
    val moods: List<HomeMoodData>,
    val pagination: PaginationInfo
)

@Serializable
data class TodayMoodResponse(
    val success: Boolean,
    val mood: HomeMoodData?
)

@Serializable
data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)

@Serializable
data class CompleteRoutineRequest(
    val taskId: String,
    val category: String,
    val title: String
)

@Serializable
data class RoutineCompletionData(
    val id: String,
    val taskId: String,
    val category: String,
    val title: String,
    val completedDate: String,
    val completedAt: String
)

@Serializable
data class RoutineCompletionResponse(
    val success: Boolean,
    val completion: RoutineCompletionData
)

@Serializable
data class RoutineCompletionsResponse(
    val success: Boolean,
    val completions: Map<String, List<RoutineCompletionData>>
)

@Serializable
data class CompletedTestData(
    val testId: String,
    val completedAt: String,
    val resultId: String
)

@Serializable
data class QuestProgressData(
    val completedCount: Int,
    val totalRequired: Int,
    val completedTests: List<CompletedTestData> = emptyList(),
    val badgeEarned: Boolean = false,
    val badgeType: String? = null,
    val badgeEarnedAt: String? = null,
    val newBadge: Boolean = false,
    val message: String? = null
)

@Serializable
data class QuestProgressResponse(
    val success: Boolean,
    val progress: QuestProgressData
)

@Serializable
data class CompleteQuestTestRequest(
    val testId: String,
    val resultId: String
)

@Serializable
data class SaveTestResultRequest(
    val testId: String,
    val testTitle: String,
    val primaryScore: Float,
    val categories: Map<String, Float> = emptyMap(),
    val primaryLabel: String,
    val description: String = "",
    val insights: List<String> = emptyList(),
    val reflection: String = "",
    val answers: Map<String, String> = emptyMap()
)

@Serializable
data class TestResultData(
    val id: String,
    val testId: String,
    val testTitle: String,
    val primaryScore: Float,
    val primaryLabel: String,
    val description: String = "",
    val insights: List<String> = emptyList(),
    val categories: Map<String, Float> = emptyMap(),
    val completedAt: String
)

@Serializable
data class TestResultResponse(
    val success: Boolean,
    val result: TestResultData
)

@Serializable
data class TestResultsResponse(
    val success: Boolean,
    val results: List<TestResultData>
)

@Serializable
data class TestResultSummaryData(
    val testId: String,
    val testTitle: String,
    val completedCount: Int,
    val lastCompleted: String,
    val latestScore: Float,
    val latestLabel: String
)

@Serializable
data class TestResultsSummaryResponse(
    val success: Boolean,
    val completedCount: Int,
    val tests: List<TestResultSummaryData>
)

@Serializable
data class QuizAnswerData(
    val questionIndex: Int,
    val selectedOption: Int,
    val isCorrect: Boolean
)

@Serializable
data class SaveQuizResultRequest(
    val quizId: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val resultMessage: String = "",
    val answers: List<QuizAnswerData> = emptyList()
)

@Serializable
data class QuizResultData(
    val id: String,
    val quizId: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val resultMessage: String = "",
    val completedAt: String
)

@Serializable
data class QuizResultResponse(
    val success: Boolean,
    val result: QuizResultData
)

@Serializable
data class QuizResultsResponse(
    val success: Boolean,
    val results: List<QuizResultData>
)

@Serializable
data class LikeSongRequest(
    val songId: String,
    val title: String,
    val artist: String = "Unknown",
    val audioUrl: String,
    val source: String,
    val duration: Int = 0
)

@Serializable
data class LikedSongData(
    val id: String,
    val songId: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val source: String,
    val duration: Int = 0,
    val likedAt: String
)

@Serializable
data class LikedSongResponse(
    val success: Boolean,
    val song: LikedSongData
)

@Serializable
data class LikedSongsResponse(
    val success: Boolean,
    val songs: List<LikedSongData>
)

@Serializable
data class RandomLikedSongResponse(
    val success: Boolean,
    val song: LikedSongData?
)

@Serializable
data class SuccessResponse(
    val success: Boolean
)

@Serializable
data class ReflectSummaryData(
    val recentMoods: List<HomeMoodData>,
    val quest: QuestSummaryData?,
    val completedTests: Int,
    val testResults: List<TestSummaryData>,
    val quizResults: List<QuizResultData>,
    val routineStreak: Int
)

@Serializable
data class QuestSummaryData(
    val completedCount: Int,
    val totalRequired: Int,
    val badgeEarned: Boolean,
    val badgeType: String?
)

@Serializable
data class TestSummaryData(
    val testId: String,
    val testTitle: String,
    val completedCount: Int,
    val lastCompleted: String
)

@Serializable
data class ReflectSummaryResponse(
    val success: Boolean,
    val data: ReflectSummaryData
)

// ═══════════════════════════════════════════════════════════════════════════════
// Calm Anxiety Data Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class CalmAnxietyReflection(
    val questionId: Int,
    val prompt: String,
    val answer: String
)

@Serializable
data class SaveCalmAnxietyRequest(
    val anxietyLevelBefore: Int? = null,
    val anxietyLevelAfter: Int? = null,
    val reflections: List<CalmAnxietyReflection>,
    val primaryTrigger: String = "",
    val completedFully: Boolean = true,
    val durationSeconds: Int = 0
)

@Serializable
data class CalmAnxietyEntryData(
    val id: String,
    val anxietyLevelBefore: Int? = null,
    val anxietyLevelAfter: Int? = null,
    val reflections: List<CalmAnxietyReflection>,
    val primaryTrigger: String = "",
    val completedFully: Boolean = true,
    val durationSeconds: Int = 0,
    val createdAt: String
)

@Serializable
data class CalmAnxietyResponse(
    val success: Boolean,
    val entry: CalmAnxietyEntryData
)

@Serializable
data class CalmAnxietyPagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int
)

@Serializable
data class CalmAnxietyHistoryResponse(
    val success: Boolean,
    val entries: List<CalmAnxietyEntryData>,
    val pagination: CalmAnxietyPagination
)
