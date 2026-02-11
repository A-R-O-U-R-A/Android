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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CalmViewModel"

/**
 * Calm ViewModel - Optimized with Lazy Loading
 * 
 * PERFORMANCE OPTIMIZATIONS:
 * - Only loads selected tab's content (lazy loading)
 * - Caches loaded categories to avoid re-fetching
 * - Limits items per category for fast rendering
 * - Debounced search
 * 
 * Categories: Nature, Ambient, Meditation, ASMR, Sleep, Focus, Music
 * Sources: Freesound (sounds), Jamendo (music)
 */
class CalmViewModel(
    private val repository: AudioRepository
) : ViewModel() {
    
    companion object {
        private const val MAX_ITEMS_PER_CATEGORY = 12 // Limit for performance
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _uiState = MutableStateFlow(CalmUiState())
    val uiState: StateFlow<CalmUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(CalmTab.NATURE)
    val selectedTab: StateFlow<CalmTab> = _selectedTab.asStateFlow()
    
    // Track which categories are already loaded
    private val loadedCategories = mutableSetOf<CalmTab>()
    private var currentLoadJob: Job? = null
    private var searchJob: Job? = null
    
    // Track loading state per tab to prevent duplicate requests
    private val loadingTabs = mutableSetOf<CalmTab>()
    
    init {
        // Only load initial tab (Nature) - not all categories
        loadSelectedTabContent()
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // LAZY LOADING - LOADS ONLY WHEN TAB IS SELECTED
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Load content for selected tab only (lazy loading)
     */
    private fun loadSelectedTabContent() {
        currentLoadJob?.cancel()
        currentLoadJob = viewModelScope.launch {
            // Small debounce to prevent rapid API calls when switching tabs quickly
            delay(100)
            
            val tab = _selectedTab.value
            
            // Skip if already loaded or currently loading
            if (loadedCategories.contains(tab)) {
                _uiState.value = _uiState.value.copy(isContentLoaded = true)
                return@launch
            }
            
            // Skip if already loading this tab
            if (loadingTabs.contains(tab)) {
                return@launch
            }
            
            loadingTabs.add(tab)
            _isLoading.value = true
            _error.value = null
            
            try {
                when (tab) {
                    CalmTab.NATURE -> loadNature()
                    CalmTab.AMBIENT -> loadAmbient()
                    CalmTab.MEDITATION -> loadMeditation()
                    CalmTab.SLEEP -> loadSleep()
                    CalmTab.FOCUS -> loadFocus()
                    CalmTab.ASMR -> loadAsmr()
                    CalmTab.MUSIC -> loadMusic()
                }
                
                loadedCategories.add(tab)
                _uiState.value = _uiState.value.copy(isContentLoaded = true)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading ${tab.name}", e)
                _error.value = "Failed to load ${tab.displayName}. Tap to retry."
                
                // Use fallback for Nature tab
                if (tab == CalmTab.NATURE) {
                    loadFallbackContent()
                }
            } finally {
                loadingTabs.remove(tab)
                _isLoading.value = false
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // INDIVIDUAL CATEGORY LOADERS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private suspend fun loadNature() {
        val result = repository.getNatureSounds()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                natureSounds = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadAmbient() {
        val result = repository.getAmbientSounds()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                ambientSounds = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadMeditation() {
        val result = repository.getMeditationSounds()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                meditationSounds = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadSleep() {
        val result = repository.getSleepSounds()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                sleepSounds = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadFocus() {
        val result = repository.getFocusMusic()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                focusMusic = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadAsmr() {
        val result = repository.getASMRSounds()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                asmrSounds = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    private suspend fun loadMusic() {
        val result = repository.getCalmMusic()
        result.onSuccess { items ->
            _uiState.value = _uiState.value.copy(
                calmMusic = items.take(MAX_ITEMS_PER_CATEGORY).map { it.toCalmMediaItem() }
            )
        }.onFailure { throw it }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // TAB SELECTION & REFRESH
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Select a tab and lazy-load its content if not cached
     */
    fun selectTab(tab: CalmTab) {
        if (_selectedTab.value == tab) return
        
        _selectedTab.value = tab
        _error.value = null
        
        // Only load if not already cached
        if (!loadedCategories.contains(tab)) {
            loadSelectedTabContent()
        }
    }
    
    /**
     * Force refresh current tab
     */
    fun refreshCurrentTab() {
        loadedCategories.remove(_selectedTab.value)
        loadSelectedTabContent()
    }
    
    /**
     * Get items for current tab
     */
    fun getCurrentTabItems(): List<CalmMediaItemData> {
        return when (_selectedTab.value) {
            CalmTab.NATURE -> _uiState.value.natureSounds
            CalmTab.AMBIENT -> _uiState.value.ambientSounds
            CalmTab.MEDITATION -> _uiState.value.meditationSounds
            CalmTab.SLEEP -> _uiState.value.sleepSounds
            CalmTab.FOCUS -> _uiState.value.focusMusic
            CalmTab.ASMR -> _uiState.value.asmrSounds
            CalmTab.MUSIC -> _uiState.value.calmMusic
        }
    }
    
    /**
     * Search for audio content with debounce
     */
    fun searchAudio(query: String) {
        searchJob?.cancel()
        
        if (query.length < 2) {
            clearSearch()
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _isLoading.value = true
            try {
                val result = repository.searchAudio(query)
                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = response.results.take(15).map { it.toCalmMediaItem() },
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
            natureSounds = getFallbackNature(),
            sleepSounds = getFallbackSleep(),
            meditationSounds = getFallbackMeditation(),
            isContentLoaded = true
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // FALLBACK DATA (No religious content)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun getFallbackNature(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_rain",
            title = "Rain Sounds",
            subtitle = "Nature Ambience",
            category = "nature",
            startColor = Color(0xFF81C784),
            endColor = Color(0xFF2E7D32),
            streamingUrl = "https://freesound.org/data/previews/531/531947_7618603-lq.mp3",
            duration = 300,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_ocean",
            title = "Ocean Waves",
            subtitle = "Beach Relaxation",
            category = "nature",
            startColor = Color(0xFF64B5F6),
            endColor = Color(0xFF1976D2),
            streamingUrl = "https://freesound.org/data/previews/610/610706_7037-lq.mp3",
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
            duration = 300,
            loopAllowed = true,
            sourceName = "Freesound"
        )
    )
    
    private fun getFallbackSleep(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_white_noise",
            title = "White Noise",
            subtitle = "Sleep Aid",
            category = "sleep",
            startColor = Color(0xFF9575CD),
            endColor = Color(0xFF512DA8),
            streamingUrl = "https://freesound.org/data/previews/132/132765_2398403-lq.mp3",
            duration = 600,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_fan",
            title = "Fan Sound",
            subtitle = "Background Noise",
            category = "sleep",
            startColor = Color(0xFF78909C),
            endColor = Color(0xFF455A64),
            streamingUrl = "https://freesound.org/data/previews/342/342884_3248244-lq.mp3",
            duration = 600,
            loopAllowed = true,
            sourceName = "Freesound"
        )
    )
    
    private fun getFallbackMeditation(): List<CalmMediaItemData> = listOf(
        CalmMediaItemData(
            id = "fb_singing_bowl",
            title = "Singing Bowl",
            subtitle = "Meditation Aid",
            category = "meditation",
            startColor = Color(0xFFFFB74D),
            endColor = Color(0xFFF57C00),
            streamingUrl = "https://freesound.org/data/previews/411/411089_5121236-lq.mp3",
            duration = 180,
            loopAllowed = true,
            sourceName = "Freesound"
        ),
        CalmMediaItemData(
            id = "fb_bell",
            title = "Meditation Bell",
            subtitle = "Zen Sound",
            category = "meditation",
            startColor = Color(0xFF4DB6AC),
            endColor = Color(0xFF00897B),
            streamingUrl = "https://freesound.org/data/previews/352/352661_5121236-lq.mp3",
            duration = 60,
            loopAllowed = true,
            sourceName = "Freesound"
        )
    )
    
    fun clearError() {
        _error.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        currentLoadJob?.cancel()
        searchJob?.cancel()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Tabs for Calm screen - NO devotional content
 */
enum class CalmTab(val displayName: String, val icon: String) {
    NATURE("Nature", "🌿"),
    AMBIENT("Ambient", "🌙"),
    MEDITATION("Meditation", "🧘"),
    SLEEP("Sleep", "😴"),
    FOCUS("Focus", "🎯"),
    ASMR("ASMR", "✨"),
    MUSIC("Music", "🎵")
}

data class CalmUiState(
    val natureSounds: List<CalmMediaItemData> = emptyList(),
    val ambientSounds: List<CalmMediaItemData> = emptyList(),
    val meditationSounds: List<CalmMediaItemData> = emptyList(),
    val asmrSounds: List<CalmMediaItemData> = emptyList(),
    val sleepSounds: List<CalmMediaItemData> = emptyList(),
    val focusMusic: List<CalmMediaItemData> = emptyList(),
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
    val imageUrl: String? = null,
    val subCategory: String? = null
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
        imageUrl = image,
        subCategory = subCategory
    )
}

/**
 * Get gradient colors based on category
 */
private fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category) {
        "nature" -> Color(0xFF81C784) to Color(0xFF2E7D32)
        "ambient" -> Color(0xFF90CAF9) to Color(0xFF1565C0)
        "meditation" -> Color(0xFFFFB74D) to Color(0xFFF57C00)
        "asmr" -> Color(0xFFCE93D8) to Color(0xFF7B1FA2)
        "sleep" -> Color(0xFF9575CD) to Color(0xFF512DA8)
        "focus" -> Color(0xFF4DD0E1) to Color(0xFF0097A7)
        "music" -> Color(0xFFE57373) to Color(0xFFD32F2F)
        else -> Color(0xFF4DB6AC) to Color(0xFF00897B)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// VIEW MODEL FACTORY
// ═══════════════════════════════════════════════════════════════════════════════

// TODO: Replace with Hilt/Koin DI — this manual factory should be removed once a DI framework is adopted.
class CalmViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalmViewModel::class.java)) {
            val tokenManager = TokenManager.getInstance(context)
            val audioApiService = ApiClient.createAudioApiService(tokenManager)
            val repository = AudioRepository(audioApiService, context)
            
            return CalmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
