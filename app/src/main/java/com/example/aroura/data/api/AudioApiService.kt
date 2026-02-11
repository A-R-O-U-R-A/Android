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
 * Endpoints for calm/peaceful audio streaming
 * Sources: Freesound (sounds), Jamendo (music)
 * 
 * NO devotional/religious content.
 * NO LibriVox or Internet Archive.
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
     * Search across all audio sources (calm-filtered)
     */
    @GET("audio/search")
    suspend fun searchAudio(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<AudioSearchResponse>
    
    /**
     * Get nature sounds (rain, ocean, forest, etc.)
     */
    @GET("audio/nature")
    suspend fun getNatureSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get ambient soundscapes
     */
    @GET("audio/ambient")
    suspend fun getAmbientSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get meditation sounds (singing bowls, bells, etc.)
     */
    @GET("audio/meditation")
    suspend fun getMeditationSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get ASMR sounds (soft tapping, pages, etc.)
     */
    @GET("audio/asmr")
    suspend fun getASMRSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get sleep sounds (white noise, brown noise, etc.)
     */
    @GET("audio/sleep")
    suspend fun getSleepSounds(): Response<AudioCategoryResponse>
    
    /**
     * Get focus/study music
     */
    @GET("audio/focus")
    suspend fun getFocusMusic(): Response<AudioCategoryResponse>
    
    /**
     * Get calm instrumental music
     */
    @GET("audio/music")
    suspend fun getCalmMusic(): Response<AudioCategoryResponse>
    
    /**
     * Get quick picks (curated selection)
     */
    @GET("audio/quick")
    suspend fun getQuickPicks(): Response<AudioCategoryResponse>
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
    val nature: AudioCategoryData? = null,
    val ambient: AudioCategoryData? = null,
    val meditation: AudioCategoryData? = null,
    val asmr: AudioCategoryData? = null,
    val sleep: AudioCategoryData? = null,
    val focus: AudioCategoryData? = null,
    val music: AudioCategoryData? = null
)

@Serializable
data class AudioCategoryData(
    val items: List<AudioItem> = emptyList(),
    val total: Int = 0
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
