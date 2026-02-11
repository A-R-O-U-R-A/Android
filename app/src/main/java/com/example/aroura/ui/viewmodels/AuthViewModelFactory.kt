package com.example.aroura.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.local.TokenManager
import com.example.aroura.data.repository.AuthRepository

/**
 * Factory for creating AuthViewModel with dependencies
 */
// TODO: Replace with Hilt/Koin DI — this manual factory should be removed once a DI framework is adopted.
class AuthViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val tokenManager = TokenManager.getInstance(application)
            val apiService = ApiClient.createAuthApiService(tokenManager)
            val authRepository = AuthRepository(apiService, tokenManager)
            
            return AuthViewModel(application, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
