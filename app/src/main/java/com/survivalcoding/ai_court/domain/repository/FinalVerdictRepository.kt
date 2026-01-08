package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.model.FinalVerdict

interface FinalVerdictRepository {
    suspend fun requestFinalVerdict(
        roomCode: String,
    ): Resource<FinalVerdict>
}