package com.survivalcoding.ai_court.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val createdAt: String? = null,
    val modifiedAt: String? = null,
    val id: Long,
    val nickname: String? = null,
    val password: String? = null
)