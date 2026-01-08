package com.survivalcoding.ai_court.presentation.entry.state

data class EntryUiState(
    val nickname: String = "",
    val password: String = "",
    val roomCode: String = "",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val createdRoomCode: String? = null,
    val isWaitingForOpponent: Boolean = false,

    val navigateToChat: NavigateToChatEvent? = null,
)

data class NavigateToChatEvent(
    val roomCode: String,
    val chatRoomId: Long,
    val userId: String,
    val nickname: String,
)