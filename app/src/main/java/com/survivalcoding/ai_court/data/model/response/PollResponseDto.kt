package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable


//TODO: 이거 확정 아님 
@Serializable
data class PollResponseDto(
    val messages: List<ChatMessageDto>,
    val chatRoomStatus: String,
    val finishRequestNickname: String? = null,
    val percent: PercentDto
)

@Serializable
data class PercentDto(
    val A: Int,
    val B: Int
)
