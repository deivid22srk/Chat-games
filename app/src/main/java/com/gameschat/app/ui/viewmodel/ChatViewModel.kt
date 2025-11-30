package com.gameschat.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gameschat.app.data.model.Message
import com.gameschat.app.data.repository.ChatRepository
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    init {
        loadMessages()
        subscribeToRealtimeMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _chatState.value = ChatState.Loading
            val result = repository.getMessages()
            result.onSuccess { messagesList ->
                _messages.value = messagesList
                _chatState.value = ChatState.Success
            }.onFailure { error ->
                _chatState.value = ChatState.Error(error.message ?: "Unknown error")
            }
        }
    }

    private fun subscribeToRealtimeMessages() {
        viewModelScope.launch {
            repository.subscribeChannel()
            repository.subscribeToMessages().collect { action ->
                when (action) {
                    is PostgresAction.Insert, is PostgresAction.Update, is PostgresAction.Delete -> {
                        loadMessages()
                    }
                    else -> {}
                }
            }
        }
    }

    fun sendMessage(userId: String, username: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val result = repository.sendMessage(userId, username, content)
            result.onFailure { error ->
                _chatState.value = ChatState.Error(error.message ?: "Failed to send message")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.unsubscribeChannel()
        }
    }
}

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    object Success : ChatState()
    data class Error(val message: String) : ChatState()
}
