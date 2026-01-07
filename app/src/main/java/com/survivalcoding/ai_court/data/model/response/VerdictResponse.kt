package com.survivalcoding.ai_court.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerdictResponse(
    @SerialName("winner") val winner: String,
    @SerialName("winner_nickname") val winnerNickname: String,
    @SerialName("score_A") val scoreA: Int,
    @SerialName("score_B") val scoreB: Int,
    @SerialName("reason") val reason: String,
    @SerialName("summary") val summary: List<String>
)
