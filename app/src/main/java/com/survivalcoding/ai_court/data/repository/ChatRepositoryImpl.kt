package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.BuildConfig
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.SendMessageRequestDto
import com.survivalcoding.ai_court.data.model.request.VerdictRequest
import com.survivalcoding.ai_court.data.model.response.ChatMessageDto
import com.survivalcoding.ai_court.data.model.response.VerdictResponse
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.Verdict
import com.survivalcoding.ai_court.domain.model.WinRate
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

// WebSocket 이벤트 구조 (내부 통신용)
@Serializable
private data class WebSocketEvent(
    val type: String,
    val payload: String
)

// WinRate 응답 (WebSocket용)
@Serializable
private data class WinRateResponse(
    val userAScore: Int,
    val userBScore: Int
) {
    fun toDomain() = WinRate(userAScore, userBScore)
}

class ChatRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val roomApiService: RoomApiService,
    private val json: Json
) : ChatRepository {

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _messages = MutableSharedFlow<ChatMessageDto>(replay = 100)
    private val _winRate = MutableStateFlow(WinRateResponse(50, 50))

    private var currentRoomCode: String? = null
    private var currentUserId: String? = null

    override fun connectToRoom(roomCode: String, userId: String) {
        currentRoomCode = roomCode
        currentUserId = userId

        val wsUrl = BuildConfig.BASE_URL.replace("http", "ws") + "ws/chat/$roomCode?user_id=$userId"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { handleMessage(text) }
            }
        })
    }

    private suspend fun handleMessage(text: String) {
        try {
            val event = json.decodeFromString<WebSocketEvent>(text)
            when (event.type) {
                "MESSAGE" -> {
                    val message = json.decodeFromString<ChatMessageDto>(event.payload)
                    _messages.emit(message)
                }
                "WIN_RATE" -> {
                    val winRate = json.decodeFromString<WinRateResponse>(event.payload)
                    _winRate.value = winRate
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun disconnectFromRoom() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    override fun sendMessage(content: String): Resource<Unit> {
        val roomCode = currentRoomCode ?: return Resource.Error("Not connected")
        val userId = currentUserId ?: return Resource.Error("Not connected")

        val request = SendMessageRequestDto(content)
        val event = WebSocketEvent(
            type = "MESSAGE",
            payload = json.encodeToString(SendMessageRequestDto.serializer(), request)
        )

        return if (webSocket?.send(
                json.encodeToString(
                    WebSocketEvent.serializer(), event
                )
            ) == true
        ) {
            Resource.Success(Unit)
        } else {
            Resource.Error("Failed to send")
        }
    }

    override fun observeMessages(): Flow<List<ChatMessage>> {
        return _messages.runningFold(emptyList()) { acc, dto ->
            acc + dto.toDomain(currentUserId ?: "")
        }
    }

    override fun observeWinRate(): Flow<WinRate> {
        return _winRate.map { it.toDomain() }
    }

    override suspend fun requestVerdict(roomCode: String): Resource<Verdict> {
        return try {
            val response = roomApiService.requestVerdict(VerdictRequest(roomCode))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private fun ChatMessageDto.toDomain(userId: String): ChatMessage {
        val timestamp = runCatching {
            val sdf = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            )
            sdf.parse(createdAt)?.time ?: System.currentTimeMillis()
        }.getOrElse {
            System.currentTimeMillis()
        }

        return ChatMessage(
            id = messageId?.toString() ?: "",
            roomCode = currentRoomCode ?: "",
            senderId = senderId?.toString() ?: "",
            senderNickname = senderNickname,
            content = content,
            timestamp = timestamp,
            isMyMessage = senderId?.toString() == userId
        )
    }

    private fun VerdictResponse.toDomain() = Verdict(
        winner = winner,
        winnerNickname = winnerNickname,
        scoreA = scoreA,
        scoreB = scoreB,
        reason = reason,
        summary = summary
    )
}