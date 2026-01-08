package com.survivalcoding.ai_court.presentation.join.state

data class JoinUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,     // 3) 잘못된 방코드 오류 메시지
    val blockingMessage: String? = null   // 5) 이미 상대 있음 메시지(3초)
)