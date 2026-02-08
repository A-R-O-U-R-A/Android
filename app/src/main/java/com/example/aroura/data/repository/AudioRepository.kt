package com.example.aroura.data.repository

import android.content.Context
import android.util.Log
import com.example.aroura.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AudioRepository"

/**
 * Audio Repository
 * 
 * Manages audio content from legal streaming sources:
 * - Freesound (nature sounds, ambience)
 * - Jamendo (calm music)
 * - Internet Archive (devotional content)
 * - LibriVox (audiobooks)
 */
class AudioRepository(
    private val audioApiService: AudioApiService,
    private val context: Context
) {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // GET ALL CONTENT
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get all calm content organized by category
     */
    suspend fun getAllContent(): Result<AudioAllResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching all audio content...")
            val response = audioApiService.getAllContent()
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "Fetched ${body.totalItems} audio items")
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: "Failed to fetch content"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Get all content failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Failed to fetch content: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get all content exception", e)
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // CATEGORY CONTENT
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get content for a specific category
     */
    suspend fun getCategoryContent(category: String): Result<AudioCategoryResponse> = 
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching $category content...")
                val response = audioApiService.getCategoryContent(category)
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Fetched ${body.items.size} items for $category")
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.error ?: "Failed to fetch $category"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch $category: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get $category exception", e)
                Result.failure(e)
            }
        }
    
    /**
     * Get nature sounds
     */
    suspend fun getNatureSounds(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getNatureSounds()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch nature sounds"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get nature sounds exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get meditation sounds
     */
    suspend fun getMeditationSounds(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getMeditationSounds()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch meditation sounds"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get meditation sounds exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get calm music
     */
    suspend fun getCalmMusic(tags: String? = null): Result<List<AudioItem>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = audioApiService.getCalmMusic(tags)
                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.items)
                } else {
                    Result.failure(Exception("Failed to fetch calm music"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get calm music exception", e)
                Result.failure(e)
            }
        }
    
    /**
     * Get devotional content
     */
    suspend fun getDevotionalContent(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getDevotionalContent()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch devotional content"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get devotional exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get audiobooks
     */
    suspend fun getAudiobooks(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getAudiobooks()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch audiobooks"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get audiobooks exception", e)
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SEARCH
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Search for audio content
     */
    suspend fun searchAudio(
        query: String,
        source: String = "all",
        page: Int = 1,
        limit: Int = 20
    ): Result<AudioSearchResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Searching for: $query")
            val response = audioApiService.searchAudio(query, source, page, limit)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "Search found ${body.results.size} results")
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: "Search failed"))
                }
            } else {
                Result.failure(Exception("Search failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Search exception", e)
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FEATURED & QUICK
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get featured content
     */
    suspend fun getFeaturedContent(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getFeaturedContent()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch featured content"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get featured exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get quick calm tracks (under 5 minutes)
     */
    suspend fun getQuickCalm(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getQuickCalm()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch quick calm"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get quick calm exception", e)
            Result.failure(e)
        }
    }
}
