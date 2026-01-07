package com.survivalcoding.ai_court.data.api

import com.survivalcoding.ai_court.data.model.request.CreateRoomRequest
import com.survivalcoding.ai_court.data.model.request.JoinRoomRequest
import com.survivalcoding.ai_court.data.model.request.VerdictRequest
import com.survivalcoding.ai_court.data.model.response.RoomResponse
import com.survivalcoding.ai_court.data.model.response.VerdictResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomApiService {
    @POST("rooms")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<RoomResponse>

    @POST("rooms/join")
    suspend fun joinRoom(@Body request: JoinRoomRequest): Response<RoomResponse>

    @GET("rooms/{roomCode}")
    suspend fun getRoom(@Path("roomCode") roomCode: String): Response<RoomResponse>

    @POST("verdict")
    suspend fun requestVerdict(@Body request: VerdictRequest): Response<VerdictResponse>
}
