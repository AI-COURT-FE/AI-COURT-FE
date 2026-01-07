package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.CreateRoomRequest
import com.survivalcoding.ai_court.data.model.request.JoinRoomRequest
import com.survivalcoding.ai_court.data.model.response.RoomResponse
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : RoomRepository {

    override suspend fun createRoom(hostNickname: String): Resource<Room> {
        return try {
            val response = roomApiService.createRoom(CreateRoomRequest(hostNickname))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun joinRoom(roomCode: String, guestNickname: String): Resource<Room> {
        return try {
            val response = roomApiService.joinRoom(JoinRoomRequest(roomCode, guestNickname))
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                Resource.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun observeRoom(roomCode: String): Flow<Room> = flow {
        while (true) {
            try {
                val response = roomApiService.getRoom(roomCode)
                if (response.isSuccessful && response.body() != null) {
                    emit(response.body()!!.toDomain())
                }
            } catch (e: Exception) {
                // Handle error silently for polling
            }
            delay(2000)
        }
    }

    private fun RoomResponse.toDomain(): Room {
        return Room(
            roomCode = roomCode,
            hostUser = User(sessionId = hostId, nickname = hostNickname),
            guestUser = if (guestId != null && guestNickname != null) {
                User(sessionId = guestId, nickname = guestNickname)
            } else null,
            isReady = isReady
        )
    }
}

