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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@kotlinx.serialization.Serializable
private data class WinRateResponse(
    val userAScore: Int,
    val userBScore: Int
) {
    fun toDomain() = WinRate(userAScore, userBScore)
}

class ChatRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService,
    private val json: Json
) : ChatRepository {

    private val stompClient = StompClient(OkHttpWebSocketClient())
    private var stompSession: StompSession? = null

    // ✅ 구독은 Subscription 객체가 아니라 "collect Job"으로 관리
    private var messageJob: Job? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _messages = MutableSharedFlow<ChatMessageDto>(replay = 100)
    private val _winRate = MutableStateFlow(WinRateResponse(50, 50))
    private val _isConnected = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private var currentRoomCode: String? = null
    private var currentChatRoomId: Long? = null
    private var currentUserId: Long? = null

    override fun connectToRoom(roomCode: String, userId: String) {
        scope.launch {
            try {
                currentRoomCode = roomCode
                currentUserId = userId.toLongOrNull()

                // ⚠️ 지금은 roomCode가 chatRoomId(숫자)라고 가정
                currentChatRoomId = roomCode.toLongOrNull()
                val chatRoomId = currentChatRoomId
                if (chatRoomId == null) {
                    _error.value = "Invalid room code: $roomCode (must be numeric)"
                    return@launch
                }

                _isConnected.value = false
                _error.value = null

                // ✅ BASE_URL -> ws/wss 변환 + 문서대로 /ws
                val httpBase = BuildConfig.BASE_URL.trimEnd('/')
                val wsBase = httpBase
                    .replaceFirst("https://", "wss://")
                    .replaceFirst("http://", "ws://")
                val wsUrl = "$wsBase/ws"

                // ✅ STOMP 연결
                stompSession = stompClient.connect(wsUrl)
                _isConnected.value = true

                // ✅ 기존 구독 정리
                messageJob?.cancel()
                messageJob = null

                // ✅ 메시지 구독(Flow collect)
                val headers = StompSubscribeHeaders(destination = "/topic/chatroom/$chatRoomId")
                val session = stompSession ?: throw IllegalStateException("No STOMP session")

                messageJob = scope.launch {
                    session
                        .subscribe(headers) // Flow<StompFrame.Message>
                        .collect { frame: StompFrame.Message ->
                            try {
                                val text = (frame.body as? FrameBody.Text)?.text ?: return@collect
                                val dto = json.decodeFromString<ChatMessageDto>(text)
                                _messages.emit(dto)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                _error.value = "Failed to parse message: ${e.message}"
                            }
                        }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Connection failed: ${e.message}"
                _isConnected.value = false
            }
        }
    }

    override fun disconnectFromRoom() {
        scope.launch {
            try {
                messageJob?.cancel()
                messageJob = null

                stompSession?.disconnect()
                stompSession = null

                _isConnected.value = false
                currentRoomCode = null
                currentChatRoomId = null
                currentUserId = null
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Disconnect error: ${e.message}"
            }
        }
    }

    override fun sendMessage(content: String): Resource<Unit> {
        val chatRoomId = currentChatRoomId ?: return Resource.Error("Not connected")
        if (!_isConnected.value) return Resource.Error("Not connected to WebSocket")

        val requestJson = json.encodeToString(
            SendMessageRequestDto.serializer(),
            SendMessageRequestDto(content)
        )

        scope.launch {
            try {
                val session = stompSession ?: throw IllegalStateException("No STOMP session")
                val headers = StompSendHeaders(destination = "/app/chatroom/$chatRoomId")
                session.send(headers, FrameBody.Text(requestJson))
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to send message: ${e.message}"
            }
        }

        return Resource.Success(Unit)
    }

    override fun observeMessages(): Flow<List<ChatMessage>> {
        return _messages.runningFold(emptyList()) { acc, dto ->
            acc + dto.toDomain(currentUserId?.toString().orEmpty())
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
        // createdAt이 "2026-01-08T12:00:00"처럼 오프셋 없을 수 있어 LocalDateTime 파싱
        val timestamp = runCatching {
            val ldt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrElse { System.currentTimeMillis() }

        val isMyMessage = senderId?.toString() == userId

        return ChatMessage(
            id = messageId?.toString() ?: "",
            roomCode = currentRoomCode.orEmpty(),
            senderId = senderId?.toString().orEmpty(),
            senderNickname = senderNickname,
            content = content,
            timestamp = timestamp,
            isMyMessage = isMyMessage
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
