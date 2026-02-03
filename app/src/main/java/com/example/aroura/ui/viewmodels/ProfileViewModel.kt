package com.example.aroura.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aroura.data.api.UserPreferences
import com.example.aroura.data.api.UserProfileData
import com.example.aroura.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

/**
 * Profile ViewModel - Manages user profile state and operations
 */
class ProfileViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // State
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<UserProfileData?>(null)
    val userProfile: StateFlow<UserProfileData?> = _userProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _uploadProgress = MutableStateFlow(false)
    val uploadProgress: StateFlow<Boolean> = _uploadProgress.asStateFlow()
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Load user profile from server
     */
    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _profileState.value = ProfileState.Loading
            
            userRepository.getProfile()
                .onSuccess { profile ->
                    _userProfile.value = profile
                    _profileState.value = ProfileState.Success(profile)
                    Log.d(TAG, "Profile loaded: ${profile.displayName}")
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to load profile")
                    Log.e(TAG, "Failed to load profile", error)
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Update user profile
     */
    fun updateProfile(
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        bio: String? = null,
        preferences: UserPreferences? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            
            userRepository.updateProfile(
                displayName = displayName,
                firstName = firstName,
                lastName = lastName,
                bio = bio,
                preferences = preferences
            )
                .onSuccess { profile ->
                    _userProfile.value = profile
                    _profileState.value = ProfileState.Updated(profile)
                    Log.d(TAG, "Profile updated successfully")
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to update profile")
                    Log.e(TAG, "Failed to update profile", error)
                }
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Picture Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Upload profile picture
     */
    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            _uploadProgress.value = true
            
            userRepository.uploadProfilePicture(imageUri)
                .onSuccess { profilePictureUrl ->
                    // Update local profile with new picture URL
                    _userProfile.value = _userProfile.value?.copy(
                        profilePicture = profilePictureUrl
                    )
                    _profileState.value = ProfileState.PictureUploaded(profilePictureUrl)
                    Log.d(TAG, "Profile picture uploaded: $profilePictureUrl")
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to upload picture")
                    Log.e(TAG, "Failed to upload profile picture", error)
                }
            
            _uploadProgress.value = false
        }
    }
    
    /**
     * Delete profile picture
     */
    fun deleteProfilePicture() {
        viewModelScope.launch {
            _isLoading.value = true
            
            userRepository.deleteProfilePicture()
                .onSuccess {
                    _userProfile.value = _userProfile.value?.copy(
                        profilePicture = null
                    )
                    _profileState.value = ProfileState.PictureDeleted
                    Log.d(TAG, "Profile picture deleted")
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to delete picture")
                    Log.e(TAG, "Failed to delete profile picture", error)
                }
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Account Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Delete user account
     */
    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            
            userRepository.deleteAccount()
                .onSuccess {
                    _profileState.value = ProfileState.AccountDeleted
                    Log.d(TAG, "Account deleted")
                    onSuccess()
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to delete account")
                    Log.e(TAG, "Failed to delete account", error)
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Reset state after handling
     */
    fun resetState() {
        _profileState.value = ProfileState.Initial
    }
}

/**
 * Profile State sealed class
 */
sealed class ProfileState {
    data object Initial : ProfileState()
    data object Loading : ProfileState()
    data class Success(val profile: UserProfileData) : ProfileState()
    data class Updated(val profile: UserProfileData) : ProfileState()
    data class PictureUploaded(val url: String) : ProfileState()
    data object PictureDeleted : ProfileState()
    data object AccountDeleted : ProfileState()
    data class Error(val message: String) : ProfileState()
}
