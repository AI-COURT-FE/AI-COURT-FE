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

        // ADDED: 비번 빈 값 체크(지금 UI에서 비번 입력 받는 구조니까)
        if (state.password.isBlank()) { // ADDED
            _uiState.update { it.copy(errorMessage = "비밀번호를 입력해주세요") } // ADDED
            return false // ADDED
        }

        // CHANGED: password를 nickname으로 고정하지 말고 state.password 사용
        val result = authRepository.login(
            nickname = state.nickname,
            password = state.password // CHANGED
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

            if (!ensureLoginOrShowError()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // CHANGED: password 전달
            val nickname = _uiState.value.nickname
            val password = _uiState.value.password // ADDED

            when (val result = roomRepository.createRoom(nickname, password)) { // ✅ CHANGED
                is Resource.Success -> {
                    val room = result.data
                    val inviteCode = room.roomCode
                    val chatRoomId = room.hostUser.sessionId.toLong()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            createdRoomCode = inviteCode,
                            isWaitingForOpponent = true,
                            navigateToChat = NavigateToChatEvent(
                                roomCode = inviteCode,
                                chatRoomId = chatRoomId,
                                nickname = it.nickname
                                // ⚠️ NOTE: 이후 화면에서 exit 등에 password가 필요하면
                                // NavigateToChatEvent에 password도 추가로 실어야 “완전”해짐.
                            )
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }

                is Resource.Loading -> {}
            }
        }
    }

    fun joinRoom() {
        val state = _uiState.value
        val nickname = state.nickname

        val cleanCode = state.roomCode.replace("-", "").replace(" ", "")

        if (cleanCode.length != 8) {
            _uiState.update { it.copy(errorMessage = "방 코드는 8자리여야 합니다.") }
            return
        }
        val formattedRoomCode = "${cleanCode.substring(0, 4)}-${cleanCode.substring(4)}"

        if (nickname.isBlank()) {
            _uiState.update { it.copy(errorMessage = "닉네임을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            if (!ensureLoginOrShowError()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // CHANGED: password 전달
            val password = _uiState.value.password // ADDED
            when (val result = roomRepository.joinRoom(formattedRoomCode, nickname, password)) { // ✅ CHANGED
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    // 여기서 navigateToChat 세팅하지 않음 (기존 로직 유지)
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }

                is Resource.Loading -> {}
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
                    chatRoomId = 123L,
                    nickname = "djfd"
                )
            )
        }
    }
}