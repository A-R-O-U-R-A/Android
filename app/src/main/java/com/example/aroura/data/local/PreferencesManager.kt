package com.example.aroura.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Preferences Manager - Local Preference Storage
 * 
 * Persists user preferences and local-only data using DataStore.
 * For non-sensitive data like language, privacy toggles, and devotional choices.
 */
class PreferencesManager(private val context: Context) {
    
    companion object {
        private val Context.prefsDataStore by preferencesDataStore(name = "app_preferences")
        
        // Language
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        
        // Privacy toggles (stored as "true"/"false")
        private val PRIVACY_USAGE_DATA = stringPreferencesKey("privacy_usage_data")
        private val PRIVACY_PERSONALIZATION = stringPreferencesKey("privacy_personalization")
        private val PRIVACY_ANALYTICS = stringPreferencesKey("privacy_analytics")
        
        // Devotional traditions (comma-separated)
        private val DEVOTIONAL_TRADITIONS = stringPreferencesKey("devotional_traditions")
        
        // Trusted contacts (pipe-separated names)
        private val TRUSTED_CONTACTS = stringPreferencesKey("trusted_contacts")
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Language
    // ═══════════════════════════════════════════════════════════════════════════
    
    val selectedLanguageFlow: Flow<String> = context.prefsDataStore.data.map {
        it[SELECTED_LANGUAGE] ?: "English"
    }
    
    suspend fun saveLanguage(language: String) {
        context.prefsDataStore.edit { it[SELECTED_LANGUAGE] = language }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Privacy Toggles
    // ═══════════════════════════════════════════════════════════════════════════
    
    val usageDataFlow: Flow<Boolean> = context.prefsDataStore.data.map {
        it[PRIVACY_USAGE_DATA] != "false" // Default true
    }
    
    val personalizationFlow: Flow<Boolean> = context.prefsDataStore.data.map {
        it[PRIVACY_PERSONALIZATION] != "false" // Default true
    }
    
    val analyticsFlow: Flow<Boolean> = context.prefsDataStore.data.map {
        it[PRIVACY_ANALYTICS] == "true" // Default false
    }
    
    suspend fun saveUsageData(enabled: Boolean) {
        context.prefsDataStore.edit { it[PRIVACY_USAGE_DATA] = enabled.toString() }
    }
    
    suspend fun savePersonalization(enabled: Boolean) {
        context.prefsDataStore.edit { it[PRIVACY_PERSONALIZATION] = enabled.toString() }
    }
    
    suspend fun saveAnalytics(enabled: Boolean) {
        context.prefsDataStore.edit { it[PRIVACY_ANALYTICS] = enabled.toString() }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Devotional Traditions
    // ═══════════════════════════════════════════════════════════════════════════
    
    val devotionalTraditionsFlow: Flow<Set<String>> = context.prefsDataStore.data.map {
        val raw = it[DEVOTIONAL_TRADITIONS] ?: ""
        if (raw.isEmpty()) emptySet() else raw.split(",").toSet()
    }
    
    suspend fun saveDevotionalTraditions(traditions: Set<String>) {
        context.prefsDataStore.edit { it[DEVOTIONAL_TRADITIONS] = traditions.joinToString(",") }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Trusted Contacts
    // ═══════════════════════════════════════════════════════════════════════════
    
    val trustedContactsFlow: Flow<List<String>> = context.prefsDataStore.data.map {
        val raw = it[TRUSTED_CONTACTS] ?: ""
        if (raw.isEmpty()) emptyList() else raw.split("|||")
    }
    
    suspend fun saveTrustedContacts(contacts: List<String>) {
        context.prefsDataStore.edit { it[TRUSTED_CONTACTS] = contacts.joinToString("|||") }
    }
}
