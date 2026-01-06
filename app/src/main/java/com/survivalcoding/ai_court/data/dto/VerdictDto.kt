package com.survivalcoding.ai_court.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerdictDto(
    @SerialName("winner") val winner: String,
    @SerialName("winner_nickname") val winnerNickname: String,
    @SerialName("score_A") val scoreA: Int,
    @SerialName("score_B") val scoreB: Int,
    @SerialName("reason") val reason: String,
    @SerialName("summary") val summary: List<String>
)

@Serializable
data class WinRateDto(
    @SerialName("user_a_score") val userAScore: Int,
    @SerialName("user_b_score") val userBScore: Int
)

@Serializable
data class VerdictRequest(
    @SerialName("room_code") val roomCode: String
)

