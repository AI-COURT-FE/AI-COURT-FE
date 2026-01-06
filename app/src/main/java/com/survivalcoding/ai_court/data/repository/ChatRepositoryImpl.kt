package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.BuildConfig
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApi
import com.survivalcoding.ai_court.data.dto.VerdictRequest
import com.survivalcoding.ai_court.data.mapper.toDomain
import com.survivalcoding.ai_court.data.websocket.ChatWebSocketService
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val webSocketService: ChatWebSocketService,
    private val roomApi: RoomApi
) : ChatRepository {

    private var currentUserId: String? = null

    override fun connectToRoom(roomCode: String, userId: String) {
        currentUserId = userId
        webSocketService.connect(BuildConfig.BASE_URL, roomCode, userId)
    }

    override fun disconnectFromRoom() {
        webSocketService.disconnect()
        currentUserId = null
    }

    override fun sendMessage(content: String): Resource<Unit> {
        return if (webSocketService.sendMessage(content)) {
            Resource.Success(Unit)
        } else {
            Resource.Error("Failed to send message")
        }
    }

    override fun observeMessages(): Flow<List<ChatMessage>> {
        return webSocketService.messages
            .runningFold(emptyList<ChatMessage>()) { acc, messageDto ->
                acc + messageDto.toDomain(currentUserId ?: "")
            }
    }

    override fun observeWinRate(): Flow<WinRate> {
        return webSocketService.winRate.map { it.toDomain() }
    }

    override suspend fun requestVerdict(roomCode: String): Resource<Verdict> {
        return try {
            val response = roomApi.requestVerdict(VerdictRequest(roomCode))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}

