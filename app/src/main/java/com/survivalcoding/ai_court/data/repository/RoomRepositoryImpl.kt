package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.ExitDecisionRequestDto
import com.survivalcoding.ai_court.data.model.request.JoinChatRoomRequestDto
import com.survivalcoding.ai_court.data.model.request.LoginRequestDto
import com.survivalcoding.ai_court.data.model.response.CreateChatRoomResponseDto
import com.survivalcoding.ai_court.data.model.response.ExitDecisionResponseDto
import com.survivalcoding.ai_court.data.model.response.ExitRequestResponseDto
import com.survivalcoding.ai_court.data.model.response.JoinChatRoomResponseDto
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User as DomainUser
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService,
    private val json: Json
) : RoomRepository {

    private fun normalizeInviteCode(code: String): String {
        val digits = code.filter { it.isDigit() }
        return if (digits.length == 8) {
            digits.substring(0, 4) + "-" + digits.substring(4, 8)
        } else {
            code.trim()
        }
    }

    // 로컬 캐시 (getRoom API가 없으므로 생성/입장 시 저장)
    private val _currentRoom = MutableStateFlow<Room?>(null)

    // ✅ CHANGED: nickname만으로 로그인 캐시하면 password가 달라질 때 문제가 생김
    private var loggedInKey: String? = null // ✅ ADDED

    // ✅ CHANGED: (nickname, password)로 로그인 보장
    private suspend fun ensureLoggedIn(nickname: String, password: String): Resource<Unit> { // ✅ CHANGED
        val key = "$nickname:$password" // ✅ ADDED
        if (loggedInKey == key) return Resource.Success(Unit) // ✅ CHANGED

        return try {
            // ✅ CHANGED: password를 nickname으로 고정하지 말고 전달받은 password 사용
            val body = roomApiService.login(LoginRequestDto(nickname, password)) // ✅ CHANGED
            if (!body.success) {
                Resource.Error(body.result, body.code)
            } else {
                loggedInKey = key // ✅ CHANGED
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "로그인 실패")
        }
    }

    // ✅ CHANGED: createRoom에 password 추가(인터페이스 기본값 덕에 기존 호출도 유지됨)
    override suspend fun createRoom(hostNickname: String, hostPassword: String): Resource<Room> { // ✅ CHANGED
        val login = ensureLoggedIn(hostNickname, hostPassword) // ✅ CHANGED
        if (login is Resource.Error) return login

        return try {
            val response = roomApiService.createChatRoom()
            if (response.success) {
                val dto = json.decodeFromJsonElement(CreateChatRoomResponseDto.serializer(), response.result)
                val room = dto.toDomain(hostNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                val msg = response.result.jsonPrimitive.content
                Resource.Error(msg, response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    // ✅ CHANGED: joinRoom에 password 추가(인터페이스 기본값 덕에 기존 호출도 유지됨)
    override suspend fun joinRoom(
        roomCode: String,
        guestNickname: String,
        guestPassword: String
    ): Resource<Room> { // ✅ CHANGED
        val login = ensureLoggedIn(guestNickname, guestPassword) // ✅ CHANGED
        if (login is Resource.Error) return login

        return try {
            val inviteCode = normalizeInviteCode(roomCode)

            val request = JoinChatRoomRequestDto(inviteCode = inviteCode)
            val response = roomApiService.joinChatRoom(request)

            if (response.success) {
                val dto = json.decodeFromJsonElement(JoinChatRoomResponseDto.serializer(), response.result)
                val room = dto.toDomain(guestNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                val msg = response.result.jsonPrimitive.content
                Resource.Error(msg, response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun observeRoom(roomCode: String): Flow<Room> {
        return _currentRoom.filterNotNull()
    }

    private fun CreateChatRoomResponseDto.toDomain(hostNickname: String): Room {
        return Room(
            roomCode = participantCode,
            hostUser = DomainUser(
                sessionId = chatRoomId.toString(),
                nickname = hostNickname
            ),
            guestUser = null,
            isReady = false
        )
    }

    private fun JoinChatRoomResponseDto.toDomain(guestNickname: String): Room {
        return Room(
            roomCode = chatRoomId.toString(),
            hostUser = DomainUser(
                sessionId = "",
                nickname = ""
            ),
            guestUser = DomainUser(
                sessionId = chatRoomId.toString(),
                nickname = guestNickname
            ),
            isReady = true
        )
    }

    // ✅ CHANGED: password 파라미터 추가(기본값은 인터페이스에 있어서 기존 호출 유지됨)
    override suspend fun requestExit(
        chatRoomId: Long,
        user: DomainUser,
        password: String
    ): ExitRequestResponseDto { // ✅ CHANGED
        val login = ensureLoggedIn(user.nickname, password) // ✅ CHANGED
        if (login is Resource.Error) throw IllegalStateException(login.message ?: "로그인 실패")

        val response = roomApiService.requestExit(
            chatRoomId = chatRoomId,
            user = user.toExitQueryMap(password) // ✅ CHANGED
        )
        return response.result
    }

    // ✅ CHANGED: password 파라미터 추가(기본값은 인터페이스에 있어서 기존 호출 유지됨)
    override suspend fun decideExit(
        chatRoomId: Long,
        user: DomainUser,
        approve: Boolean,
        password: String
    ): ExitDecisionResponseDto { // ✅ CHANGED
        val login = ensureLoggedIn(user.nickname, password) // ✅ CHANGED
        if (login is Resource.Error) throw IllegalStateException(login.message ?: "로그인 실패")

        val response = roomApiService.decideExit(
            chatRoomId = chatRoomId,
            user = user.toExitQueryMap(password), // ✅ CHANGED
            body = ExitDecisionRequestDto(approve = approve)
        )
        return response.result
    }

    // ✅ CHANGED: password를 인자로 받아서 query에 넣기
    private fun DomainUser.toExitQueryMap(password: String): Map<String, String> { // ✅ CHANGED
        val idLong = sessionId.toLongOrNull() ?: 0L

        return buildMap {
            put("id", idLong.toString())
            put("nickname", nickname)
            put("password", password) // ✅ CHANGED (기존 nickname 고정 제거)
        }
    }
}