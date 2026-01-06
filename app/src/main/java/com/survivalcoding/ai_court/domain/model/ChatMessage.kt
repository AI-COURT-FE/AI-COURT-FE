package com.survivalcoding.ai_court.domain.model

data class ChatMessage(
    val id: String,
    val roomCode: String,
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val isMyMessage: Boolean = false
)

