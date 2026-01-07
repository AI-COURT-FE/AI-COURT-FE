package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    @SerialName("room_code") val roomCode: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("content") val content: String
)
