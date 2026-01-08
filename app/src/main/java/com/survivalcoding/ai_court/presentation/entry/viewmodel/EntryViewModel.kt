package com.survivalcoding.ai_court.presentation.entry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.repository.AuthRepository
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import com.survivalcoding.ai_court.presentation.entry.state.EntryUiState
import com.survivalcoding.ai_court.presentation.entry.state.NavigateToChatEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    private val userId: String = UUID.randomUUID().toString()

    fun onNicknameChanged(nickname: String) {
        _uiState.update { it.copy(nickname = nickname, errorMessage = null) }
    }

    fun onRoomCodeChanged(roomCode: String) {
        _uiState.update { it.copy(roomCode = roomCode, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    private suspend fun ensureLoginOrShowError(): Boolean {
        val state = _uiState.value
        if (state.isLoggedIn) return true

        if (state.nickname.isBlank()) {
            _uiState.update { it.copy(errorMessage = "닉네임을 입력해주세요") }
            return false
        }

        // 비번 입력 X nickname을 그대로 password로 넣기
        val result = authRepository.login(
            nickname = state.nickname,
            password = state.nickname
        )

        return result.fold(
            onSuccess = {
                _uiState.update { it.copy(isLoggedIn = true) }
                true
            },
            onFailure = { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "로그인 실패") }
                false
            }
        )
    }


    fun createRoom() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1) 로그인 먼저
            if (!ensureLoginOrShowError()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // 2) 로그인 성공 후 기존 로직 그대로
            when (val result = roomRepository.createRoom(_uiState.value.nickname)) {
                is Resource.Success -> {
                    val room = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            createdRoomCode = room.roomCode,
                            isWaitingForOpponent = true
                        )
                    }
                    observeRoom(room.roomCode)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun observeRoom(roomCode: String) {
        viewModelScope.launch {
            roomRepository.observeRoom(roomCode).collect { room ->
                if (room.isReady && room.guestUser != null) {
                    _uiState.update {
                        it.copy(
                            isWaitingForOpponent = false,
                            navigateToChat = NavigateToChatEvent(
                                roomCode = roomCode,
                                userId = userId,
                                nickname = _uiState.value.nickname
                            )
                        )
                    }
                }
            }
        }
    }

    fun joinRoom() {
        val nickname = _uiState.value.nickname
        val roomCode = _uiState.value.roomCode

        if (nickname.isBlank()) {
            _uiState.update { it.copy(errorMessage = "닉네임을 입력해주세요") }
            return
        }
        if (roomCode.isBlank()) {
            _uiState.update { it.copy(errorMessage = "방 코드를 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = roomRepository.joinRoom(roomCode, nickname)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            navigateToChat = NavigateToChatEvent(
                                roomCode = roomCode,
                                userId = userId,
                                nickname = nickname
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is Resource.Loading -> { /* handled by isLoading state */ }
            }
        }
    }
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToChat = null) }
    }

    fun cancelWaiting() {
        _uiState.update {
            it.copy(
                isWaitingForOpponent = false,
                createdRoomCode = null
            )
        }
    }
    fun debugEnterRoom() {
        _uiState.update {
            it.copy(
                navigateToChat = NavigateToChatEvent(
                    roomCode = "TEST123",
                    userId = "testUser",
                    nickname = "테스터"
                )
            )
        }
    }
}

