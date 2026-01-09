package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ChatMessageDto(
    val messageId: Long? = null,

    val senderId: Long? = null,

    @JsonNames("senderNickName", "senderNickname")
    val senderNickname: String,

    val content: String,

    val createdAt: String,

    val type: String? = null  // API 스펙에 없으므로 optional
)