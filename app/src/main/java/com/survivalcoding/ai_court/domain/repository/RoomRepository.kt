package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.model.response.ExitDecisionResponseDto
import com.survivalcoding.ai_court.data.model.response.ExitRequestResponseDto
import com.survivalcoding.ai_court.domain.model.Room
import com.survivalcoding.ai_court.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    // password 추가 (기존 호출부 안 깨지게 기본값 = nickname)
    suspend fun createRoom(
        hostNickname: String,
        hostPassword: String = hostNickname,
    ): Resource<Room>

    // password를 추가 (기존 호출부 안 깨지게 기본값 = nickname)
    suspend fun joinRoom(
        roomCode: String,
        guestNickname: String,
        guestPassword: String = guestNickname,
    ): Resource<Room>

    fun observeRoom(roomCode: String): Flow<Room>

    // exit에도 password 추가 (기존 호출부 안 깨지게 기본값 = user.nickname)
    suspend fun requestExit(
        chatRoomId: Long,
        user: User,
        password: String = user.nickname,
    ): ExitRequestResponseDto

    // exit에도 password 추가 (기존 호출부 안 깨지게 기본값 = user.nickname)
    suspend fun decideExit(
        chatRoomId: Long,
        user: User,
        approve: Boolean,
        password: String = user.nickname,
    ): ExitDecisionResponseDto
}

