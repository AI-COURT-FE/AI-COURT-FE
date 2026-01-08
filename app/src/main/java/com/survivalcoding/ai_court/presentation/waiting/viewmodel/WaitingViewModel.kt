package com.survivalcoding.ai_court.presentation.waiting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiEvent
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WaitingUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var started = false
    private var currentChatRoomId: Long = 0L

    fun start(roomCode: String, chatRoomId: Long) {
        if (started) return
        started = true
        currentChatRoomId = chatRoomId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // WebSocket에 미리 연결하여 상대방 입장을 감지
            chatRepository.connectToRoom(chatRoomId, "host")

            // 상대방 입장 감지 (메시지가 오거나 입장 알림이 오면)
            viewModelScope.launch {
                chatRepository.observeOpponentJoined()
                    .filter { it }  // true가 되면
                    .first()        // 한 번만 처리
                
                // 상대방이 입장했으므로 채팅으로 이동
                _events.tryEmit(WaitingUiEvent.NavigateToChat(chatRoomId))
            }

            // room 상태도 계속 관찰 (로컬 캐시 업데이트용)
            roomRepository.observeRoom(roomCode).collectLatest { room ->
                _uiState.update { it.copy(room = room, isLoading = false) }

                // 로컬 캐시에서 ready 상태가 되면 (이전 호환성 유지)
                if (room.isReady && room.guestUser != null) {
                    _events.tryEmit(WaitingUiEvent.NavigateToChat(room.chatRoomId))
                }
            }
        }
    }

    fun getChatRoomId(): Long = currentChatRoomId

    override fun onCleared() {
        super.onCleared()
        // WaitingViewModel이 제거될 때 WebSocket 연결 해제하지 않음
        // ChatScreen에서 재사용됨
    }
}