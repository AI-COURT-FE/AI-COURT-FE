package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val code: Int,
    val result: T
)

typealias BaseResponseString = BaseResponse<String>