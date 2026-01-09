package com.survivalcoding.ai_court.domain.model

enum class ChatRoomStatus {
    ALIVE,
    REQUEST_FINISH,
    REQUEST_ACCEPT,
    DONE;

    companion object {
        fun fromString(value: String): ChatRoomStatus {
            return entries.find { it.name == value } ?: ALIVE
        }
    }
}

