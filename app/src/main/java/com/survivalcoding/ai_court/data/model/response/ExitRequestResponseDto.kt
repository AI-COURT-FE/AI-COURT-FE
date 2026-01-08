package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ExitRequestResponseDto(
    val chatRoomId: Long,
    val requesterNickname: String,
    val message: String
)