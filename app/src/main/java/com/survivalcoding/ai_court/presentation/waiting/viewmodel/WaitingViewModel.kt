package com.survivalcoding.ai_court.presentation.waiting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiEvent
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingViewModel @Inject constructor(
    private val roomApiService: RoomApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WaitingUiEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var pollingJob: Job? = null
    private var lastMessageId: Long? = null
    private var navigated = false

    /**
     * inviteCode: 화면에 표시/복사용 코드(참여코드)
     * chatRoomId : 폴링/채팅 API용 숫자 ID
     */
    fun startPolling(inviteCode: String, chatRoomId: Long) {
        if (pollingJob?.isActive == true) return

        _uiState.update { it.copy(inviteCode = inviteCode, isLoading = true, errorMessage = null) }
        lastMessageId = null
        navigated = false

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val res = roomApiService.pollChatRoom(
                        chatRoomId = chatRoomId,
                        lastMessageId = lastMessageId
                    )

                    if (res.success) {
                        val data = res.result

                        // lastMessageId 업데이트(중복 메시지 방지)
                        if (data.messages.isNotEmpty()) {
                            lastMessageId = data.messages.last().messageId
                        }

                        val participants = data.percent.keys.toList()
                        val count = data.percent.size

                        _uiState.update {
                            it.copy(
                                poll = data,
                                participantCount = count,
                                participants = participants,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                        // ✅ 핵심: percent에 두 명이면 호스트도 채팅방으로
                        if (!navigated && count >= 2) {
                            navigated = true
                            stopPolling()
                            _events.tryEmit(
                                WaitingUiEvent.NavigateToChat(
                                    chatRoomId = chatRoomId,
                                    inviteCode = inviteCode
                                )
                            )
                            break
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "폴링 실패(code=${res.code})"
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "폴링 중 오류"
                        )
                    }
                }

                delay(1000L)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }
}