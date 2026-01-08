package com.survivalcoding.ai_court.presentation.waiting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiEvent
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WaitingUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var started = false

    fun start(roomCode: String) {
        if (started) return
        started = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // room 상태를 계속 관찰 (나중에 WebSocket 붙으면 여기서 바로 반응)
            roomRepository.observeRoom(roomCode).collectLatest { room ->
                _uiState.update { it.copy(room = room, isLoading = false) }

                // 상대방 들어와서 ready 되면 Chat으로 이동 이벤트
                if (room.isReady && room.guestUser != null) {
                    _events.tryEmit(WaitingUiEvent.NavigateToChat)
                }
            }
        }
    }
}