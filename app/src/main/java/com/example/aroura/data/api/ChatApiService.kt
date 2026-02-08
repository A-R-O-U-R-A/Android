package com.example.aroura.data.api

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

/**
 * Chat API Service
 * 
 * Endpoints for AI chat with Counselor and Best Friend personas
 */
interface ChatApiService {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SEND MESSAGE
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Send a message and get AI response (non-streaming)
     */
    @POST("chat/message")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>
    
    // ═══════════════════════════════════════════════════════════════════════════
    // CONVERSATIONS
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get list of user's conversations
     */
    @GET("chat/conversations")
    suspend fun getConversations(
        @Query("persona") persona: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<ConversationsResponse>
    
    /**
     * Get specific conversation with messages
     */
    @GET("chat/conversation/{id}")
    suspend fun getConversation(
        @Path("id") conversationId: String
    ): Response<ConversationDetailResponse>
    
    /**
     * Get messages for a conversation with pagination
     */
    @GET("chat/conversation/{id}/messages")
    suspend fun getMessages(
        @Path("id") conversationId: String,
        @Query("limit") limit: Int = 50,
        @Query("before") beforeMessageId: String? = null
    ): Response<MessagesResponse>
    
    /**
     * Start a new conversation
     */
    @POST("chat/conversation/new")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): Response<CreateConversationResponse>
    
    /**
     * Delete/archive a conversation
     */
    @DELETE("chat/conversation/{id}")
    suspend fun deleteConversation(
        @Path("id") conversationId: String
    ): Response<DeleteConversationResponse>
    
    /**
     * Get active conversation for a persona
     */
    @GET("chat/active/{persona}")
    suspend fun getActiveConversation(
        @Path("persona") persona: String
    ): Response<ActiveConversationResponse>
    
    /**
     * Health check for chat services
     */
    @GET("chat/health")
    suspend fun healthCheck(): Response<ChatHealthResponse>
}

// ═══════════════════════════════════════════════════════════════════════════════
// REQUEST MODELS
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class SendMessageRequest(
    val message: String,
    val persona: String, // "counselor" or "bestfriend"
    val conversationId: String? = null
)

@Serializable
data class CreateConversationRequest(
    val persona: String,
    val title: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════════
// RESPONSE MODELS
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class SendMessageResponse(
    val success: Boolean,
    val conversationId: String? = null,
    val messageId: String? = null,
    val response: String = "",
    val isCrisis: Boolean? = false,
    val metadata: MessageMetadata? = null,
    val error: String? = null
)

@Serializable
data class ChatMessage(
    val id: String,
    val content: String,
    val role: String, // "user" or "assistant"
    val timestamp: String? = null,
    val metadata: ChatMessageMetadata? = null
)

@Serializable
data class ChatMessageMetadata(
    val isCrisis: Boolean? = false,
    val tokenCount: Int? = null
)

@Serializable
data class MessageMetadata(
    val latency: Long? = null,
    val tokenCount: Int? = null,
    val crisisDetected: Boolean? = null,
    val remaining: Int? = null
)

@Serializable
data class ConversationsResponse(
    val success: Boolean,
    val conversations: List<ConversationSummary>? = null
)

@Serializable
data class ConversationSummary(
    val id: String,
    val title: String = "",
    val persona: String,
    val messageCount: Int = 0,
    val lastMessageAt: String? = null,
    val createdAt: String? = null
)

@Serializable
data class ConversationDetailResponse(
    val success: Boolean,
    val conversation: ConversationDetail? = null
)

@Serializable
data class ConversationDetail(
    val id: String,
    val title: String = "",
    val persona: String,
    val status: String = "",
    val messageCount: Int = 0,
    val totalTokens: Int = 0,
    val lastMessageAt: String? = null,
    val createdAt: String? = null,
    val messages: List<ChatMessage>? = null
)

@Serializable
data class MessagesResponse(
    val success: Boolean,
    val messages: List<ChatMessage>? = null,
    val hasMore: Boolean? = null,
    val fromCache: Boolean? = null
)

@Serializable
data class CreateConversationResponse(
    val success: Boolean,
    val conversation: ConversationSummary? = null
)

@Serializable
data class DeleteConversationResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class ActiveConversationResponse(
    val success: Boolean,
    val hasActive: Boolean = false,
    val conversation: ConversationSummary? = null
)

@Serializable
data class ChatHealthResponse(
    val status: String,
    val services: ServiceHealth? = null
)

@Serializable
data class ServiceHealth(
    val gemini: HealthStatus? = null,
    val redis: HealthStatus? = null
)

@Serializable
data class HealthStatus(
    val healthy: Boolean = false,
    val latency: Long? = null,
    val error: String? = null
)
