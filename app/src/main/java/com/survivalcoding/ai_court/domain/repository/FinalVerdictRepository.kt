package com.survivalcoding.ai_court.domain.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.model.FinalVerdict

interface FinalVerdictRepository {
    suspend fun getFinalJudgement(chatRoomId: Long): Resource<FinalVerdict>
}