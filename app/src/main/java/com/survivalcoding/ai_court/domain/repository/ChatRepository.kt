package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.model.response.FinalJudgementResponseDto
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.domain.model.ChatRoomStatus
import com.survivalcoding.ai_court.domain.model.WinRate
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connectToRoom(roomCode: String, userId: String)
    fun disconnectFromRoom()
    suspend fun sendMessage(content: String): Resource<Unit>
    fun observeMessages(): Flow<List<ChatMessage>>
    fun observeWinRate(): Flow<WinRate>
    fun observeChatRoomStatus(): Flow<ChatRoomStatus>
    fun observeFinishRequestNickname(): Flow<String?>
    
    // 채팅방 종료(판결) 요청
    suspend fun requestExit(chatRoomId: Long): Resource<Unit>
    
    // 최종 판결 조회
    suspend fun getFinalJudgement(chatRoomId: Long): Resource<FinalJudgementResponseDto>
}

