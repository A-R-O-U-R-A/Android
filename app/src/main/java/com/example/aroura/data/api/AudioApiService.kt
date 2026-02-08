package com.example.aroura.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Audio API Service
 * 
 * Endpoints for calm/meditation audio streaming from legal sources
 */
interface AudioApiService {
    
    /**
     * Get all calm content organized by category
     */
    @GET("audio/all")
    suspend fun getAllContent(): Response<AudioAllResponse>
    
    /**
     * Get content for a specific category
     */
    @GET("audio/category/{category}")
    suspend fun getCategoryContent(
        @Path("category") category: String
    ): Response<AudioCategoryResponse>
    
    /**
     * Search across all audio sources
     */
    @GET("audio/search")
    suspend fun searchAudio(
        @Query("q") query: String,
        @Query("source") source: String? = "all",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<AudioSearchResponse>
    
    /**
     * Get nature sounds
     */
    @GET("audio/nature")
    suspend fun getNatureSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get meditation sounds
     */
    @GET("audio/meditation")
    suspend fun getMeditationSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get calm music
     */
    @GET("audio/music")
    suspend fun getCalmMusic(
        @Query("tags") tags: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<AudioCategoryResponse>
    
    /**
     * Get devotional content
     */
    @GET("audio/devotional")
    suspend fun getDevotionalContent(): Response<AudioCategoryResponse>
    
    /**
     * Get audiobooks
     */
    @GET("audio/audiobooks")
    suspend fun getAudiobooks(): Response<AudioCategoryResponse>
    
    /**
     * Get featured content
     */
    @GET("audio/featured")
    suspend fun getFeaturedContent(): Response<AudioCategoryResponse>
    
    /**
     * Get quick calm tracks (under 5 minutes)
     */
    @GET("audio/quick")
    suspend fun getQuickCalm(): Response<AudioCategoryResponse>
}

// ═══════════════════════════════════════════════════════════════════════════════
// Response Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class AudioItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    @SerialName("source_name")
    val sourceName: String,
    @SerialName("streaming_url")
    val streamingUrl: String,
    @SerialName("streaming_url_backup")
    val streamingUrlBackup: String? = null,
    val duration: Int = 0,
    @SerialName("attribution_text")
    val attributionText: String? = null,
    @SerialName("loop_allowed")
    val loopAllowed: Boolean = false,
    @SerialName("sleep_timer_supported")
    val sleepTimerSupported: Boolean = true,
    val tags: List<String>? = null,
    val license: String? = null,
    val image: String? = null,
    val album: String? = null,
    val subCategory: String? = null,
    val chapters: Int? = null,
    val author: String? = null,
    val description: String? = null
)

@Serializable
data class AudioCategory(
    val title: String,
    val description: String? = null,
    val items: List<AudioItem>,
    @SerialName("loop_allowed")
    val loopAllowed: Boolean = false
)

@Serializable
data class AudioAllResponse(
    val success: Boolean,
    val categories: AudioCategories? = null,
    val totalItems: Int = 0,
    val sources: List<String> = emptyList(),
    val error: String? = null
)

@Serializable
data class AudioCategories(
    val nature: AudioCategory? = null,
    val meditation: AudioCategory? = null,
    @SerialName("calm_music")
    val calmMusic: AudioCategory? = null,
    val devotional: AudioCategory? = null,
    val audiobooks: AudioCategory? = null
)

@Serializable
data class AudioCategoryResponse(
    val success: Boolean,
    val category: String? = null,
    val title: String? = null,
    val description: String? = null,
    val items: List<AudioItem> = emptyList(),
    @SerialName("loop_allowed")
    val loopAllowed: Boolean = false,
    @SerialName("sleep_timer_supported")
    val sleepTimerSupported: Boolean = true,
    val error: String? = null
)

@Serializable
data class AudioSearchResponse(
    val success: Boolean,
    val query: String? = null,
    val results: List<AudioItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
    val error: String? = null
)
