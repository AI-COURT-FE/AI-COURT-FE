package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PollResponseDto(
    val messages: List<ChatMessageDto>,
    val chatRoomStatus: String,
    val finishRequestNickname: String? = null,
    val percent: Map<String, Int>
)
