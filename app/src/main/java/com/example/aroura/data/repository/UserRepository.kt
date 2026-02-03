package com.example.aroura.data.repository

import android.content.Context
import android.net.Uri
import com.example.aroura.data.api.*
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * User Repository - Manages user profile operations
 */
class UserRepository(
    private val userService: UserApiService,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get current user profile
     */
    suspend fun getProfile(): Result<UserProfileData> = withContext(Dispatchers.IO) {
        try {
            val response = userService.getProfile()
            
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.user!!
                // Update local cache
                tokenManager.saveUserData(
                    userId = user.id,
                    email = user.email,
                    displayName = user.displayName,
                    avatar = user.profilePicture,
                    authProvider = user.authProvider
                )
                Result.success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to get profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        bio: String? = null,
        preferences: UserPreferences? = null
    ): Result<UserProfileData> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(
                displayName = displayName,
                firstName = firstName,
                lastName = lastName,
                bio = bio,
                preferences = preferences
            )
            
            val response = userService.updateProfile(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val user = response.body()!!.user!!
                // Update local cache
                displayName?.let { tokenManager.saveUserData(
                    userId = user.id,
                    email = user.email,
                    displayName = it,
                    avatar = user.profilePicture,
                    authProvider = user.authProvider
                ) }
                Result.success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Picture Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Upload profile picture from URI
     */
    suspend fun uploadProfilePicture(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Read file from URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(Exception("Cannot read file"))
            
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            // Get MIME type
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            
            // Create multipart body
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                "file",
                "profile_picture.${mimeType.substringAfter("/")}",
                requestBody
            )
            
            val response = userService.uploadProfilePicture(part)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val profilePictureUrl = response.body()!!.profilePicture!!
                Result.success(profilePictureUrl)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to upload profile picture"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete profile picture
     */
    suspend fun deleteProfilePicture(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = userService.deleteProfilePicture()
            
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to delete profile picture"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Account Operations
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Delete user account
     */
    suspend fun deleteAccount(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = userService.deleteAccount()
            
            if (response.isSuccessful && response.body()?.success == true) {
                tokenManager.clearAll()
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to delete account"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
