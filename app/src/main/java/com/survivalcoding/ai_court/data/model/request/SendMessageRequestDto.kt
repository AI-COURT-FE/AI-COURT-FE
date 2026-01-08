package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequestDto(
    val content: String
)
