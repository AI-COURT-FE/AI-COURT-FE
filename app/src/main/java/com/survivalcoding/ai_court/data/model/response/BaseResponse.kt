package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val result: T,
    val message: String? = null  // 에러 시 메시지
)
