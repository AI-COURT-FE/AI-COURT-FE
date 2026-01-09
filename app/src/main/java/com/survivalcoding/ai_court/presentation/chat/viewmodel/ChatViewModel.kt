package com.survivalcoding.ai_court.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.model.ChatRoomStatus
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

        // 폴링 시작 (connectToRoom)
        chatRepository.connectToRoom(
            roomCode = roomCode,
            userId = userId,
            myNickname = myNickname
        )
        _uiState.update { it.copy(isConnected = true) }

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

        // ChatRoomStatus observe
        viewModelScope.launch {
            chatRepository.observeChatRoomStatus()
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(errorMessage = "Failed to receive chat room status: ${e.message}") }
                }
                .collect { status ->
                    _uiState.update { it.copy(chatRoomStatus = status) }

                    // DONE 상태일 때 판결문 자동 요청
                    if (status == ChatRoomStatus.DONE) {
                        loadVerdict()
                    }
                }
        }

        // FinishRequestNickname observe - 종료 승인 모달 표시 로직
        viewModelScope.launch {
            chatRepository.observeFinishRequestNickname()
                .catch { e ->
                    e.printStackTrace()
                }
                .collect { finishRequestNickname ->
                    _uiState.update { currentState ->
                        // finishRequestNickname이 있고, 내 닉네임이 아닌 경우에만 승인 모달 표시
                        val shouldShowApprovalDialog = finishRequestNickname != null &&
                                finishRequestNickname != currentState.myNickname &&
                                currentState.chatRoomStatus == ChatRoomStatus.REQUEST_FINISH

                        currentState.copy(
                            finishRequestNickname = finishRequestNickname,
                            showFinishApprovalDialog = shouldShowApprovalDialog
                        )
                    }
                }
        }
        viewModelScope.launch {
            chatRepository.observeOpponentNickname()
                .catch { it.printStackTrace() }
                .collect { opponent ->
                    if (!opponent.isNullOrBlank()) {
                        _uiState.update { s -> s.copy(opponentNickname = opponent) }
                    }
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

        // ALIVE 상태에서만 메시지 전송 가능
        if (s.chatRoomStatus != ChatRoomStatus.ALIVE) {
            _uiState.update { it.copy(errorMessage = "채팅이 종료되었거나 종료 요청 중입니다.") }
            return
        }

        // Repository를 통해 메시지 전송 (suspend 함수)
        viewModelScope.launch {
            val result = chatRepository.sendMessage(text)

            if (result is Resource.Success) {
                // 전송 성공 시 입력 필드만 초기화 (폴링에서 메시지가 추가됨)
                _uiState.update { it.copy(inputMessage = "") }
            } else if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    private fun loadVerdict() {
        val roomCode = currentRoomCode ?: return
        val chatRoomId = roomCode.replace("-", "").toLongOrNull() ?: return

        // 이미 로딩 중이거나 판결문이 있으면 스킵
        if (_uiState.value.isLoading || _uiState.value.verdict != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = chatRepository.getFinalJudgement(chatRoomId)) {
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
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
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

    fun closeFinishApprovalDialog() {
        _uiState.update { it.copy(showFinishApprovalDialog = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.disconnectFromRoom()
    }

    fun reconnect() {
        val roomCode = currentRoomCode ?: return
        val userId = currentUserId ?: return
        val nickname = _uiState.value.myNickname

        viewModelScope.launch {
            chatRepository.connectToRoom(
                roomCode = roomCode,
                userId = userId,
                myNickname = nickname
            )
            _uiState.update { it.copy(isConnected = true) }
        }
    }

    fun requestExit() {
        android.util.Log.d("VERDICT", "VM requestExit() CALLED")

        val roomCode = currentRoomCode ?: run {
            android.util.Log.d("VERDICT", "VM requestExit() STOP: currentRoomCode null")
            return
        }

        val chatRoomId = roomCode.replace("-", "").toLongOrNull() ?: run {
            android.util.Log.d("VERDICT", "VM requestExit() STOP: chatRoomId parse fail, roomCode=$roomCode")
            return
        }

        android.util.Log.d("VERDICT", "VM requestExit() chatRoomId=$chatRoomId")

        viewModelScope.launch {
            android.util.Log.d("VERDICT", "VM requestExit() launching repository...")

            val result = chatRepository.requestExit(chatRoomId)

            android.util.Log.d("VERDICT", "VM requestExit() repository result=$result")

            if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            } else {
                _uiState.update { it.copy(showVerdictDialog = false) }
            }
        }
    }

    fun approveExit() {
        val roomCode = currentRoomCode ?: return
        val chatRoomId = roomCode.replace("-", "").toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(showFinishApprovalDialog = false) }
            val result = chatRepository.approveExit(chatRoomId)
            if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun rejectExit() {
        val roomCode = currentRoomCode ?: return
        val chatRoomId = roomCode.replace("-", "").toLongOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(showFinishApprovalDialog = false) }
            val result = chatRepository.rejectExit(chatRoomId)
            if (result is Resource.Error) {
                _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

}
