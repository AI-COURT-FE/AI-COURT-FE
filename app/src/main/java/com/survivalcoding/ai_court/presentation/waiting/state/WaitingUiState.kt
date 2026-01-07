package com.survivalcoding.ai_court.presentation.waiting.state

import com.survivalcoding.ai_court.domain.model.Room

data class WaitingUiState(
    val room: Room? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
