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
 * Manages calm audio content from:
 * - Freesound (nature, ambient, meditation, asmr, sleep sounds)
 * - Jamendo (calm instrumental music, focus music)
 * 
 * NO devotional/religious content.
 * NO LibriVox or Internet Archive.
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
     * Get ambient sounds
     */
    suspend fun getAmbientSounds(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getAmbientSounds()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch ambient sounds"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get ambient sounds exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get ASMR sounds
     */
    suspend fun getASMRSounds(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getASMRSounds()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch ASMR sounds"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get ASMR sounds exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get sleep sounds (white noise, etc.)
     */
    suspend fun getSleepSounds(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getSleepSounds()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch sleep sounds"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get sleep sounds exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get focus/study music
     */
    suspend fun getFocusMusic(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getFocusMusic()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch focus music"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get focus music exception", e)
            Result.failure(e)
        }
    }

    /**
     * Get calm music
     */
    suspend fun getCalmMusic(): Result<List<AudioItem>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = audioApiService.getCalmMusic()
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
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SEARCH
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Search for audio content
     */
    suspend fun searchAudio(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<AudioSearchResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Searching for: $query")
            val response = audioApiService.searchAudio(query, page, limit)
            
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
    // QUICK PICKS
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get quick picks (curated selection)
     */
    suspend fun getQuickPicks(): Result<List<AudioItem>> = withContext(Dispatchers.IO) {
        try {
            val response = audioApiService.getQuickPicks()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Failed to fetch quick picks"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get quick picks exception", e)
            Result.failure(e)
        }
    }
}
