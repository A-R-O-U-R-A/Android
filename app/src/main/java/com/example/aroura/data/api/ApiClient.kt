package com.example.aroura.data.api

import com.example.aroura.BuildConfig
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API Client - Retrofit Configuration
 * 
 * Provides configured Retrofit instance with:
 * - JWT Bearer token authentication
 * - Automatic token refresh on 401
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
        
        // Debug logging
        android.util.Log.d("ApiClient", "Request to: ${originalRequest.url}")
        android.util.Log.d("ApiClient", "Token present: ${accessToken != null}")
        
        if (accessToken != null) {
            android.util.Log.d("ApiClient", "Adding Authorization header, token length: ${accessToken.length}")
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        } else {
            android.util.Log.w("ApiClient", "No token available for authenticated request!")
            chain.proceed(originalRequest)
        }
    }
    
    /**
     * Token Authenticator - Automatically refreshes token on 401
     */
    private fun createTokenAuthenticator(tokenManager: TokenManager) = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            // Don't retry if we've already tried to refresh
            if (response.request.header("X-Retry-Auth") != null) {
                android.util.Log.e("ApiClient", "Token refresh already attempted, giving up")
                return null
            }
            
            android.util.Log.d("ApiClient", "Got 401, attempting token refresh...")
            
            val refreshToken = tokenManager.getRefreshTokenSync()
            if (refreshToken == null) {
                android.util.Log.e("ApiClient", "No refresh token available")
                return null
            }
            
            // Make synchronous refresh request
            val refreshRequest = Request.Builder()
                .url("${BuildConfig.API_BASE_URL}auth/refresh")
                .post(
                    JSONObject().put("refreshToken", refreshToken).toString()
                        .toRequestBody("application/json".toMediaType())
                )
                .build()
            
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
            
            return try {
                val refreshResponse = client.newCall(refreshRequest).execute()
                
                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body?.string()
                    val jsonResponse = JSONObject(body ?: "{}")
                    
                    val newAccessToken = jsonResponse.optString("accessToken")
                    val newRefreshToken = jsonResponse.optString("refreshToken")
                    
                    if (newAccessToken.isNotEmpty()) {
                        // Save new tokens
                        runBlocking {
                            if (newRefreshToken.isNotEmpty()) {
                                tokenManager.updateTokens(newAccessToken, newRefreshToken)
                            } else {
                                tokenManager.updateAccessToken(newAccessToken)
                            }
                        }
                        
                        android.util.Log.d("ApiClient", "Token refreshed successfully!")
                        
                        // Retry original request with new token
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .header("X-Retry-Auth", "true")
                            .build()
                    } else {
                        android.util.Log.e("ApiClient", "Refresh response missing accessToken")
                        null
                    }
                } else {
                    android.util.Log.e("ApiClient", "Token refresh failed: ${refreshResponse.code}")
                    // Clear tokens on refresh failure - user needs to re-login
                    runBlocking { tokenManager.clearAll() }
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e("ApiClient", "Token refresh exception: ${e.message}")
                null
            }
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
            .authenticator(createTokenAuthenticator(tokenManager))
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
     * Create Chat API Service with provided TokenManager
     */
    fun createChatApiService(tokenManager: TokenManager): ChatApiService {
        return createRetrofit(tokenManager).create(ChatApiService::class.java)
    }
    
    /**
     * Create Audio API Service with provided TokenManager
     */
    fun createAudioApiService(tokenManager: TokenManager): AudioApiService {
        return createRetrofit(tokenManager).create(AudioApiService::class.java)
    }
    
    /**
     * Create Reflect API Service with provided TokenManager
     */
    fun createReflectApiService(tokenManager: TokenManager): ReflectApiService {
        return createRetrofit(tokenManager).create(ReflectApiService::class.java)
    }
    
    /**
     * Create a service for other API endpoints
     */
    fun <T> createService(tokenManager: TokenManager, serviceClass: Class<T>): T {
        return createRetrofit(tokenManager).create(serviceClass)
    }
}
