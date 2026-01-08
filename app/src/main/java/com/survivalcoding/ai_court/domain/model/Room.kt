package com.survivalcoding.ai_court.domain.model

data class Room(
    val roomCode: String,        // 초대 코드 (participantCode) - 사용자에게 표시
    val chatRoomId: Long,        // 채팅방 ID - WebSocket 연결에 사용
    val hostUser: User,
    val guestUser: User? = null,
    val isReady: Boolean = false
)

