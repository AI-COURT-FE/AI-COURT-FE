package com.survivalcoding.ai_court.domain.model

data class FinalVerdict(
    val winnerNickname: String,
    val loserNickname: String = "",

    // 추가: 역할 식별용
    val plaintiffNickname: String = "",
    val defendantNickname: String = "",

    val plaintiffLogicScore: Int,
    val defendantLogicScore: Int,
    val plaintiffEmpathyScore: Int,
    val defendantEmpathyScore: Int,

    val judgmentComment: String,
    val winnerReason: String,
    val loserReason: String
)