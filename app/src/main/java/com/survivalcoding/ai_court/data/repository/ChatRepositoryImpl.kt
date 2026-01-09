package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.ExitDecisionRequestDto
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
    private val _opponentNickname = MutableStateFlow<String?>(null)

    private var currentRoomCode: String? = null
    private var currentChatRoomId: Long? = null

    private var currentUserId: String? = null
    private var currentUserNickname: String? = null


    override fun connectToRoom(roomCode: String, userId: String, myNickname: String) {
        scope.launch {
            try {
                currentRoomCode = roomCode
                currentUserId = userId
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

                                //  메시지 처리: 첫 폴링(lastMessageId == null)이면 "전체 교체", 이후에는 "append"
                                if (pollData.messages.isNotEmpty()) {
                                    // id null이면 0으로 보고 정렬/필터
                                    val sorted = pollData.messages.sortedBy { it.messageId ?: 0L }

                                    if (lastMessageId == null) {
                                        // 최초(또는 재접속): 서버가 준 전체 대화를 그대로 "교체"
                                        _messages.value = sorted.map { dto ->
                                            dto.toDomain(myNickname = currentUserNickname.orEmpty())
                                        }
                                    } else {
                                        val base = lastMessageId ?: 0L

                                        // 이후 폴링: lastMessageId 이후만 append
                                        val onlyNew = sorted
                                            .filter { (it.messageId ?: 0L) > base }
                                            .map { dto -> dto.toDomain(myNickname = currentUserNickname.orEmpty()) }

                                        if (onlyNew.isNotEmpty()) {
                                            _messages.value = _messages.value + onlyNew
                                        }
                                    }

                                    // lastMessageId는 항상 "최대"로 갱신
                                    val newLast = sorted.mapNotNull { it.messageId }.maxOrNull()
                                    if (newLast != null) lastMessageId = newLast
                                }


                                // 상태 업데이트
                                _chatRoomStatus.value =
                                    ChatRoomStatus.fromString(pollData.chatRoomStatus)
                                _finishRequestNickname.value = pollData.finishRequestNickname

                                // percent는 Map<String, Int> 타입
                                val percentMap = pollData.percent
                                if (percentMap.isNotEmpty()) {
                                    val me = currentUserNickname

                                    val opponent = percentMap.keys.firstOrNull { it != me }
                                    _opponentNickname.value = opponent

                                    val myScore = if (me != null) percentMap[me] else null

                                    if (myScore != null) {
                                        val opponentKey = percentMap.keys.firstOrNull { it != me }
                                        val opponentScore =
                                            opponentKey?.let { percentMap[it] } ?: (100 - myScore)

                                        // userB = 나, userA = 상대 (너 WinRateHeader가 이렇게 쓰는지에 맞춰)
                                        _winRate.value = WinRate(
                                            userAScore = opponentScore,
                                            userBScore = myScore
                                        )
                                    } else {
                                        // 아직 상대가 없거나, 내 닉네임이 percent에 안 잡힐 때
                                        val first = percentMap.values.firstOrNull() ?: 50
                                        val second =
                                            percentMap.values.drop(1).firstOrNull() ?: (100 - first)
                                        _winRate.value = WinRate(first, second)
                                    }
                                }

                            } else {
                                _error.value =
                                    "Poll failed: ${response.message ?: "code=${response.code}"}"
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

        // 선택
        _messages.value = emptyList()
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
                user = buildUserQuery()   // ✅ emptyMap() -> buildUserQuery()
            )
            if (response.success) Resource.Success(Unit)
            else Resource.Error(response.message ?: "Failed to request exit")
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

    override fun observeOpponentNickname(): Flow<String?> = _opponentNickname.asStateFlow()

    private fun buildUserQuery(): Map<String, String> {
        val uid = currentUserId ?: ""
        val nick = currentUserNickname ?: ""
        return mapOf(
            "userId" to uid,
            "nickname" to nick
        )
    }

    override suspend fun approveExit(chatRoomId: Long): Resource<Unit> {
        return try {
            val response = roomApiService.decideExit(
                chatRoomId = chatRoomId,
                user = buildUserQuery(),
                body = ExitDecisionRequestDto(approve = true)
            )
            if (response.success) Resource.Success(Unit)
            else Resource.Error(response.message ?: "Failed to approve exit")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun rejectExit(chatRoomId: Long): Resource<Unit> {
        return try {
            val response = roomApiService.decideExit(
                chatRoomId = chatRoomId,
                user = buildUserQuery(),
                body = ExitDecisionRequestDto(approve = false)
            )
            if (response.success) Resource.Success(Unit)
            else Resource.Error(response.message ?: "Failed to reject exit")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
