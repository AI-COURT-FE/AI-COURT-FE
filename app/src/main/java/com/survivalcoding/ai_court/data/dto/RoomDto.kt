package com.survivalcoding.ai_court.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    @SerialName("room_code") val roomCode: String,
    @SerialName("host_id") val hostId: String,
    @SerialName("host_nickname") val hostNickname: String,
    @SerialName("guest_id") val guestId: String? = null,
    @SerialName("guest_nickname") val guestNickname: String? = null,
    @SerialName("is_ready") val isReady: Boolean = false
)

@Serializable
data class CreateRoomRequest(
    @SerialName("host_nickname") val hostNickname: String
)

@Serializable
data class JoinRoomRequest(
    @SerialName("room_code") val roomCode: String,
    @SerialName("guest_nickname") val guestNickname: String
)

