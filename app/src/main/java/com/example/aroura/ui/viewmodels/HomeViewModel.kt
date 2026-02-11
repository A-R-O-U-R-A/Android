package com.example.aroura.ui.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.api.CompleteRoutineRequest
import com.example.aroura.data.api.HomeMoodData
import com.example.aroura.data.api.QuestProgressData
import com.example.aroura.data.api.ReflectApiService
import com.example.aroura.data.api.SaveHomeMoodRequest
import com.example.aroura.data.api.TestResultSummaryData
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * HomeViewModel - Manages home screen state
 * 
 * Features:
 * - Daily affirmation with Gemini AI
 * - Home mood check-in saving
 * - Routine tracking
 * - Quest progress
 * - Test results summary
 */
class HomeViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {
    
    companion object {
        private const val TAG = "HomeViewModel"
    }
    
    private val reflectApi: ReflectApiService = ApiClient.createReflectApiService(tokenManager)
    
    // ═══════════════════════════════════════════════════════════════════════════
    // UI STATE
    // ═══════════════════════════════════════════════════════════════════════════
    
    data class HomeUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        
        // Affirmation
        val dailyAffirmation: String = "You are allowed to be exactly who you are.",
        val affirmationLoading: Boolean = false,
        
        // Mood check-in
        val selectedMood: Int? = null,
        val todaysMood: HomeMoodData? = null,
        val moodSaved: Boolean = false,
        val moodSaving: Boolean = false,
        val showMoodSaveSuccess: Boolean = false,
        
        // Routine
        val routineCompletions: Map<String, List<String>> = emptyMap(), // date -> taskIds
        val selectedDay: Int = getCurrentDayIndex(),
        
        // Quest
        val questProgress: QuestProgressData? = null,
        
