package com.example.aroura.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aroura.BuildConfig
import com.example.aroura.data.api.UserResponse
import com.example.aroura.data.repository.AuthErrorCode
import com.example.aroura.data.repository.AuthException
import com.example.aroura.data.repository.AuthRepository
import com.example.aroura.data.repository.AuthResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

private const val TAG = "AuthViewModel"

/**
 * Auth ViewModel - Manages authentication state and operations
 */
class AuthViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {
    
    private val credentialManager = CredentialManager.create(application)
    
    // ═══════════════════════════════════════════════════════════════════════════
    // State
    // ═══════════════════════════════════════════════════════════════════════════
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser.asStateFlow()
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Check Initial Auth State
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun checkAuthState() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                // Verify token is still valid
                authRepository.verifyToken()
                    .onSuccess { user ->
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated(user)
                    }
                    .onFailure {
                        // Try to refresh token
                        authRepository.refreshToken()
                            .onSuccess {
                                authRepository.verifyToken()
                                    .onSuccess { user ->
                                        _currentUser.value = user
                                        _authState.value = AuthState.Authenticated(user)
                                    }
                                    .onFailure {
                                        _authState.value = AuthState.Unauthenticated
                                    }
                            }
                            .onFailure {
                                _authState.value = AuthState.Unauthenticated
                            }
                    }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Email/Password Authentication
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Register with email and password
     */
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            
            authRepository.register(email, password, displayName)
                .onSuccess { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _currentUser.value = result.user
                            _authState.value = AuthState.Authenticated(
                                user = result.user,
                                isNewUser = result.isNewUser
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        message = error.message ?: "Registration failed",
                        code = (error as? AuthException)?.code
                    )
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            
            authRepository.login(email, password)
                .onSuccess { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _currentUser.value = result.user
                            _authState.value = AuthState.Authenticated(result.user)
                        }
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        message = error.message ?: "Login failed",
                        code = (error as? AuthException)?.code
                    )
                }
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Google Sign-In
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Generate a nonce for security
     */
    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    /**
     * Initiate Google Sign-In using Sign In With Google button flow
     */
    suspend fun signInWithGoogle(context: Context): Result<GetCredentialResponse> {
        return try {
            Log.d(TAG, "Starting Google Sign-In with client ID: ${BuildConfig.GOOGLE_WEB_CLIENT_ID}")
            
            // Use SignInWithGoogleOption for the button flow - more reliable
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
            )
                .setNonce(generateNonce())
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()
            
            Log.d(TAG, "Calling credentialManager.getCredential")
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            
            Log.d(TAG, "Got credential response: ${result.credential.type}")
            Result.success(result)
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credentials available", e)
            Result.failure(Exception("No Google account found. Please add a Google account to your device."))
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled sign-in")
            Result.failure(Exception("Sign-in cancelled"))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException", e)
            Result.failure(Exception("Google sign-in failed: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In", e)
            Result.failure(e)
        }
    }
    
    /**
     * Handle Google Sign-In result
     */
    fun handleGoogleSignInResult(result: GetCredentialResponse) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            
            try {
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken
                            
                            // Send to backend
                            authRepository.loginWithGoogle(idToken)
                                .onSuccess { authResult ->
                                    when (authResult) {
                                        is AuthResult.Success -> {
                                            _currentUser.value = authResult.user
                                            _authState.value = AuthState.Authenticated(
                                                user = authResult.user,
                                                isNewUser = authResult.isNewUser
                                            )
                                        }
                                    }
                                }
                                .onFailure { error ->
                                    _authState.value = AuthState.Error(
                                        message = error.message ?: "Google sign-in failed",
                                        code = AuthErrorCode.OAUTH_FAILED
                                    )
                                }
                        }
                    }
                    else -> {
                        _authState.value = AuthState.Error(
                            message = "Unexpected credential type",
                            code = AuthErrorCode.OAUTH_FAILED
                        )
                    }
                }
            } catch (e: GoogleIdTokenParsingException) {
                _authState.value = AuthState.Error(
                    message = "Failed to parse Google token",
                    code = AuthErrorCode.OAUTH_FAILED
                )
            }
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Facebook Sign-In
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Handle Facebook login result
     */
    fun handleFacebookSignInResult(accessToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthState.Loading
            
            authRepository.loginWithFacebook(accessToken)
                .onSuccess { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            _currentUser.value = result.user
                            _authState.value = AuthState.Authenticated(
                                user = result.user,
                                isNewUser = result.isNewUser
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        message = error.message ?: "Facebook sign-in failed",
                        code = AuthErrorCode.OAUTH_FAILED
                    )
                }
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Logout
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Logout current session
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            
            authRepository.logout()
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
            
            _isLoading.value = false
        }
    }
    
    /**
     * Logout from all devices
     */
    fun logoutAll() {
        viewModelScope.launch {
            _isLoading.value = true
            
            authRepository.logoutAll()
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
            
            _isLoading.value = false
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Error Handling
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Clear error state
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
    
    /**
     * Reset to initial state
     */
    fun resetState() {
        _authState.value = AuthState.Unauthenticated
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Auth State
// ═══════════════════════════════════════════════════════════════════════════════

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    
    data class Authenticated(
        val user: UserResponse,
        val isNewUser: Boolean = false
    ) : AuthState()
    
    data class Error(
        val message: String,
        val code: AuthErrorCode? = null
    ) : AuthState()
}
