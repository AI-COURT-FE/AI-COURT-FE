package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomResponseDto(
    val chatRoomId: Long,
    val title: String,
    val participantCode: String,
    val observerCode: String
)