package com.example.aroura.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aroura.data.api.*
import com.example.aroura.data.local.TokenManager
import com.example.aroura.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel"

// ═════════════════════════════════════════════════════════════════════════════════
// UI MESSAGE DATA CLASS
// ═════════════════════════════════════════════════════════════════════════════════

data class UIChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isCrisisResponse: Boolean = false
)

// ═════════════════════════════════════════════════════════════════════════════════
// CHAT UI STATE
// ═════════════════════════════════════════════════════════════════════════════════

data class ChatUiState(
    val messages: List<UIChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val conversationId: String? = null,
    val persona: String = "counselor",
    val personaDisplayName: String = "AI Counselor",
    val aiTyping: Boolean = false,
    val showWelcome: Boolean = true,
    val isConnected: Boolean = true
)

// ═════════════════════════════════════════════════════════════════════════════════
// CHAT VIEW MODEL
// ═════════════════════════════════════════════════════════════════════════════════

class ChatViewModel(
    private val repository: ChatRepository,
    private val persona: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState(
        persona = persona,
        personaDisplayName = if (persona == "counselor") "AI Counselor" else "AI Best Friend"
    ))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var messageIdCounter = 0
    
    init {
        loadActiveConversation()
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // LOAD CONVERSATION
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun loadActiveConversation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = repository.getActiveConversation(persona)
            
            result.fold(
                onSuccess = { conversation ->
                    if (conversation != null) {
                        _uiState.update { 
                            it.copy(
                                conversationId = conversation.id,
                                showWelcome = false,
                                isLoading = false
                            ) 
                        }
                        loadMessages(conversation.id)
                    } else {
                        _uiState.update { it.copy(isLoading = false, showWelcome = true) }
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load active conversation", error)
                    _uiState.update { it.copy(isLoading = false, showWelcome = true) }
                }
            )
        }
    }
    
    private fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            val result = repository.getMessages(conversationId, 50)
            
            result.fold(
                onSuccess = { response ->
                    val uiMessages = response.messages?.map { msg ->
                        UIChatMessage(
                            id = msg.id,
                            content = msg.content,
                            isFromUser = msg.role == "user",
                            timestamp = parseTimestamp(msg.timestamp),
                            isCrisisResponse = msg.metadata?.isCrisis == true
                        )
                    } ?: emptyList()
                    
                    _uiState.update { 
                        it.copy(
                            messages = uiMessages.reversed(), // oldest first
                            showWelcome = uiMessages.isEmpty()
                        )
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load messages", error)
                }
            )
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // SEND MESSAGE
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun sendMessage(content: String) {
        if (content.isBlank() || _uiState.value.isSending) return
        
        viewModelScope.launch {
            // Add user message immediately
            val userMessage = UIChatMessage(
                id = generateMessageId(),
                content = content.trim(),
                isFromUser = true
            )
            
            // Add loading indicator for AI response
            val loadingMessage = UIChatMessage(
                id = generateMessageId(),
                content = "",
                isFromUser = false,
                isLoading = true
            )
            
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + userMessage + loadingMessage,
                    isSending = true,
                    aiTyping = true,
                    showWelcome = false,
                    error = null
                )
            }
            
            // Send to API
            val result = repository.sendMessage(
                message = content.trim(),
                persona = persona,
                conversationId = _uiState.value.conversationId
            )
            
            result.fold(
                onSuccess = { response ->
                    val aiMessage = UIChatMessage(
                        id = response.messageId ?: generateMessageId(),
                        content = response.response,
                        isFromUser = false,
                        isCrisisResponse = response.isCrisis == true
                    )
                    
                    _uiState.update { state ->
                        // Remove loading message and add real response
                        val messagesWithoutLoading = state.messages.dropLast(1)
                        state.copy(
                            messages = messagesWithoutLoading + aiMessage,
                            isSending = false,
                            aiTyping = false,
                            conversationId = response.conversationId
                        )
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Send message failed", error)
                    
                    val errorMessage = UIChatMessage(
                        id = generateMessageId(),
                        content = "Sorry, I couldn't send that message. ${error.message}",
                        isFromUser = false,
                        isError = true
                    )
                    
                    _uiState.update { state ->
                        val messagesWithoutLoading = state.messages.dropLast(1)
                        state.copy(
                            messages = messagesWithoutLoading + errorMessage,
                            isSending = false,
                            aiTyping = false,
                            error = error.message
                        )
                    }
                }
            )
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // CONVERSATION MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════
    
    fun startNewConversation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = repository.createConversation(persona)
            
            result.fold(
                onSuccess = { conversation ->
                    _uiState.update { 
                        it.copy(
                            messages = emptyList(),
                            conversationId = conversation.id,
                            showWelcome = true,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to create conversation", error)
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to start new conversation"
                        )
                    }
                }
            )
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun retryLastMessage() {
        val messages = _uiState.value.messages
        val lastUserMessage = messages.lastOrNull { it.isFromUser }
        
        if (lastUserMessage != null) {
            // Remove the error message
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.filter { !it.isError }
                )
            }
            sendMessage(lastUserMessage.content)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private fun generateMessageId(): String {
        return "local_${System.currentTimeMillis()}_${++messageIdCounter}"
    }
    
    private fun parseTimestamp(timestamp: String?): Long {
        return try {
            timestamp?.let {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                    .parse(it)?.time ?: System.currentTimeMillis()
            } ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// VIEW MODEL FACTORY
// ═════════════════════════════════════════════════════════════════════════════════

// TODO: Replace with Hilt/Koin DI — this manual factory should be removed once a DI framework is adopted.
class ChatViewModelFactory(
    private val context: Context,
    private val persona: String
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val tokenManager = TokenManager.getInstance(context)
            val chatApiService = ApiClient.createChatApiService(tokenManager)
            val repository = ChatRepository(chatApiService, tokenManager, context)
            
            return ChatViewModel(repository, persona) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
