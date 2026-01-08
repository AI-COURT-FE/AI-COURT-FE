package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.JoinChatRoomRequestDto
import com.survivalcoding.ai_court.data.model.response.CreateChatRoomResponseDto
import com.survivalcoding.ai_court.data.model.response.JoinChatRoomResponseDto
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : RoomRepository {

    // 로컬 캐시 (getRoom API가 없으므로 생성/입장 시 저장)
    private val _currentRoom = MutableStateFlow<Room?>(null)

    override suspend fun createRoom(hostNickname: String): Resource<Room> {
        return try {
            val response = roomApiService.createChatRoom()
            if (response.success) {
                val room = response.result.toDomain(hostNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                Resource.Error("Failed to create room", response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun joinRoom(roomCode: String, guestNickname: String): Resource<Room> {
        return try {
            val request = JoinChatRoomRequestDto(inviteCode = roomCode)
            val response = roomApiService.joinChatRoom(request)
            if (response.success) {
                val room = response.result.toDomain(guestNickname)
                _currentRoom.value = room
                Resource.Success(room)
            } else {
                Resource.Error("Failed to join room", response.code)
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
            roomCode = participantCode, // 참가자 코드를 roomCode로 사용
            hostUser = User(
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
            hostUser = User(
                sessionId = "",
                nickname = "" // 입장 시에는 호스트 정보를 알 수 없음
            ),
            guestUser = User(
                sessionId = chatRoomId.toString(),
                nickname = guestNickname
            ),
            isReady = true // 입장했으므로 준비됨
        )
    }
}