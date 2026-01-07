package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomResponse(
    @SerialName("room_code") val roomCode: String,
    @SerialName("host_id") val hostId: String,
    @SerialName("host_nickname") val hostNickname: String,
    @SerialName("guest_id") val guestId: String? = null,
    @SerialName("guest_nickname") val guestNickname: String? = null,
    @SerialName("is_ready") val isReady: Boolean = false
)
