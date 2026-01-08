package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.model.response.ExitDecisionResponseDto
import com.survivalcoding.ai_court.data.model.response.ExitRequestResponseDto
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun createRoom(hostNickname: String): Resource<Room>
    suspend fun joinRoom(roomCode: String, guestNickname: String): Resource<Room>
    fun observeRoom(roomCode: String): Flow<Room>
    suspend fun requestExit(chatRoomId: Long, user: User): ExitRequestResponseDto
    suspend fun decideExit(chatRoomId: Long, user: User, approve: Boolean): ExitDecisionResponseDto
}

