package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomRequestDto(
    val inviteCode: String
)