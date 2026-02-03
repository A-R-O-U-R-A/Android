package com.example.aroura.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * User API Service - Retrofit Interface
 * 
 * Defines all user profile and settings endpoints
 */
interface UserApiService {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    @GET("users/me")
    suspend fun getProfile(): Response<GetProfileResponse>
    
    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Profile Picture
    // ═══════════════════════════════════════════════════════════════════════════
    
    @Multipart
    @POST("users/me/profile-picture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): Response<ProfilePictureResponse>
    
    @DELETE("users/me/profile-picture")
    suspend fun deleteProfilePicture(): Response<DeleteProfilePictureResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // Account Management
    // ═══════════════════════════════════════════════════════════════════════════
    
    @DELETE("users/me")
    suspend fun deleteAccount(): Response<DeleteAccountResponse>
}
