package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.SendMessageRequestDto
import com.survivalcoding.ai_court.data.model.response.ChatMessageDto
import com.survivalcoding.ai_court.data.model.response.FinalJudgementResponseDto
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.ChatRoomStatus
import com.survivalcoding.ai_court.domain.model.WinRate
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : ChatRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 폴링 Job
    private var pollingJob: Job? = null

    // 마지막 메시지 ID 추적
    private var lastMessageId: Long? = null

    // State Flows
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _winRate = MutableStateFlow(WinRate(50, 50))
    private val _chatRoomStatus = MutableStateFlow(ChatRoomStatus.ALIVE)
    private val _finishRequestNickname = MutableStateFlow<String?>(null)
    private val _isConnected = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private var currentRoomCode: String? = null
    private var currentChatRoomId: Long? = null
    private var currentUserId: Long? = null
    private var currentUserNickname: String? = null

    override fun connectToRoom(roomCode: String, userId: String, myNickname: String) {
        scope.launch {
            try {
                currentRoomCode = roomCode
                currentUserId = userId.toLongOrNull()
                currentUserNickname = myNickname

                // roomCode가 chatRoomId(숫자)라고 가정
                currentChatRoomId = roomCode.toLongOrNull()
                val cleanCode = roomCode.replace("-", "").toLongOrNull()
                if (cleanCode == null) {
                    _error.value = "유효하지 않은 방 코드입니다."
                    return@launch
                }
                currentChatRoomId = cleanCode

                // 상태 초기화
                _isConnected.value = true
                _error.value = null
                _messages.value = emptyList()
                lastMessageId = null

                // 기존 폴링 Job 취소
                pollingJob?.cancel()

                // 1초 간격 폴링 시작
                pollingJob = scope.launch {
                    while (isActive) {
                        try {
                            val response = roomApiService.pollChatRoom(
                                chatRoomId = cleanCode, lastMessageId = lastMessageId
                            )

                            if (response.success) {
                                val pollData = response.result

                                // 새 메시지가 있으면 추가
                                if (pollData.messages.isNotEmpty()) {
                                    val base = lastMessageId ?: 0L

                                    val filtered = pollData.messages
                                        .filter { (it.messageId ?: 0L) > base }    // null이면 0으로
                                        .sortedBy { it.messageId ?: 0L }           // null이면 0으로 정렬

                                    if (filtered.isNotEmpty()) {
                                        val newMessages = filtered.map { dto ->
                                            dto.toDomain(myNickname = currentUserNickname.orEmpty())
                                        }
                                        _messages.value = _messages.value + newMessages

                                        // maxOf 대신 maxOrNull로 명확하게
                                        val newLast = filtered.mapNotNull { it.messageId }.maxOrNull()
                                        if (newLast != null) lastMessageId = newLast
                                    }
                                }

                                // 상태 업데이트
                                _chatRoomStatus.value =
                                    ChatRoomStatus.fromString(pollData.chatRoomStatus)
                                _finishRequestNickname.value = pollData.finishRequestNickname

                                // percent는 Map<String, Int> 타입
                                val percentMap = pollData.percent
                                if (percentMap.isNotEmpty()) {
                                    val me = currentUserNickname
                                    val myScore = if (me != null) percentMap[me] else null

                                    if (myScore != null) {
                                        val opponentKey = percentMap.keys.firstOrNull { it != me }
                                        val opponentScore = opponentKey?.let { percentMap[it] } ?: (100 - myScore)

                                        // userB = 나, userA = 상대 (너 WinRateHeader가 이렇게 쓰는지에 맞춰)
                                        _winRate.value = WinRate(
                                            userAScore = opponentScore,
                                            userBScore = myScore
                                        )
                                    } else {
                                        // 아직 상대가 없거나, 내 닉네임이 percent에 안 잡힐 때
                                        val first = percentMap.values.firstOrNull() ?: 50
                                        val second = percentMap.values.drop(1).firstOrNull() ?: (100 - first)
                                        _winRate.value = WinRate(first, second)
                                    }
                                }

                            } else {
                                _error.value = "Poll failed: ${response.message ?: "code=${response.code}"}"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // 에러가 발생해도 폴링은 계속 진행
                            _error.value = "Poll error: ${e.message}"
                        }

                        delay(1000) // 1초 대기
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
        pollingJob?.cancel()
        pollingJob = null

        _isConnected.value = false
        currentRoomCode = null
        currentChatRoomId = null
        currentUserId = null
        lastMessageId = null
    }

    override suspend fun sendMessage(content: String): Resource<Unit> {
        val chatRoomId = currentChatRoomId ?: return Resource.Error("Not connected")
        if (!_isConnected.value) return Resource.Error("Not connected to chat room")

        return try {
            val response = roomApiService.sendMessage(
                chatRoomId = chatRoomId, body = SendMessageRequestDto(content)
            )

            if (response.success) {
                // sendMessage API는 ChatMessageDto 하나만 반환함
                // 새 메시지는 폴링에서 자동으로 가져옴
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to send message: ${response.message ?: "code=${response.code}"}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun observeMessages(): Flow<List<ChatMessage>> {
        return _messages.asStateFlow()
    }

    override fun observeWinRate(): Flow<WinRate> {
        return _winRate.asStateFlow()
    }

    override fun observeChatRoomStatus(): Flow<ChatRoomStatus> {
        return _chatRoomStatus.asStateFlow()
    }

    override fun observeFinishRequestNickname(): Flow<String?> {
        return _finishRequestNickname.asStateFlow()
    }

    override suspend fun requestExit(chatRoomId: Long): Resource<Unit> {
        return try {
            val response = roomApiService.requestExit(
                chatRoomId = chatRoomId,
                user = emptyMap() // 세션 기반 인증이라면 빈 맵
            )
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message ?: "Failed to request exit")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getFinalJudgement(chatRoomId: Long): Resource<FinalJudgementResponseDto> {
        return try {
            val response = roomApiService.getFinalJudgement(chatRoomId)
            if (response.success) {
                Resource.Success(response.result)
            } else {
                Resource.Error(response.message ?: "판결문 조회 실패")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "판결문 조회 중 오류 발생")
        }
    }
    private fun ChatMessageDto.toDomain(myNickname: String): ChatMessage {
        val timestamp = runCatching {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            sdf.parse(createdAt)?.time ?: System.currentTimeMillis()
        }.getOrElse { System.currentTimeMillis() }

        val isMyMessage = (senderNickname == myNickname)

        return ChatMessage(
            id = messageId.toString(),
            roomCode = currentRoomCode.orEmpty(),
            senderId = senderNickname,          // senderId 대신 닉네임 넣기
            senderNickname = senderNickname,
            content = content,
            timestamp = timestamp,
            isMyMessage = isMyMessage
        )
    }

}
