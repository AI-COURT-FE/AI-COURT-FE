package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerdictRequest(
    @SerialName("room_code") val roomCode: String
)
