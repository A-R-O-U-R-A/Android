package com.example.aroura.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Token Manager - Secure Token Storage
 * 
 * Uses DataStore Preferences for token persistence.
 * In production, consider using EncryptedSharedPreferences for added security.
 */
class TokenManager(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")
        
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        private val AUTH_PROVIDER_KEY = stringPreferencesKey("auth_provider")
        private val HAS_COMPLETED_ONBOARDING_KEY = stringPreferencesKey("has_completed_onboarding")
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Token Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Save tokens after successful authentication
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }
    
    /**
     * Get access token as Flow
     */
    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]
    }
    
    /**
     * Get refresh token as Flow
     */
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_TOKEN_KEY]
    }
    
    /**
     * Get access token synchronously (for interceptor)
     */
    fun getAccessTokenSync(): String? {
        return runBlocking {
            context.dataStore.data.first()[ACCESS_TOKEN_KEY]
        }
    }
    
    /**
     * Get refresh token synchronously
     */
    fun getRefreshTokenSync(): String? {
        return runBlocking {
            context.dataStore.data.first()[REFRESH_TOKEN_KEY]
        }
    }
    
    /**
     * Update access token (after refresh)
     */
    suspend fun updateAccessToken(accessToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
        }
    }
    
    /**
     * Update both tokens (after refresh with rotation)
     */
    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // User Data Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Save user data after authentication
     */
    suspend fun saveUserData(
        userId: String,
        email: String,
        displayName: String,
        avatar: String?,
        authProvider: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_NAME_KEY] = displayName
            avatar?.let { prefs[USER_AVATAR_KEY] = it }
            prefs[AUTH_PROVIDER_KEY] = authProvider
        }
    }
    
    /**
     * Get user ID as Flow
     */
    val userIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }
    
    /**
     * Get user email as Flow
     */
    val userEmailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY]
    }
    
    /**
     * Get user display name as Flow
     */
    val userNameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }
    
    /**
     * Get user avatar URL as Flow
     */
    val userAvatarFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_AVATAR_KEY]
    }
    
    /**
     * Check if user has completed onboarding (first launch)
     */
    val hasCompletedOnboardingFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_COMPLETED_ONBOARDING_KEY] == "true"
    }
    
    /**
     * Mark onboarding as completed
     */
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs ->
            prefs[HAS_COMPLETED_ONBOARDING_KEY] = "true"
        }
    }
    
    /**
     * Check if user is logged in
     */
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY] != null
    }
    
    /**
     * Check if logged in synchronously
     */
    fun isLoggedIn(): Boolean {
        return runBlocking {
            context.dataStore.data.first()[ACCESS_TOKEN_KEY] != null
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Clear Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Clear all tokens and user data (logout)
     */
    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
    
    /**
     * Clear tokens only
     */
    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }
}
