package com.survivalcoding.ai_court.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import com.survivalcoding.ai_court.presentation.chat.state.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentRoomCode: String = ""

    fun connectToRoom(roomCode: String, userId: String, nickname: String) {
        currentRoomCode = roomCode
        _uiState.update { it.copy(myNickname = nickname) }

        chatRepository.connectToRoom(roomCode, userId)

        // 메시지 관찰
        viewModelScope.launch {
            chatRepository.observeMessages().collect { messages ->
                _uiState.update { state ->
                    // 상대방 닉네임 추출
                    val opponentNickname = messages
                        .firstOrNull { !it.isMyMessage }
                        ?.senderNickname ?: "상대방"

                    state.copy(
                        messages = messages,
                        isConnected = true,
                        opponentNickname = opponentNickname
                    )
                }
            }
        }

        // 승률 관찰
        viewModelScope.launch {
            chatRepository.observeWinRate().collect { winRate ->
                _uiState.update { it.copy(winRate = winRate) }
            }
        }
    }

    fun onInputMessageChanged(message: String) {
        _uiState.update { it.copy(inputMessage = message) }
    }

    fun sendMessage() {
        val content = _uiState.value.inputMessage.trim()
        if (content.isBlank()) return

        when (chatRepository.sendMessage(content)) {
            is Resource.Success -> {
                _uiState.update { it.copy(inputMessage = "") }
            }
            is Resource.Error -> {
                _uiState.update { it.copy(errorMessage = "메시지 전송 실패") }
            }
            is Resource.Loading -> { /* ignore */ }
        }
    }

    fun requestVerdict() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = chatRepository.requestVerdict(currentRoomCode)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            verdict = result.data,
                            showVerdictDialog = true
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> { /* handled by isLoading */ }
            }
        }
    }

    fun dismissVerdictDialog() {
        _uiState.update { it.copy(showVerdictDialog = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnectFromRoom()
    }
}

