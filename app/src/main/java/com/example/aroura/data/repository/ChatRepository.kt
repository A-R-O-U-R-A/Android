package com.example.aroura.data.repository

import android.content.Context
import android.util.Log
import com.example.aroura.data.api.*
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ChatRepository"

/**
 * Chat Repository
 * 
 * Manages chat data operations between API and local storage
 */
class ChatRepository(
    private val chatApiService: ChatApiService,
    private val tokenManager: TokenManager,
    private val context: Context
) {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SEND MESSAGE
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Send a message to the AI and get response
     */
    suspend fun sendMessage(
        message: String,
        persona: String,
        conversationId: String? = null
    ): Result<SendMessageResponse> = withContext(Dispatchers.IO) {
        try {
            // Debug: Check if token exists
            val token = tokenManager.getAccessTokenSync()
            Log.d(TAG, "Sending message to $persona, token exists: ${token != null}, token length: ${token?.length ?: 0}")
            
            if (token == null) {
                Log.e(TAG, "No access token found! User needs to login.")
                return@withContext Result.failure(Exception("Please login to continue"))
            }
            
            val request = SendMessageRequest(
                message = message,
                persona = persona,
                conversationId = conversationId
            )
            
            Log.d(TAG, "Making API call to chat/message...")
            val response = chatApiService.sendMessage(request)
            Log.d(TAG, "API response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Log.d(TAG, "Message sent successfully to $persona, response: ${body.response?.take(50)}...")
                    Result.success(body)
                } else {
                    Log.e(TAG, "API returned error: ${body.error}")
                    Result.failure(Exception(body.error ?: "Failed to send message"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Send message failed: ${response.code()} - $errorBody")
                
                when (response.code()) {
                    401 -> Result.failure(Exception("Session expired. Please login again."))
                    429 -> Result.failure(Exception("Too many messages. Please wait a moment."))
                    else -> Result.failure(Exception("Failed to send message: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Send message exception", e)
            Result.failure(e)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // CONVERSATIONS
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Get user's conversations
     */
    suspend fun getConversations(
        persona: String? = null,
        limit: Int = 20
    ): Result<List<ConversationSummary>> = withContext(Dispatchers.IO) {
        try {
            val response = chatApiService.getConversations(persona, limit)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body.conversations ?: emptyList())
                } else {
                    Result.failure(Exception("Failed to fetch conversations"))
                }
            } else {
                Result.failure(Exception("Failed to fetch conversations: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get conversations exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get specific conversation with all messages
     */
    suspend fun getConversation(conversationId: String): Result<ConversationDetail> = 
        withContext(Dispatchers.IO) {
            try {
                val response = chatApiService.getConversation(conversationId)
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success && body.conversation != null) {
                        Result.success(body.conversation)
                    } else {
                        Result.failure(Exception("Conversation not found"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch conversation: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get conversation exception", e)
                Result.failure(e)
            }
        }
    
    /**
     * Get messages with pagination
     */
    suspend fun getMessages(
        conversationId: String,
        limit: Int = 50,
        beforeMessageId: String? = null
    ): Result<MessagesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApiService.getMessages(conversationId, limit, beforeMessageId)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Failed to fetch messages"))
                }
            } else {
                Result.failure(Exception("Failed to fetch messages: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get messages exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Create a new conversation
     */
    suspend fun createConversation(
        persona: String,
        title: String? = null
    ): Result<ConversationSummary> = withContext(Dispatchers.IO) {
        try {
            val request = CreateConversationRequest(persona, title)
            val response = chatApiService.createConversation(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.conversation != null) {
                    Log.d(TAG, "Created new conversation for $persona")
                    Result.success(body.conversation)
                } else {
                    Result.failure(Exception("Failed to create conversation"))
                }
            } else {
                Result.failure(Exception("Failed to create conversation: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create conversation exception", e)
            Result.failure(e)
        }
    }
    
    /**
     * Delete/archive a conversation
     */
    suspend fun deleteConversation(conversationId: String): Result<Boolean> = 
        withContext(Dispatchers.IO) {
            try {
                val response = chatApiService.deleteConversation(conversationId)
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Log.d(TAG, "Conversation deleted: $conversationId")
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Failed to delete conversation"))
                    }
                } else {
                    Result.failure(Exception("Failed to delete conversation: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Delete conversation exception", e)
                Result.failure(e)
            }
        }
    
    /**
     * Get active conversation for a persona
     */
    suspend fun getActiveConversation(persona: String): Result<ConversationSummary?> = 
        withContext(Dispatchers.IO) {
            try {
                val response = chatApiService.getActiveConversation(persona)
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Result.success(if (body.hasActive) body.conversation else null)
                    } else {
                        Result.failure(Exception("Failed to fetch active conversation"))
                    }
                } else {
                    Result.failure(Exception("Failed to fetch active conversation: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get active conversation exception", e)
                Result.failure(e)
            }
        }
    
    /**
     * Check chat service health
     */
    suspend fun healthCheck(): Result<ChatHealthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = chatApiService.healthCheck()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Health check failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Health check exception", e)
            Result.failure(e)
        }
    }
}
