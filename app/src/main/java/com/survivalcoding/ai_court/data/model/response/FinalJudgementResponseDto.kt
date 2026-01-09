package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class FinalJudgementResponseDto(
    val id: Long,
    val chatRoomId: Long,
    val winner: String,
    val plaintiff: String, // 원고
    val defendant: String, // 피고
    val winnerLogicScore: Int,
    val winnerEmpathyScore: Int,
    val judgmentComment: String,
    val winnerReason: String,
    val loserReason: String
)