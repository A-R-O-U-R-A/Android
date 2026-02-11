package com.example.aroura.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "TokenManager"

/**
 * Token Manager - Secure Token Storage (Singleton)
 * 
 * Uses EncryptedSharedPreferences (AES-256) for secure token persistence.
 * Migrates from legacy DataStore on first launch after update.
 * Provides Flow-based reactive access via StateFlows.
 * Synchronous reads for OkHttp interceptors — no runBlocking needed.
 * 
 * IMPORTANT: Use getInstance(context) to get the singleton instance.
 */
class TokenManager private constructor(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "aroura_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_AVATAR = "user_avatar"
        private const val KEY_AUTH_PROVIDER = "auth_provider"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        private const val KEY_MIGRATION_DONE = "migration_from_datastore_done"
        
        // Legacy DataStore reference — kept only for migration
        private val Context.legacyDataStore by preferencesDataStore(name = "auth_tokens")
        
        @Volatile
        private var INSTANCE: TokenManager? = null
        
        /**
         * Get singleton instance of TokenManager.
         * Thread-safe double-checked locking pattern.
         */
        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { 
                    INSTANCE = it 
                    Log.d(TAG, "TokenManager singleton created")
                }
            }
        }
    }
    
    private val encryptedPrefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        Log.e(TAG, "Failed to create EncryptedSharedPreferences, falling back", e)
        context.getSharedPreferences("${PREFS_NAME}_fallback", Context.MODE_PRIVATE)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Reactive State (StateFlows — replaces DataStore Flows)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _accessTokenFlow = MutableStateFlow(encryptedPrefs.getString(KEY_ACCESS_TOKEN, null))
    private val _refreshTokenFlow = MutableStateFlow(encryptedPrefs.getString(KEY_REFRESH_TOKEN, null))
    private val _userIdFlow = MutableStateFlow(encryptedPrefs.getString(KEY_USER_ID, null))
    private val _userEmailFlow = MutableStateFlow(encryptedPrefs.getString(KEY_USER_EMAIL, null))
    private val _userNameFlow = MutableStateFlow(encryptedPrefs.getString(KEY_USER_NAME, null))
    private val _userAvatarFlow = MutableStateFlow(encryptedPrefs.getString(KEY_USER_AVATAR, null))
    private val _hasCompletedOnboardingFlow = MutableStateFlow(
        encryptedPrefs.getString(KEY_HAS_COMPLETED_ONBOARDING, null) == "true"
    )
    
    val accessTokenFlow: StateFlow<String?> = _accessTokenFlow.asStateFlow()
    val refreshTokenFlow: StateFlow<String?> = _refreshTokenFlow.asStateFlow()
    val userIdFlow: StateFlow<String?> = _userIdFlow.asStateFlow()
    val userEmailFlow: StateFlow<String?> = _userEmailFlow.asStateFlow()
    val userNameFlow: StateFlow<String?> = _userNameFlow.asStateFlow()
    val userAvatarFlow: StateFlow<String?> = _userAvatarFlow.asStateFlow()
    val hasCompletedOnboardingFlow: StateFlow<Boolean> = _hasCompletedOnboardingFlow.asStateFlow()
    val isLoggedInFlow: Flow<Boolean> = _accessTokenFlow.map { it != null }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Synchronous Reads (for OkHttp interceptor — no runBlocking!)
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun getAccessTokenSync(): String? = _accessTokenFlow.value
    fun getRefreshTokenSync(): String? = _refreshTokenFlow.value
    fun isLoggedIn(): Boolean = _accessTokenFlow.value != null
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Token Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
        _accessTokenFlow.value = accessToken
        _refreshTokenFlow.value = refreshToken
    }
    
    suspend fun updateAccessToken(accessToken: String) {
        encryptedPrefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
        _accessTokenFlow.value = accessToken
    }
    
    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        saveTokens(accessToken, refreshToken)
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // User Data Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    suspend fun saveUserData(
        userId: String,
        email: String,
        displayName: String,
        avatar: String?,
        authProvider: String
    ) {
        val editor = encryptedPrefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, displayName)
            .putString(KEY_AUTH_PROVIDER, authProvider)
        if (avatar != null) editor.putString(KEY_USER_AVATAR, avatar)
        editor.apply()
        
        _userIdFlow.value = userId
        _userEmailFlow.value = email
        _userNameFlow.value = displayName
        _userAvatarFlow.value = avatar
    }
    
    suspend fun setOnboardingCompleted() {
        encryptedPrefs.edit()
            .putString(KEY_HAS_COMPLETED_ONBOARDING, "true")
            .apply()
        _hasCompletedOnboardingFlow.value = true
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Clear Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    suspend fun clearAll() {
        encryptedPrefs.edit().clear().apply()
        _accessTokenFlow.value = null
        _refreshTokenFlow.value = null
        _userIdFlow.value = null
        _userEmailFlow.value = null
        _userNameFlow.value = null
        _userAvatarFlow.value = null
        _hasCompletedOnboardingFlow.value = false
    }
    
    suspend fun clearTokens() {
        encryptedPrefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        _accessTokenFlow.value = null
        _refreshTokenFlow.value = null
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Migration from Legacy DataStore
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Migrate tokens from legacy plain-text DataStore to EncryptedSharedPreferences.
     * Call once during app startup (MainActivity.onCreate).
     * Safe to call multiple times — migration flag prevents re-execution.
     */
    suspend fun migrateFromLegacyDataStoreIfNeeded() {
        if (encryptedPrefs.getBoolean(KEY_MIGRATION_DONE, false)) return
        
        try {
            val legacyPrefs = context.legacyDataStore.data.first()
            val oldKeys = listOf(
                Pair(stringPreferencesKey("access_token"), KEY_ACCESS_TOKEN),
                Pair(stringPreferencesKey("refresh_token"), KEY_REFRESH_TOKEN),
                Pair(stringPreferencesKey("user_id"), KEY_USER_ID),
                Pair(stringPreferencesKey("user_email"), KEY_USER_EMAIL),
                Pair(stringPreferencesKey("user_name"), KEY_USER_NAME),
                Pair(stringPreferencesKey("user_avatar"), KEY_USER_AVATAR),
                Pair(stringPreferencesKey("auth_provider"), KEY_AUTH_PROVIDER),
                Pair(stringPreferencesKey("has_completed_onboarding"), KEY_HAS_COMPLETED_ONBOARDING),
            )
            
            val editor = encryptedPrefs.edit()
            var migrated = false
            
            for ((oldKey, newKey) in oldKeys) {
                val value: String? = legacyPrefs[oldKey]
                if (value != null) {
                    editor.putString(newKey, value)
                    migrated = true
                }
            }
            
            editor.putBoolean(KEY_MIGRATION_DONE, true).apply()
            
            if (migrated) {
                // Refresh all StateFlows with migrated data
                _accessTokenFlow.value = encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
                _refreshTokenFlow.value = encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
                _userIdFlow.value = encryptedPrefs.getString(KEY_USER_ID, null)
                _userEmailFlow.value = encryptedPrefs.getString(KEY_USER_EMAIL, null)
                _userNameFlow.value = encryptedPrefs.getString(KEY_USER_NAME, null)
                _userAvatarFlow.value = encryptedPrefs.getString(KEY_USER_AVATAR, null)
                _hasCompletedOnboardingFlow.value =
                    encryptedPrefs.getString(KEY_HAS_COMPLETED_ONBOARDING, null) == "true"
                
                // Clear old unencrypted DataStore
                context.legacyDataStore.edit { it.clear() }
                Log.d(TAG, "Migrated from DataStore to EncryptedSharedPreferences")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Migration failed (may be first install)", e)
            encryptedPrefs.edit().putBoolean(KEY_MIGRATION_DONE, true).apply()
        }
    }
}
