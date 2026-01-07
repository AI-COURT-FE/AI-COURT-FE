package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    @SerialName("id") val id: String,
    @SerialName("room_code") val roomCode: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("sender_nickname") val senderNickname: String,
    @SerialName("content") val content: String,
    @SerialName("timestamp") val timestamp: Long
)

@Serializable
data class WinRateResponse(
    @SerialName("user_a_score") val userAScore: Int,
    @SerialName("user_b_score") val userBScore: Int
)

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
