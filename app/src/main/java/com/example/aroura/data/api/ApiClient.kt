package com.example.aroura.data.api

import com.example.aroura.BuildConfig
import com.example.aroura.data.local.TokenManager
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API Client - Retrofit Configuration
 * 
 * Provides configured Retrofit instance with:
 * - JWT Bearer token authentication
 * - Request/response logging (debug only)
 * - Proper timeout configuration
 */
object ApiClient {
    
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }
    
    /**
     * Create Auth Interceptor with specific TokenManager
     */
    private fun createAuthInterceptor(tokenManager: TokenManager) = Interceptor { chain ->
        val originalRequest = chain.request()
        
        // Skip auth header for login/register endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || 
            path.contains("/auth/register") ||
            path.contains("/auth/google") ||
            path.contains("/auth/facebook") ||
            path.contains("/auth/refresh") ||
            path.contains("/auth/check-email")) {
            return@Interceptor chain.proceed(originalRequest)
        }
        
        // Add auth header if token exists
        val accessToken = tokenManager.getAccessTokenSync()
        
        if (accessToken != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
    
    /**
     * Logging Interceptor - Debug builds only
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    
    /**
     * Create OkHttp Client with interceptors
     */
    private fun createOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(createAuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Create Retrofit instance
     */
    private fun createRetrofit(tokenManager: TokenManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    /**
     * Create Auth API Service with provided TokenManager
     */
    fun createAuthApiService(tokenManager: TokenManager): AuthApiService {
        return createRetrofit(tokenManager).create(AuthApiService::class.java)
    }
    
    /**
     * Create User API Service with provided TokenManager
     */
    fun createUserApiService(tokenManager: TokenManager): UserApiService {
        return createRetrofit(tokenManager).create(UserApiService::class.java)
    }
    
    /**
     * Create a service for other API endpoints
     */
    fun <T> createService(tokenManager: TokenManager, serviceClass: Class<T>): T {
        return createRetrofit(tokenManager).create(serviceClass)
    }
}
