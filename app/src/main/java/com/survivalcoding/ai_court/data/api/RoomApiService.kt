package com.survivalcoding.ai_court.data.api

import com.survivalcoding.ai_court.data.model.request.FinalVerdictRequest
import com.survivalcoding.ai_court.data.model.request.JoinChatRoomRequestDto
import com.survivalcoding.ai_court.data.model.request.LoginRequestDto
import com.survivalcoding.ai_court.data.model.request.SendMessageRequestDto
import com.survivalcoding.ai_court.data.model.request.VerdictRequest
import com.survivalcoding.ai_court.data.model.response.BaseResponse
import com.survivalcoding.ai_court.data.model.response.ChatMessageDto
import com.survivalcoding.ai_court.data.model.response.CreateChatRoomResponseDto
import com.survivalcoding.ai_court.data.model.response.FinalVerdictResponse
import com.survivalcoding.ai_court.data.model.response.JoinChatRoomResponseDto
import com.survivalcoding.ai_court.data.model.response.VerdictResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomApiService {
    // 로그인: 세션 생성
    @POST("login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): BaseResponse<String>

    // 로그아웃: 세션 종료
    @POST("logout")
    suspend fun logout(): BaseResponse<String>

    // 채팅방 생성
    @POST("chat/room/create")
    suspend fun createChatRoom(
        // 서버 스펙이 body를 요구하면 여기에 request dto 추가
    ): BaseResponse<JsonElement>

    // 채팅방 입장 (초대 코드)
    @POST("chat/room/join")
    suspend fun joinChatRoom(
        @Body body: JoinChatRoomRequestDto
    ): BaseResponse<JsonElement>

    // 메시지 목록 조회
    @GET("chat/room/{chatRoomId}/messages")
    suspend fun getMessages(
        @Path("chatRoomId") chatRoomId: Long
    ): BaseResponse<List<ChatMessageDto>>

    // 메시지 전송 (REST)
    @POST("chat/room/{chatRoomId}/message")
    suspend fun sendMessage(
        @Path("chatRoomId") chatRoomId: Long,
        @Body body: SendMessageRequestDto
    ): BaseResponse<ChatMessageDto>

    @POST("verdict")
    suspend fun requestVerdict(
        @Body request: VerdictRequest
    ): Response<VerdictResponse>

    @POST("verdict/final")
    suspend fun requestFinalVerdict(
        @Body request: FinalVerdictRequest
    ): Response<FinalVerdictResponse>
}
