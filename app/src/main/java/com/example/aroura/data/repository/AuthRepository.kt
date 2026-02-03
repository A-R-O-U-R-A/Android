package com.example.aroura.data.repository

import com.example.aroura.data.api.*
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Auth Repository - Single source of truth for authentication
 * 
 * Handles:
 * - Email/password login & registration
 * - Google OAuth
 * - Facebook OAuth
 * - Token management
 * - Session management
 */
class AuthRepository(
    private val authService: AuthApiService,
    private val tokenManager: TokenManager
) {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Email/Password Authentication
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Register with email and password
     */
    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<AuthResult> = withContext(Dispatchers.IO) {
        try {
            val response = authService.register(
                RegisterRequest(email, password, displayName)
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                handleAuthSuccess(body)
                Result.success(AuthResult.Success(
                    user = body.user!!,
                    isNewUser = body.isNewUser
                ))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Registration failed"
                Result.failure(AuthException(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(AuthException(e.message ?: "Network error"))
        }
    }
    
    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Result<AuthResult> = 
        withContext(Dispatchers.IO) {
            try {
                val response = authService.login(LoginRequest(email, password))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    handleAuthSuccess(body)
                    Result.success(AuthResult.Success(
                        user = body.user!!,
                        isNewUser = false
                    ))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseError(errorBody) ?: "Invalid credentials"
                    
                    // Check for specific error codes
                    if (errorMessage.contains("locked", ignoreCase = true)) {
                        Result.failure(AuthException(errorMessage, AuthErrorCode.ACCOUNT_LOCKED))
                    } else {
                        Result.failure(AuthException(errorMessage, AuthErrorCode.INVALID_CREDENTIALS))
                    }
                }
            } catch (e: Exception) {
                Result.failure(AuthException(e.message ?: "Network error"))
            }
        }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // OAuth Authentication
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Authenticate with Google ID Token
     */
    suspend fun loginWithGoogle(idToken: String): Result<AuthResult> = 
        withContext(Dispatchers.IO) {
            try {
                val response = authService.googleAuth(GoogleAuthRequest(idToken))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    handleAuthSuccess(body)
                    Result.success(AuthResult.Success(
                        user = body.user!!,
                        isNewUser = body.isNewUser
                    ))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseError(errorBody) ?: "Google authentication failed"
                    Result.failure(AuthException(errorMessage, AuthErrorCode.OAUTH_FAILED))
                }
            } catch (e: Exception) {
                Result.failure(AuthException(e.message ?: "Network error"))
            }
        }
    
    /**
     * Authenticate with Facebook Access Token
     */
    suspend fun loginWithFacebook(accessToken: String): Result<AuthResult> = 
        withContext(Dispatchers.IO) {
            try {
                val response = authService.facebookAuth(FacebookAuthRequest(accessToken))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    handleAuthSuccess(body)
                    Result.success(AuthResult.Success(
                        user = body.user!!,
                        isNewUser = body.isNewUser
                    ))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseError(errorBody) ?: "Facebook authentication failed"
                    Result.failure(AuthException(errorMessage, AuthErrorCode.OAUTH_FAILED))
                }
            } catch (e: Exception) {
                Result.failure(AuthException(e.message ?: "Network error"))
            }
        }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Token Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Refresh access token
     */
    suspend fun refreshToken(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = tokenManager.getRefreshTokenSync()
                ?: return@withContext Result.failure(AuthException("No refresh token"))
            
            val response = authService.refreshToken(RefreshTokenRequest(refreshToken))
            
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                if (body.accessToken != null && body.refreshToken != null) {
                    tokenManager.updateTokens(body.accessToken, body.refreshToken)
                    Result.success(true)
                } else {
                    Result.failure(AuthException("Invalid token response"))
                }
            } else {
                // Refresh token expired - user needs to re-login
                tokenManager.clearAll()
                Result.failure(AuthException("Session expired", AuthErrorCode.SESSION_EXPIRED))
            }
        } catch (e: Exception) {
            Result.failure(AuthException(e.message ?: "Token refresh failed"))
        }
    }
    
    /**
     * Verify current token is valid
     */
    suspend fun verifyToken(): Result<UserResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.verifyToken()
            
            if (response.isSuccessful && response.body()?.valid == true) {
                Result.success(response.body()!!.user!!)
            } else {
                Result.failure(AuthException("Invalid token"))
            }
        } catch (e: Exception) {
            Result.failure(AuthException(e.message ?: "Verification failed"))
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Session Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Logout current session
     */
    suspend fun logout(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = tokenManager.getRefreshTokenSync()
            authService.logout(LogoutRequest(refreshToken))
            tokenManager.clearAll()
            Result.success(true)
        } catch (e: Exception) {
            // Clear local tokens even if API call fails
            tokenManager.clearAll()
            Result.success(true)
        }
    }
    
    /**
     * Logout from all devices
     */
    suspend fun logoutAll(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            authService.logoutAll()
            tokenManager.clearAll()
            Result.success(true)
        } catch (e: Exception) {
            tokenManager.clearAll()
            Result.success(true)
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Check if email is available
     */
    suspend fun checkEmailAvailability(email: String): Result<Boolean> = 
        withContext(Dispatchers.IO) {
            try {
                val response = authService.checkEmailAvailability(email)
                if (response.isSuccessful) {
                    Result.success(response.body()?.available ?: false)
                } else {
                    Result.failure(AuthException("Could not check email"))
                }
            } catch (e: Exception) {
                Result.failure(AuthException(e.message ?: "Network error"))
            }
        }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Private Helpers
    // ═══════════════════════════════════════════════════════════════════════════
    
    private suspend fun handleAuthSuccess(response: AuthResponse) {
        // Save tokens
        if (response.accessToken != null && response.refreshToken != null) {
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }
        
        // Save user data
        response.user?.let { user ->
            tokenManager.saveUserData(
                userId = user.id,
                email = user.email,
                displayName = user.displayName,
                avatar = user.profilePicture,
                authProvider = user.authProvider
            )
        }
    }
    
    private fun parseError(errorBody: String?): String? {
        if (errorBody.isNullOrEmpty()) return null
        return try {
            // Simple parsing - look for "error" field
            val regex = """"error"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(errorBody)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Auth Result Types
// ═══════════════════════════════════════════════════════════════════════════════

sealed class AuthResult {
    data class Success(
        val user: UserResponse,
        val isNewUser: Boolean
    ) : AuthResult()
}

enum class AuthErrorCode {
    INVALID_CREDENTIALS,
    ACCOUNT_LOCKED,
    EMAIL_EXISTS,
    OAUTH_FAILED,
    SESSION_EXPIRED,
    NETWORK_ERROR,
    UNKNOWN
}

class AuthException(
    message: String,
    val code: AuthErrorCode = AuthErrorCode.UNKNOWN
) : Exception(message)
