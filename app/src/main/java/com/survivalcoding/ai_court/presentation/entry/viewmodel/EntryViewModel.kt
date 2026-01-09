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
                            isWaitingForOpponent = true,

                            // EntryScreen이 이 이벤트를 받으면 Waiting으로 이동함
                            navigateToChat = NavigateToChatEvent(
                                roomCode = room.roomCode,
                                userId = userId,
                                nickname = it.nickname
                            )
                        )
                    }

                    // Entry 화면은 Waiting으로 이동하면서 popUpTo로 사라짐 → 여기서 observeRoom 돌려도 의미 없음
                    // observeRoom(room.roomCode)
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
        val state = _uiState.value
        val nickname = state.nickname

        // 1. 모든 공백과 하이픈을 제거하여 순수 데이터만 추출
        val cleanCode = state.roomCode.replace("-", "").replace(" ", "")

        // 2. 8자리 검증 및 하이픈 재조합 (서버 포맷: XXXX-XXXX)
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

            // 3. 로그인 체크 (비밀번호 규칙을 닉네임과 동일하게 통일)
            if (!ensureLoginOrShowError()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            // 4. 정제된 formattedRoomCode 전달
            when (val result = roomRepository.joinRoom(formattedRoomCode, nickname)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            navigateToChat = NavigateToChatEvent(
                                roomCode = formattedRoomCode,
                                userId = userId,
                                nickname = nickname
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

