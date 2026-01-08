package com.survivalcoding.ai_court.presentation.join.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import com.survivalcoding.ai_court.presentation.join.state.JoinUiEvent
import com.survivalcoding.ai_court.presentation.join.state.JoinUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<JoinUiEvent>()
    val events = _events.asSharedFlow()

    fun joinRoom(roomCode: String, nickname: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val res = roomRepository.joinRoom(roomCode, nickname)) {
                is Resource.Success -> {
                    val room = res.data
                    _events.emit(JoinUiEvent.NavigateToChat(room.roomCode, room.chatRoomId))
                }
                is Resource.Error -> {
                    val msg = res.message ?: "입장에 실패했어요."

                    if (msg.contains("이미")) {
                        _uiState.update { it.copy(blockingMessage = msg) }
                        delay(3000)
                        _uiState.update { it.copy(blockingMessage = null) }
                    } else {
                        _uiState.update { it.copy(errorMessage = msg) }
                    }
                }
                is Resource.Loading -> {
                    // repository에서 Loading을 방출하는 경우 대비
                    // 우리는 이미 isLoading을 true로 올려놨으니 여기서는 아무 것도 안 해도 됨
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}