        // Test results
        val completedTestsCount: Int = 0,
        val testResultsSummary: List<TestResultSummaryData> = emptyList()
    )
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // ═══════════════════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════════════
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Load all data in parallel
            launch { fetchDailyAffirmation() }
            launch { fetchTodaysMood() }
            launch { fetchQuestProgress() }
            launch { fetchTestResultsSummary() }
            launch { fetchRoutineCompletions() }
            
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // DAILY AFFIRMATION
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun fetchDailyAffirmation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(affirmationLoading = true)
            
            try {
                val mood = _uiState.value.todaysMood?.moodLabel
                val response = reflectApi.getDailyAffirmation(mood)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        dailyAffirmation = response.body()!!.affirmation,
                        affirmationLoading = false
                    )
                    Log.d(TAG, "Affirmation loaded: ${response.body()!!.affirmation}")
                } else {
                    Log.e(TAG, "Affirmation fetch failed: ${response.code()}")
                    _uiState.value = _uiState.value.copy(affirmationLoading = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Affirmation fetch error", e)
                _uiState.value = _uiState.value.copy(affirmationLoading = false)
            }
        }
    }
    
    fun shareAffirmation(context: Context) {
        val affirmation = _uiState.value.dailyAffirmation
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "\"$affirmation\"\n\n— Shared from A.R.O.U.R.A 💜")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Affirmation"))
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MOOD CHECK-IN
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun selectMood(moodIndex: Int) {
        _uiState.value = _uiState.value.copy(
            selectedMood = moodIndex,
            moodSaved = false,
            showMoodSaveSuccess = false
        )
    }
    
    fun saveMood(note: String = "") {
        val moodIndex = _uiState.value.selectedMood ?: return
        
        val moodData = getMoodDataByIndex(moodIndex)
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(moodSaving = true)
            
            try {
                val request = SaveHomeMoodRequest(
                    moodIndex = moodIndex,
                    moodLabel = moodData.label,
                    moodEmoji = moodData.emoji,
                    note = note
                )
                
                val response = reflectApi.saveHomeMood(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        todaysMood = response.body()!!.mood,
                        moodSaved = true,
                        moodSaving = false,
                        showMoodSaveSuccess = true
                    )
                    Log.d(TAG, "Mood saved successfully")
                } else {
                    Log.e(TAG, "Mood save failed: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        moodSaving = false,
                        error = "Failed to save mood"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Mood save error", e)
                _uiState.value = _uiState.value.copy(
                    moodSaving = false,
                    error = "Error saving mood: ${e.message}"
                )
            }
        }
    }
    
    fun dismissMoodSaveSuccess() {
        _uiState.value = _uiState.value.copy(showMoodSaveSuccess = false)
    }
    
    private fun fetchTodaysMood() {
        viewModelScope.launch {
            try {
                val response = reflectApi.getTodayMood()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val mood = response.body()!!.mood
                    if (mood != null) {
                        _uiState.value = _uiState.value.copy(
                            todaysMood = mood,
                            selectedMood = mood.moodIndex,
                            moodSaved = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch today's mood error", e)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // ROUTINE TRACKING
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun selectDay(dayIndex: Int) {
        // Only allow selecting today or past days
        val today = getCurrentDayIndex()
        if (dayIndex <= today) {
            _uiState.value = _uiState.value.copy(selectedDay = dayIndex)
        }
    }
    
    fun completeRoutineTask(taskId: String, category: String, title: String) {
        viewModelScope.launch {
            try {
                val request = CompleteRoutineRequest(
                    taskId = taskId,
                    category = category,
                    title = title
                )
                
                val response = reflectApi.completeRoutineTask(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh completions
                    fetchRoutineCompletions()
                    Log.d(TAG, "Routine task completed: $taskId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Complete routine task error", e)
            }
        }
    }
    
    private fun fetchRoutineCompletions() {
        viewModelScope.launch {
            try {
                // Get completions for the last 7 days
                val response = reflectApi.getRoutineCompletions()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val completions = response.body()!!.completions
                    val taskIdsByDate = completions.mapValues { (_, completionList) ->
                        completionList.map { it.taskId }
                    }
                    _uiState.value = _uiState.value.copy(routineCompletions = taskIdsByDate)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch routine completions error", e)
            }
        }
    }
    
    fun isTaskCompletedForDay(taskId: String, dayIndex: Int): Boolean {
        val date = getDateForDayIndex(dayIndex)
        return _uiState.value.routineCompletions[date]?.contains(taskId) == true
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // QUEST PROGRESS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun fetchQuestProgress() {
        viewModelScope.launch {
            try {
                val response = reflectApi.getQuestProgress()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        questProgress = response.body()!!.progress
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch quest progress error", e)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // TEST RESULTS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun fetchTestResultsSummary() {
        viewModelScope.launch {
            try {
                val response = reflectApi.getTestResultsSummary()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        completedTestsCount = body.completedCount,
                        testResultsSummary = body.tests
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fetch test results summary error", e)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun refresh() {
        loadInitialData()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MOOD DATA
// ═══════════════════════════════════════════════════════════════════════════════

data class MoodInfo(
    val emoji: String,
    val label: String
)

fun getMoodDataByIndex(index: Int): MoodInfo {
    return when (index) {
        0 -> MoodInfo("😔", "Struggling")
        1 -> MoodInfo("😕", "Meh")
        2 -> MoodInfo("😐", "Okay")
        3 -> MoodInfo("🙂", "Good")
        4 -> MoodInfo("😄", "Amazing")
        else -> MoodInfo("😐", "Okay")
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DATE HELPERS
// ═══════════════════════════════════════════════════════════════════════════════

fun getCurrentDayIndex(): Int {
    val calendar = java.util.Calendar.getInstance()
    // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ... Saturday=7
    // We want Monday=0, ... Sunday=6
    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    return when (dayOfWeek) {
        java.util.Calendar.MONDAY -> 0
        java.util.Calendar.TUESDAY -> 1
        java.util.Calendar.WEDNESDAY -> 2
        java.util.Calendar.THURSDAY -> 3
        java.util.Calendar.FRIDAY -> 4
        java.util.Calendar.SATURDAY -> 5
        java.util.Calendar.SUNDAY -> 6
        else -> 0
    }
}

fun getDateForDayIndex(dayIndex: Int): String {
    val calendar = java.util.Calendar.getInstance()
    val currentDayIndex = getCurrentDayIndex()
    
    // Calculate days to subtract
    val daysToSubtract = currentDayIndex - dayIndex
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysToSubtract)
    
    // Format as YYYY-MM-DD
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    
    return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

/**
 * Factory for creating HomeViewModel with required dependencies
 */
class HomeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val tokenManager = TokenManager.getInstance(application)
            return HomeViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
