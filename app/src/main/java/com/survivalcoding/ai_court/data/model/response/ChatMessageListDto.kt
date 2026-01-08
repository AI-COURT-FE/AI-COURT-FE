package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageListDto(
    val messageId: Long,
    val senderNickname: String,
    val content: String,
    val createdAt: String,
    val type: String
)
