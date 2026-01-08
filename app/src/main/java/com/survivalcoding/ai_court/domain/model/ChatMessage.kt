package com.survivalcoding.ai_court.domain.model

data class ChatMessage(
    val id: String,
    val roomCode: String,
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val isMyMessage: Boolean = false

    /*
    roomCode = chatRoomId.toString()
    id = messageId.toString()
    senderId = senderId.toString()
    timestamp = createdAt 파싱해서 epochMillis
    isMyMessage는 서버가 userId를 안 주면 → 내 nickname이랑 비교하는 게 현실적 (웹소켓 문서도 senderId 비교를 말하지만, 로그인 응답이 userId를 안 줌)
     */
)

