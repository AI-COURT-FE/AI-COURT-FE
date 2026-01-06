package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApi
import com.survivalcoding.ai_court.data.dto.CreateRoomRequest
import com.survivalcoding.ai_court.data.dto.JoinRoomRequest
import com.survivalcoding.ai_court.data.mapper.toDomain
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val roomApi: RoomApi
) : RoomRepository {

    override suspend fun createRoom(hostNickname: String): Resource<Room> {
        return try {
            val response = roomApi.createRoom(CreateRoomRequest(hostNickname))
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
            val response = roomApi.joinRoom(JoinRoomRequest(roomCode, guestNickname))
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
                val response = roomApi.getRoom(roomCode)
                if (response.isSuccessful && response.body() != null) {
                    emit(response.body()!!.toDomain())
                }
            } catch (e: Exception) {
                // Handle error silently for polling
            }
            delay(2000) // Poll every 2 seconds
        }
    }
}

