package com.survivalcoding.ai_court.domain.model

data class FinalVerdict(
    val winnerNickname: String,
    val loserNickname: String = "",

    val logicA: Int,
    val logicB: Int,
    val empathyA: Int,
    val empathyB: Int,

    val reason: String,
    val summary: List<String>
)