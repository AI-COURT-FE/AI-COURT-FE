package com.survivalcoding.ai_court.domain.model

data class Room(
    val roomCode: String,
    val hostUser: User,
    val guestUser: User? = null,
    val isReady: Boolean = false
)

