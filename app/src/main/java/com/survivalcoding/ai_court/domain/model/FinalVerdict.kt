package com.survivalcoding.ai_court.domain.model

data class FinalVerdict(
    val winner: String,
    val winnerNickname: String,
    val scoreA: Int,
    val scoreB: Int,
    val reason: String,
    val summary: List<String>
)