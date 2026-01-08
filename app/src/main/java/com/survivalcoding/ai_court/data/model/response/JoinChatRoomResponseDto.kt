package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomResponseDto(
    val chatRoomId: Long,
    val title: String,
    val role: String,
    val message: String
)