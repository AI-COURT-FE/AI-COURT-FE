package com.survivalcoding.ai_court.presentation.waiting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaitingViewModel(
    // TODO: 실제 데이터 가져오는 useCase/repository 주입
    // private val roomRepository: RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingUiState(isLoading = true))
    val uiState: StateFlow<WaitingUiState> = _uiState.asStateFlow()

    fun loadRoom(roomCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                // TODO: 서버에서 room 가져오기
                // roomRepository.getRoom(roomCode)
                // 임시: 더미
                Room(
                    roomCode = roomCode,
                    hostUser = User(
                        sessionId = "local_host",
                        nickname = "host"), // TODO: 네 프로젝트 User 생성자에 맞게 수정
                    guestUser = null,
                    isReady = false
                )
            }.onSuccess { room ->
                _uiState.update { it.copy(room = room, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun setRoom(room: Room) {
        _uiState.update { it.copy(room = room, isLoading = false, errorMessage = null) }
    }

    // 예: 상대 들어와서 ready되면 채팅으로 넘어갈 때 쓸 수도 있음
    fun markReady() {
        _uiState.update { state ->
            state.copy(room = state.room?.copy(isReady = true))
        }
    }
}
