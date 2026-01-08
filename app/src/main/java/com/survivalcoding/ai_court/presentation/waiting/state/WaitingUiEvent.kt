package com.survivalcoding.ai_court.presentation.waiting.state

sealed interface WaitingUiEvent {
    data class NavigateToChat(val chatRoomId: Long) : WaitingUiEvent
}