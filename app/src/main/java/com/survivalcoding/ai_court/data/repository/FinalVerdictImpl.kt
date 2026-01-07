package com.survivalcoding.ai_court.data.repository

import android.util.Log.e
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.FinalVerdictRequest
import com.survivalcoding.ai_court.domain.model.FinalVerdict
import com.survivalcoding.ai_court.domain.repository.FinalVerdictRepository
import javax.inject.Inject

class FinalVerdictRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : FinalVerdictRepository {

    override suspend fun requestFinalVerdict(roomCode: String): Resource<FinalVerdict> {
        return try {
            val response = roomApiService.requestFinalVerdict(FinalVerdictRequest(roomCode))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body.toDomain())
                } else {
                    // 200인데 body가 null인 이상 케이스
                    Resource.Error("Response body is null", response.code())
                }
            } else {
                // 서버가 내려준 에러 내용(있으면)까지 포함해서 보여주기
                val errorBodyText = runCatching { response.errorBody()?.string() }.getOrNull()
                val msg = buildString {
                    append("HTTP ${response.code()}")
                    if (!response.message().isNullOrBlank()) append(" - ${response.message()}")
                    if (!errorBodyText.isNullOrBlank()) append(" / $errorBodyText")
                }
                Resource.Error(msg, response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}