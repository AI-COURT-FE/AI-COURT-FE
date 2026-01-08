package com.survivalcoding.ai_court.presentation.waiting.state

sealed interface WaitingUiEvent {
    data object NavigateToChat : WaitingUiEvent
}