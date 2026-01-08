package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseErrorResponse(
    val success: Boolean,
    val code: Int,
    val result: String
)