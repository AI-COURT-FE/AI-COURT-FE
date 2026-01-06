package com.survivalcoding.ai_court.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketEvent(
    @SerialName("type") val type: String,
    @SerialName("payload") val payload: String
)

object WebSocketEventType {
    const val MESSAGE = "message"
    const val WIN_RATE = "win_rate"
    const val USER_JOINED = "user_joined"
    const val USER_LEFT = "user_left"
    const val ROOM_READY = "room_ready"
}

