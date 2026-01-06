package com.survivalcoding.ai_court.data.websocket

import com.survivalcoding.ai_court.data.dto.ChatMessageDto
import com.survivalcoding.ai_court.data.dto.SendMessageRequest
import com.survivalcoding.ai_court.data.dto.WebSocketEvent
import com.survivalcoding.ai_court.data.dto.WebSocketEventType
import com.survivalcoding.ai_court.data.dto.WinRateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _messages = MutableSharedFlow<ChatMessageDto>(replay = 100)
    val messages: SharedFlow<ChatMessageDto> = _messages.asSharedFlow()

    private val _winRate = MutableStateFlow(WinRateDto(50, 50))
    val winRate: StateFlow<WinRateDto> = _winRate.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _roomReady = MutableSharedFlow<Boolean>()
    val roomReady: SharedFlow<Boolean> = _roomReady.asSharedFlow()

    private var currentRoomCode: String? = null
    private var currentUserId: String? = null

    fun connect(baseUrl: String, roomCode: String, userId: String) {
        currentRoomCode = roomCode
        currentUserId = userId

        val wsUrl = baseUrl.replace("http", "ws") + "ws/chat/$roomCode?user_id=$userId"
        val request = Request.Builder()
            .url(wsUrl)
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionState.value = ConnectionState.CONNECTED
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    handleMessage(text)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _connectionState.value = ConnectionState.DISCONNECTED
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionState.value = ConnectionState.ERROR
            }
        })
    }

    private suspend fun handleMessage(text: String) {
        try {
            val event = json.decodeFromString<WebSocketEvent>(text)
            when (event.type) {
                WebSocketEventType.MESSAGE -> {
                    val message = json.decodeFromString<ChatMessageDto>(event.payload)
                    _messages.emit(message)
                }
                WebSocketEventType.WIN_RATE -> {
                    val winRateDto = json.decodeFromString<WinRateDto>(event.payload)
                    _winRate.value = winRateDto
                }
                WebSocketEventType.ROOM_READY -> {
                    _roomReady.emit(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(content: String): Boolean {
        val roomCode = currentRoomCode ?: return false
        val userId = currentUserId ?: return false

        val request = SendMessageRequest(
            roomCode = roomCode,
            senderId = userId,
            content = content
        )

        val event = WebSocketEvent(
            type = WebSocketEventType.MESSAGE,
            payload = json.encodeToString(SendMessageRequest.serializer(), request)
        )

        return webSocket?.send(json.encodeToString(WebSocketEvent.serializer(), event)) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        currentRoomCode = null
        currentUserId = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
}

