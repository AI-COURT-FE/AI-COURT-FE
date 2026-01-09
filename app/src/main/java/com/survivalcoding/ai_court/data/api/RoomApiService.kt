package com.survivalcoding.ai_court.data.api

import com.survivalcoding.ai_court.data.model.request.ExitDecisionRequestDto
import com.survivalcoding.ai_court.data.model.request.FinalVerdictRequest
import com.survivalcoding.ai_court.data.model.request.JoinChatRoomRequestDto
import com.survivalcoding.ai_court.data.model.request.LoginRequestDto
import com.survivalcoding.ai_court.data.model.request.SendMessageRequestDto
import com.survivalcoding.ai_court.data.model.request.VerdictRequest
import com.survivalcoding.ai_court.data.model.response.BaseResponse
import com.survivalcoding.ai_court.data.model.response.ChatMessageDto
import com.survivalcoding.ai_court.data.model.response.ExitDecisionResponseDto
import com.survivalcoding.ai_court.data.model.response.ExitRequestResponseDto
import com.survivalcoding.ai_court.data.model.response.FinalVerdictResponse
import com.survivalcoding.ai_court.data.model.response.PollResponseDto
import com.survivalcoding.ai_court.data.model.response.VerdictResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RoomApiService {
    // 상태 폴링 (메시지, 채팅방 상태, 승률) TODO: 이거 아직 확정은 아님

    @GET("chat/poll")
    suspend fun pollChatRoom(
        @Query("chatRoomId") chatRoomId: Long,
        @Query("lastMessageId") lastMessageId: Long? = null
    ): BaseResponse<com.survivalcoding.ai_court.data.model.response.PollResponseDto>

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

    // 채팅방 종료(판결) 요청
    @POST("chat/room/{chatRoomId}/exit/request")
    suspend fun requestExit(
        @Path("chatRoomId") chatRoomId: Long,
        // 스웨거 "user (query) object" → QueryMap으로 펼쳐서 전송
        @QueryMap user: Map<String, String>
    ): BaseResponse<ExitRequestResponseDto>

    // 채팅방 종료(판결) 요청 결정
    @POST("chat/room/{chatRoomId}/exit/decide")
    suspend fun decideExit(
        @Path("chatRoomId") chatRoomId: Long,
        @QueryMap user: Map<String, String>,
        @Body body: ExitDecisionRequestDto
    ): BaseResponse<ExitDecisionResponseDto>

    @POST("verdict")
    suspend fun requestVerdict(
        @Body request: VerdictRequest
    ): Response<VerdictResponse>

    @POST("verdict/final")
    suspend fun requestFinalVerdict(
        @Body request: FinalVerdictRequest
    ): Response<FinalVerdictResponse>
}
