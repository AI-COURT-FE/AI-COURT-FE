package com.survivalcoding.ai_court.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    @SerialName("id") val id: String,
    @SerialName("room_code") val roomCode: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("sender_nickname") val senderNickname: String,
    @SerialName("content") val content: String,
    @SerialName("timestamp") val timestamp: Long
)

@Serializable
data class SendMessageRequest(
    @SerialName("room_code") val roomCode: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("content") val content: String
)

