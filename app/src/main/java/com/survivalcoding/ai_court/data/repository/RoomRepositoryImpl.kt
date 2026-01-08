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

    // 로컬 캐시 (getRoom API가 없으므로 생성/입장 시 저장)
    private val _currentRoom = MutableStateFlow<Room?>(null)

    private var loggedInNickname: String? = null

    private suspend fun ensureLoggedIn(nickname: String): Resource<Unit> {
        // 같은 닉네임이면 이미 로그인했다고 가정(세션 유지)
        if (loggedInNickname == nickname) return Resource.Success(Unit)

        return try {
            // 닉네임/비번을 동일하게 쓰는 현재 프로젝트 방식(로그에 이렇게 찍힘)
            val body = roomApiService.login(LoginRequestDto(nickname, nickname))
            if (!body.success) {
                Resource.Error(body.result, body.code)
            } else {
                loggedInNickname = nickname
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "로그인 실패")
        }
    }

    override suspend fun createRoom(hostNickname: String): Resource<Room> {
        val login = ensureLoggedIn(hostNickname)
        if (login is Resource.Error) return login

        return try {
            val response = roomApiService.createChatRoom()
            if (response.success) {
                val dto = json.decodeFromJsonElement(CreateChatRoomResponseDto.serializer(), response.result)
                val room = dto.toDomain(hostNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                // 실패 result가 문자열이어도 안전
                val msg = response.result.jsonPrimitive.content
                Resource.Error(msg, response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun joinRoom(roomCode: String, guestNickname: String): Resource<Room> {
        val login = ensureLoggedIn(guestNickname)
        if (login is Resource.Error) return login

        return try {
            val request = JoinChatRoomRequestDto(inviteCode = roomCode)
            val response = roomApiService.joinChatRoom(request)

            if (response.success) {
                val dto = json.decodeFromJsonElement(JoinChatRoomResponseDto.serializer(), response.result)
                val room = dto.toDomain(guestNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                // ✅ “User not logged in”, “이미 입장한 채팅방”, “잘못된 코드” 전부 여기로 안전하게 옴
                val msg = response.result.jsonPrimitive.content
                Resource.Error(msg, response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun observeRoom(roomCode: String): Flow<Room> {
        // getRoom API가 없으므로 로컬 캐시된 Room을 반환
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

    override suspend fun requestExit(
        chatRoomId: Long,
        user: DomainUser
    ): ExitRequestResponseDto {
        val login = ensureLoggedIn(user.nickname)
        if (login is Resource.Error) throw IllegalStateException(login.message ?: "로그인 실패")

        val response = roomApiService.requestExit(
            chatRoomId = chatRoomId,
            user = user.toExitQueryMap()
        )
        return response.result
    }

    override suspend fun decideExit(
        chatRoomId: Long,
        user: DomainUser,
        approve: Boolean
    ): ExitDecisionResponseDto {
        val login = ensureLoggedIn(user.nickname)
        if (login is Resource.Error) throw IllegalStateException(login.message ?: "로그인 실패")

        val response = roomApiService.decideExit(
            chatRoomId = chatRoomId,
            user = user.toExitQueryMap(),
            body = ExitDecisionRequestDto(approve = approve)
        )
        return response.result
    }

    private fun DomainUser.toExitQueryMap(): Map<String, String> {
        val idLong = sessionId.toLongOrNull() ?: 0L

        return buildMap {
            put("id", idLong.toString())
            put("nickname", nickname)
            // 너희 프로젝트는 login(password)에 nickname을 그대로 쓰고 있으니 동일하게 맞춤 :contentReference[oaicite:1]{index=1}
            put("password", nickname)
            // createdAt/modifiedAt은 보통 필요 없어서 생략
        }
    }
}