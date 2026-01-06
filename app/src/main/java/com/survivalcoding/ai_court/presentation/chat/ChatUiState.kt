package com.survivalcoding.ai_court.presentation.chat

import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val winRate: WinRate = WinRate(50, 50),
    val inputMessage: String = "",
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val verdict: Verdict? = null,
    val showVerdictDialog: Boolean = false,
    val opponentNickname: String = "상대방",
    val myNickname: String = ""
)

