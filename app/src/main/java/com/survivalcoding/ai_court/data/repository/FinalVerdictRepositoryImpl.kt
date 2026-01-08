package com.survivalcoding.ai_court.data.repository

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
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Resource.Success(body.toDomain())
            } else {
                Resource.Error(response.message(), response.code())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}