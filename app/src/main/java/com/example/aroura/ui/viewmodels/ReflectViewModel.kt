package com.example.aroura.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.api.CalmAnxietyEntryData
import com.example.aroura.data.api.HomeMoodData
import com.example.aroura.data.api.MoodJournalEntryData
import com.example.aroura.data.api.TestResultData
import com.example.aroura.data.api.QuizResultData
import com.example.aroura.data.api.QuestProgressData
import com.example.aroura.data.api.SaveHomeMoodRequest
import com.example.aroura.data.api.SaveQuizResultRequest
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ReflectViewModel
 * 
 * Manages state for the Reflect screens:
 * - Mood check-in history
 * - Test results history
 * - Quiz results history
 * - Quest progress
 * - Summary statistics
 */
class ReflectViewModel(application: Application) : AndroidViewModel(application) {
    
    private val tokenManager = TokenManager.getInstance(application)
    private val reflectApi = ApiClient.createReflectApiService(tokenManager)
    
    // Mood history (How are you feeling - quick check-ins)
    private val _moodHistory = MutableStateFlow<List<HomeMoodData>>(emptyList())
    val moodHistory: StateFlow<List<HomeMoodData>> = _moodHistory.asStateFlow()
    
    // Mood journal history (Track Your Mood - detailed entries)
    private val _moodJournalHistory = MutableStateFlow<List<MoodJournalEntryData>>(emptyList())
    val moodJournalHistory: StateFlow<List<MoodJournalEntryData>> = _moodJournalHistory.asStateFlow()
    
    // Anxiety history
    private val _anxietyHistory = MutableStateFlow<List<CalmAnxietyEntryData>>(emptyList())
    val anxietyHistory: StateFlow<List<CalmAnxietyEntryData>> = _anxietyHistory.asStateFlow()
    
    // Test results
    private val _testResults = MutableStateFlow<List<TestResultData>>(emptyList())
    val testResults: StateFlow<List<TestResultData>> = _testResults.asStateFlow()
    
    // Quiz results
    private val _quizResults = MutableStateFlow<List<QuizResultData>>(emptyList())
    val quizResults: StateFlow<List<QuizResultData>> = _quizResults.asStateFlow()
    
    // Quest progress
    private val _questProgress = MutableStateFlow<QuestProgressData?>(null)
    val questProgress: StateFlow<QuestProgressData?> = _questProgress.asStateFlow()
    
    // Summary stats
    private val _totalMoodCheckIns = MutableStateFlow(0)
    val totalMoodCheckIns: StateFlow<Int> = _totalMoodCheckIns.asStateFlow()
    
    private val _totalTestsCompleted = MutableStateFlow(0)
    val totalTestsCompleted: StateFlow<Int> = _totalTestsCompleted.asStateFlow()
    
    private val _totalQuizzesCompleted = MutableStateFlow(0)
    val totalQuizzesCompleted: StateFlow<Int> = _totalQuizzesCompleted.asStateFlow()
    
    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Current selected mood for check-in
    private val _selectedMood = MutableStateFlow<String?>(null)
    val selectedMood: StateFlow<String?> = _selectedMood.asStateFlow()
    
    private val _moodSaved = MutableStateFlow(false)
    val moodSaved: StateFlow<Boolean> = _moodSaved.asStateFlow()
    
    init {
        fetchAllData()
    }
    
