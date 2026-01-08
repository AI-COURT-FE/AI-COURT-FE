package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ExitDecisionResponseDto(
    val chatRoomId: Long,
    val approved: Boolean,
    val message: String
)