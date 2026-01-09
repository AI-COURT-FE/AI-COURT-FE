package com.survivalcoding.ai_court.presentation.waiting.state

import com.survivalcoding.ai_court.data.model.response.PollResponseDto

data class WaitingUiState(
    val inviteCode: String = "",

    // 폴링 결과(원하면 UI에서 messages/percent 그대로 쓰기 가능)
    val poll: PollResponseDto? = null,

    // 편의값
    val participantCount: Int = 1,
    val participants: List<String> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
