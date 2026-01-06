package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connectToRoom(roomCode: String, userId: String)
    fun disconnectFromRoom()
    fun sendMessage(content: String): Resource<Unit>
    fun observeMessages(): Flow<List<ChatMessage>>
    fun observeWinRate(): Flow<WinRate>
    suspend fun requestVerdict(roomCode: String): Resource<Verdict>
}

