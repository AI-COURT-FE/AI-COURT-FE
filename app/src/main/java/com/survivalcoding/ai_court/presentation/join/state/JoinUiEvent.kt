package com.survivalcoding.ai_court.presentation.join.state

sealed interface JoinUiEvent {
    data class NavigateToChat(val roomCode: String, val chatRoomId: Long) : JoinUiEvent
}