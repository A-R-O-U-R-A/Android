package com.example.aroura.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API Data Models - Auth Requests & Responses
 */

// ═══════════════════════════════════════════════════════════════════════════════
// Request Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)

@Serializable
data class GoogleAuthRequest(
    val idToken: String
)

@Serializable
data class FacebookAuthRequest(
    val accessToken: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class LogoutRequest(
    val refreshToken: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════════
// Response Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class AuthResponse(
    val success: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Int? = null,
    val tokenType: String? = null,
    val user: UserResponse? = null,
    val isNewUser: Boolean = false,
    val error: String? = null,
    val code: String? = null
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val displayName: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePicture: String? = null,
    val authProvider: String,
    val isEmailVerified: Boolean = false,
    val isPremium: Boolean = false
)

@Serializable
data class TokenRefreshResponse(
    val success: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Int? = null,
    val tokenType: String? = null,
    val error: String? = null
)

@Serializable
data class VerifyTokenResponse(
    val valid: Boolean,
    val user: UserResponse? = null
)

@Serializable
data class CheckEmailResponse(
    val available: Boolean,
    val message: String
)

@Serializable
data class LogoutResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class ApiError(
    val success: Boolean = false,
    val error: String,
    val code: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════════
// User Profile Models
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class GetProfileResponse(
    val success: Boolean,
    val user: UserProfileData? = null,
    val error: String? = null
)

@Serializable
data class UserProfileData(
    val id: String,
    val email: String,
    val displayName: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePicture: String? = null,
    val bio: String? = null,
    val authProvider: String,
    val isEmailVerified: Boolean = false,
    val isPremium: Boolean = false,
    val preferences: UserPreferences? = null,
    val createdAt: String? = null
)

@Serializable
data class UserPreferences(
    val notifications: Boolean = true,
    val darkMode: Boolean = true,
    val language: String = "en",
    val devotionalType: String = "all",
    val aiMemory: Boolean = true
)

@Serializable
data class UpdateProfileRequest(
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val preferences: UserPreferences? = null
)

@Serializable
data class UpdateProfileResponse(
    val success: Boolean,
    val message: String? = null,
    val user: UserProfileData? = null,
    val error: String? = null
)

@Serializable
data class ProfilePictureResponse(
    val success: Boolean,
    val message: String? = null,
    val profilePicture: String? = null,
    val error: String? = null
)

@Serializable
data class DeleteProfilePictureResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class DeleteAccountResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)
