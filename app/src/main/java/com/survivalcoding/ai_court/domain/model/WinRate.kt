package com.survivalcoding.ai_court.domain.model

data class WinRate(
    val userAScore: Int,
    val userBScore: Int
) {
    val total: Int get() = userAScore + userBScore
    val userAPercentage: Float get() = if (total > 0) userAScore.toFloat() / total else 0.5f
    val userBPercentage: Float get() = if (total > 0) userBScore.toFloat() / total else 0.5f
}

