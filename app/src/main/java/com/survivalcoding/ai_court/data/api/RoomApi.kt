package com.survivalcoding.ai_court.data.api

import com.survivalcoding.ai_court.data.dto.CreateRoomRequest
import com.survivalcoding.ai_court.data.dto.JoinRoomRequest
import com.survivalcoding.ai_court.data.dto.RoomDto
import com.survivalcoding.ai_court.data.dto.VerdictDto
import com.survivalcoding.ai_court.data.dto.VerdictRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomApi {
    @POST("rooms")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<RoomDto>

    @POST("rooms/join")
    suspend fun joinRoom(@Body request: JoinRoomRequest): Response<RoomDto>

    @GET("rooms/{roomCode}")
    suspend fun getRoom(@Path("roomCode") roomCode: String): Response<RoomDto>

    @POST("verdict")
    suspend fun requestVerdict(@Body request: VerdictRequest): Response<VerdictDto>
}

