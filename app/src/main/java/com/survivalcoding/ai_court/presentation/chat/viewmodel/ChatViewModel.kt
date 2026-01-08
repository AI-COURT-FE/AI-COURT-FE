package com.survivalcoding.ai_court.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import com.survivalcoding.ai_court.presentation.chat.state.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentRoomCode: String? = null
    private var currentUserId: String? = null

    fun initialize(roomCode: String, userId: String, myNickname: String, opponentNickname: String) {
        currentRoomCode = roomCode
        currentUserId = userId

        _uiState.update {
            it.copy(
                myNickname = myNickname,
                opponentNickname = opponentNickname,
                isConnected = false
            )
        }

        // WebSocket 연결
        chatRepository.connectToRoom(roomCode, userId)

        // 메시지 observe
        viewModelScope.launch {
            chatRepository.observeMessages()
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(errorMessage = "Failed to receive messages: ${e.message}") }
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }

        // WinRate observe
        viewModelScope.launch {
            chatRepository.observeWinRate()
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(errorMessage = "Failed to receive win rate: ${e.message}") }
                }
                .collect { winRate ->
                    _uiState.update { it.copy(winRate = winRate) }
                }
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputMessage = text) }
    }

    fun onSendClick() {
        val s = _uiState.value
        val text = s.inputMessage.trim()
        if (text.isBlank()) return

        val roomCode = currentRoomCode ?: return
        val userId = currentUserId ?: return

        // Repository를 통해 메시지 전송
        val result = chatRepository.sendMessage(text)
        
        if (result is com.survivalcoding.ai_court.core.util.Resource.Success) {
            // 전송 성공 시 입력 필드만 초기화 (서버에서 응답이 오면 메시지가 추가됨)
            _uiState.update { it.copy(inputMessage = "") }
        } else if (result is com.survivalcoding.ai_court.core.util.Resource.Error) {
            _uiState.update { it.copy(errorMessage = result.message) }
        }
    }

    fun setMyNickname(nickname: String) {
        _uiState.update { it.copy(myNickname = nickname) }
    }

    fun setOpponentNickname(nickname: String) {
        _uiState.update { it.copy(opponentNickname = nickname) }
    }

    fun openVerdictDialog() {
        _uiState.update { it.copy(showVerdictDialog = true) }
    }

    fun closeVerdictDialog() {
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
