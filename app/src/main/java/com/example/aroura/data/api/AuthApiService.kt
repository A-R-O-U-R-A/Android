package com.example.aroura.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Auth API Service - Retrofit Interface
 * 
 * Defines all authentication endpoints
 */
interface AuthApiService {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Email/Password Authentication
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // OAuth Authentication
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<AuthResponse>
    
    @POST("auth/facebook")
    suspend fun facebookAuth(@Body request: FacebookAuthRequest): Response<AuthResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Token Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<TokenRefreshResponse>
    
    @GET("auth/verify")
    suspend fun verifyToken(): Response<VerifyTokenResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Session Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<LogoutResponse>
    
    @POST("auth/logout-all")
    suspend fun logoutAll(): Response<LogoutResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════════════════════
    
    @GET("auth/check-email/{email}")
    suspend fun checkEmailAvailability(@Path("email") email: String): Response<CheckEmailResponse>
}
