package com.example.aroura.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.api.AudioItem
import com.example.aroura.data.local.TokenManager
import com.example.aroura.data.repository.AudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CalmViewModel"

/**
 * Calm ViewModel
 * 
 * Manages state for the Calm screen with real audio content
 * from Freesound, Jamendo, Internet Archive, and LibriVox
 */
class CalmViewModel(
    private val repository: AudioRepository
) : ViewModel() {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _uiState = MutableStateFlow(CalmUiState())
    val uiState: StateFlow<CalmUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadAllContent()
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // LOAD CONTENT
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Load all audio content from backend
     */
    fun loadAllContent() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Load each category in parallel
                val devotionalResult = repository.getDevotionalContent()
                val audiobooksResult = repository.getAudiobooks()
                
                // Update state with results
                _uiState.value = _uiState.value.copy(
                    devotionalItems = devotionalResult.getOrDefault(emptyList())
                        .map { it.toCalmMediaItem() },
                    audiobookItems = audiobooksResult.getOrDefault(emptyList())
                        .map { it.toCalmMediaItem() },
                    isContentLoaded = true
                )
                
                // Load nature sounds and calm music (may take longer due to external APIs)
                loadNatureAndMusic()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading content", e)
                _error.value = e.message ?: "Failed to load content"
                
                // Use fallback content if API fails
                loadFallbackContent()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load nature sounds and calm music (external APIs)
     */
    private fun loadNatureAndMusic() {
        viewModelScope.launch {
            try {
                val natureResult = repository.getNatureSounds()
                val musicResult = repository.getCalmMusic()
                
                _uiState.value = _uiState.value.copy(
                    natureSounds = natureResult.getOrDefault(emptyList())
                        .map { it.toCalmMediaItem() },
                    calmMusic = musicResult.getOrDefault(emptyList())
                        .map { it.toCalmMediaItem() }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading nature/music", e)
            }
        }
    }
    
    /**
     * Load devotional content specifically
     */
    fun loadDevotional() {
        viewModelScope.launch {
            try {
                val result = repository.getDevotionalContent()
                result.onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        devotionalItems = items.map { it.toCalmMediaItem() }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading devotional", e)
            }
        }
    }
    
    /**
     * Load audiobooks specifically
     */
    fun loadAudiobooks() {
        viewModelScope.launch {
            try {
                val result = repository.getAudiobooks()
                result.onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        audiobookItems = items.map { it.toCalmMediaItem() }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading audiobooks", e)
            }
        }
    }
    
    /**
     * Search for audio content
     */
    fun searchAudio(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchAudio(query)
                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = response.results.map { it.toCalmMediaItem() },
                        isSearchActive = true
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search error", e)
                _error.value = "Search failed"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear search results
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyList(),
            isSearchActive = false
        )
    }
    
    /**
     * Fallback content when API fails
     */
    private fun loadFallbackContent() {
        _uiState.value = _uiState.value.copy(
            devotionalItems = getFallbackDevotional(),
            audiobookItems = getFallbackAudiobooks(),
            relaxationItems = getFallbackRelaxation(),
            isContentLoaded = true
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FALLBACK DATA
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun getFallbackDevotional(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_meditation_bells",
            title = "Meditation Bells",
            subtitle = "Tibetan Singing Bowls",
            category = "devotional",
            startColor = Color(0xFF5C6BC0),
            endColor = Color(0xFF3949AB),
            streamingUrl = "https://freesound.org/data/previews/411/411089_5121236-lq.mp3",
            duration = 180,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_temple_bells",
            title = "Temple Bells",
            subtitle = "Sacred Ambience",
            category = "devotional",
            startColor = Color(0xFFFFB74D),
            endColor = Color(0xFFF57C00),
            streamingUrl = "https://freesound.org/data/previews/352/352661_5121236-lq.mp3",
            duration = 120,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_wind_chimes",
            title = "Wind Chimes",
            subtitle = "Peaceful Relaxation",
            category = "devotional",
            startColor = Color(0xFF4DB6AC),
            endColor = Color(0xFF00897B),
            streamingUrl = "https://freesound.org/data/previews/467/467090_7166240-lq.mp3",
            duration = 60,
            loopAllowed = true,
            sourceName = "Freesound"
        )
    )
    
    private fun getFallbackAudiobooks(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_relaxing_story",
            title = "Relaxing Story",
            subtitle = "Sleep Tales",
            category = "audiobooks",
            startColor = Color(0xFFE57373),
            endColor = Color(0xFFD32F2F),
            streamingUrl = "https://freesound.org/data/previews/456/456515_9322996-lq.mp3",
            duration = 300,
            loopAllowed = false,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_peaceful_narration",
            title = "Peaceful Journey",
            subtitle = "Guided Relaxation",
            category = "audiobooks",
            startColor = Color(0xFFFFD54F),
            endColor = Color(0xFFFFA000),
            streamingUrl = "https://freesound.org/data/previews/562/562359_7166240-lq.mp3",
            duration = 240,
            loopAllowed = false,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_calm_voice",
            title = "Calm Meditation",
            subtitle = "Voice Guide",
            category = "audiobooks",
            startColor = Color(0xFF81C784),
            endColor = Color(0xFF388E3C),
            streamingUrl = "https://freesound.org/data/previews/528/528905_11766219-lq.mp3",
            duration = 180,
            loopAllowed = false,
            sourceName = "Freesound"
        )
    )
    
    private fun getFallbackRelaxation(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_rain",
            title = "Rain Sounds",
            subtitle = "Nature Ambience",
            category = "nature",
            startColor = Color(0xFF81C784),
            endColor = Color(0xFF2E7D32),
            streamingUrl = "https://freesound.org/data/previews/531/531947_7618603-lq.mp3",
            streamingUrlBackup = "https://archive.org/download/RainThunder/Rain%20%26%20Thunder.mp3",
            duration = 300,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_ocean",
            title = "Ocean Waves",
            subtitle = "Beach Relaxation",
            category = "nature",
            startColor = Color(0xFF9575CD),
            endColor = Color(0xFF512DA8),
            streamingUrl = "https://freesound.org/data/previews/610/610706_7037-lq.mp3",
            streamingUrlBackup = "https://archive.org/download/ocean-waves-sound/Ocean%20Waves.mp3",
            duration = 300,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_forest",
            title = "Forest Birds",
            subtitle = "Morning Ambience",
            category = "nature",
            startColor = Color(0xFF66BB6A),
            endColor = Color(0xFF43A047),
            streamingUrl = "https://freesound.org/data/previews/576/576828_6515806-lq.mp3",
            streamingUrlBackup = "https://archive.org/download/forest-birds-morning/Forest%20Birds.mp3",
            duration = 300,
            loopAllowed = true,
            sourceName = "Freesound"
        )
    )
    
    fun clearError() {
        _error.value = null
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════════════════════════

data class CalmUiState(
    val devotionalItems: List<CalmMediaItemData> = emptyList(),
    val audiobookItems: List<CalmMediaItemData> = emptyList(),
    val relaxationItems: List<CalmMediaItemData> = emptyList(),
    val natureSounds: List<CalmMediaItemData> = emptyList(),
    val calmMusic: List<CalmMediaItemData> = emptyList(),
    val searchResults: List<CalmMediaItemData> = emptyList(),
    val isSearchActive: Boolean = false,
    val isContentLoaded: Boolean = false
)

/**
 * Media item data class with streaming info
 */
data class CalmMediaItemData(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val startColor: Color,
    val endColor: Color,
    val streamingUrl: String = "",
    val streamingUrlBackup: String? = null,
    val duration: Int = 0,
    val loopAllowed: Boolean = false,
    val sleepTimerSupported: Boolean = true,
    val sourceName: String = "",
    val attributionText: String? = null,
    val imageUrl: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════════
// EXTENSIONS
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Convert API AudioItem to UI CalmMediaItemData
 */
fun AudioItem.toCalmMediaItem(): CalmMediaItemData {
    val (startColor, endColor) = getCategoryColors(category)
    return CalmMediaItemData(
        id = id,
        title = title,
        subtitle = subtitle,
        category = category,
        startColor = startColor,
        endColor = endColor,
        streamingUrl = streamingUrl,
        streamingUrlBackup = streamingUrlBackup,
        duration = duration,
        loopAllowed = loopAllowed,
        sleepTimerSupported = sleepTimerSupported,
        sourceName = sourceName,
        attributionText = attributionText,
        imageUrl = image
    )
}

/**
 * Get gradient colors based on category
 */
private fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category) {
        "devotional" -> Color(0xFF5C6BC0) to Color(0xFF3949AB)
        "audiobooks" -> Color(0xFFE57373) to Color(0xFFD32F2F)
        "nature" -> Color(0xFF81C784) to Color(0xFF2E7D32)
        "meditation" -> Color(0xFFFFB74D) to Color(0xFFF57C00)
        "calm_music" -> Color(0xFF9575CD) to Color(0xFF512DA8)
        else -> Color(0xFF4DB6AC) to Color(0xFF00897B)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// VIEW MODEL FACTORY
// ═══════════════════════════════════════════════════════════════════════════════

class CalmViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalmViewModel::class.java)) {
            val tokenManager = TokenManager(context)
            val audioApiService = ApiClient.createAudioApiService(tokenManager)
            val repository = AudioRepository(audioApiService, context)
            
            return CalmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