    /**
     * Fetch all reflect data from the API
     */
    fun fetchAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Fetch in parallel
                launch { fetchMoodHistory() }
                launch { fetchMoodJournalHistory() }
                launch { fetchTestResults() }
                launch { fetchQuizResults() }
                launch { fetchQuestProgress() }
                launch { fetchSummary() }
            } catch (e: Exception) {
                Log.e("ReflectViewModel", "Error fetching data", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch mood check-in history (How are you feeling)
     */
    private suspend fun fetchMoodHistory() {
        try {
            val response = reflectApi.getHomeMoodHistory(limit = 30)
            if (response.isSuccessful && response.body()?.success == true) {
                _moodHistory.value = response.body()?.moods ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching mood history", e)
        }
    }
    
    /**
     * Fetch mood journal history (Track Your Mood - detailed entries)
     */
    private suspend fun fetchMoodJournalHistory() {
        try {
            val response = reflectApi.getMoodJournalHistory(limit = 30)
            if (response.isSuccessful && response.body()?.success == true) {
                _moodJournalHistory.value = response.body()?.entries ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching mood journal history", e)
        }
    }
    
    /**
     * Fetch anxiety history
     */
    fun fetchAnxietyHistory() {
        viewModelScope.launch {
            try {
                val response = reflectApi.getCalmAnxietyHistory(limit = 30)
                if (response.isSuccessful && response.body()?.success == true) {
                    _anxietyHistory.value = response.body()?.entries ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReflectViewModel", "Error fetching anxiety history", e)
            }
        }
    }
    
    /**
     * Fetch test results
     */
    private suspend fun fetchTestResults() {
        try {
            val response = reflectApi.getTestResults()
            if (response.isSuccessful && response.body()?.success == true) {
                _testResults.value = response.body()?.results ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching test results", e)
        }
    }
    
    /**
     * Fetch quiz results
     */
    private suspend fun fetchQuizResults() {
        try {
            val response = reflectApi.getQuizResults()
            if (response.isSuccessful && response.body()?.success == true) {
                _quizResults.value = response.body()?.results ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching quiz results", e)
        }
    }
    
    /**
     * Fetch quest progress
     */
    private suspend fun fetchQuestProgress() {
        try {
            val response = reflectApi.getQuestProgress()
            if (response.isSuccessful && response.body()?.success == true) {
                _questProgress.value = response.body()?.progress
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching quest progress", e)
        }
    }
    
    /**
     * Fetch summary statistics
     */
    private suspend fun fetchSummary() {
        try {
            val response = reflectApi.getReflectSummary()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { summary ->
                    _totalMoodCheckIns.value = summary.recentMoods.size
                    _totalTestsCompleted.value = summary.completedTests
                    _totalQuizzesCompleted.value = summary.quizResults.size
                }
            }
        } catch (e: Exception) {
            Log.e("ReflectViewModel", "Error fetching summary", e)
        }
    }
    
    /**
     * Select a mood in the mood check-in screen
     */
    fun selectMood(mood: String) {
        _selectedMood.value = mood
        _moodSaved.value = false
    }
    
    /**
     * Save the selected mood to the database
     */
    fun saveMood(note: String = "") {
        val mood = _selectedMood.value ?: return
        
        viewModelScope.launch {
            try {
                val moodIndex = getMoodIndex(mood)
                val moodEmoji = getMoodEmoji(mood)
                val request = SaveHomeMoodRequest(
                    moodIndex = moodIndex,
                    moodLabel = mood,
                    moodEmoji = moodEmoji,
                    note = note
                )
                
                val response = reflectApi.saveHomeMood(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _moodSaved.value = true
                    // Refresh mood history
                    fetchMoodHistory()
                    Log.d("ReflectViewModel", "Mood saved successfully")
                } else {
                    Log.e("ReflectViewModel", "Failed to save mood: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ReflectViewModel", "Error saving mood", e)
            }
        }
    }
    
    /**
     * Reset mood selection after saving
     */
    fun resetMoodSelection() {
        _selectedMood.value = null
        _moodSaved.value = false
    }
    
    /**
     * Save quiz result to the database
     */
    fun saveQuizResult(
        quizId: String,
        quizTitle: String,
        resultMessage: String,
        score: Int
    ) {
        viewModelScope.launch {
            try {
                val request = SaveQuizResultRequest(
                    quizId = quizId,
                    quizTitle = quizTitle,
                    resultMessage = resultMessage,
                    score = score,
                    totalQuestions = 10 // All our quizzes have 10 questions
                )
                
                val response = reflectApi.saveQuizResult(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh quiz results
                    fetchQuizResults()
                    Log.d("ReflectViewModel", "Quiz result saved successfully")
                }
            } catch (e: Exception) {
                Log.e("ReflectViewModel", "Error saving quiz result", e)
            }
        }
    }
    
    /**
     * Get test results for a specific test
     */
    fun getTestResultsForTest(testId: String): List<TestResultData> {
        return _testResults.value.filter { it.testId == testId }
    }
    
    /**
     * Get quiz results for a specific quiz
     */
    fun getQuizResultsForQuiz(quizId: String): List<QuizResultData> {
        return _quizResults.value.filter { it.quizId == quizId }
    }
    
    /**
     * Get the most common mood from history
     */
    fun getMostCommonMood(): String? {
        val moods = _moodHistory.value
        if (moods.isEmpty()) return null
        
        return moods.groupingBy { it.moodLabel }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
    
    /**
     * Get recent mood trend (last 7 days)
     */
    fun getRecentMoodTrend(): List<HomeMoodData> {
        return _moodHistory.value.take(7)
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Get mood index from mood name
     */
    private fun getMoodIndex(mood: String): Int {
        return when (mood.lowercase()) {
            "happy" -> 0
            "calm" -> 1
            "sad" -> 2
            "anxious" -> 3
            "tired" -> 4
            "angry" -> 5
            else -> 0
        }
    }
    
    /**
     * Get mood emoji from mood name
     */
    fun getMoodEmoji(mood: String): String {
        return when (mood.lowercase()) {
            "happy" -> "😊"
            "calm" -> "😌"
            "sad" -> "😢"
            "anxious" -> "😰"
            "tired" -> "😴"
            "angry" -> "😠"
            else -> "🙂"
        }
    }
    
    /**
     * Format date for display
     */
    fun formatDate(dateString: String): String {
        // Simple date formatting - in production use proper date parsing
        return try {
            // Assuming ISO 8601 format
            val parts = dateString.split("T")
            if (parts.isNotEmpty()) {
                val dateParts = parts[0].split("-")
                if (dateParts.size == 3) {
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                       "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    val month = dateParts[1].toIntOrNull()?.let { months.getOrNull(it - 1) } ?: dateParts[1]
                    val day = dateParts[2].toIntOrNull() ?: dateParts[2]
                    "$month $day"
                } else dateString
            } else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}

/**
 * Factory for creating ReflectViewModel with required dependencies
 */
class ReflectViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReflectViewModel::class.java)) {
            return ReflectViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
