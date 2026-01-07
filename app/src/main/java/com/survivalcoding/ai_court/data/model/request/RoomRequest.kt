package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(
    @SerialName("host_nickname") val hostNickname: String
)

@Serializable
data class JoinRoomRequest(
    @SerialName("room_code") val roomCode: String,
    @SerialName("guest_nickname") val guestNickname: String
)
