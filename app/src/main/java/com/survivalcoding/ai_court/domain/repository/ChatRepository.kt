package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connectToRoom(chatRoomId: Long, userId: String)
    fun disconnectFromRoom()
    fun sendMessage(content: String): Resource<Unit>
    fun observeMessages(): Flow<List<ChatMessage>>
    fun observeWinRate(): Flow<WinRate>
    fun observeOpponentJoined(): Flow<Boolean>  // 상대방 입장 감지
    suspend fun requestVerdict(roomCode: String): Resource<Verdict>
}

