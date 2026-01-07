package com.survivalcoding.ai_court.data.model.response

import com.survivalcoding.ai_court.domain.model.FinalVerdict
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinalVerdictResponse(
    @SerialName("winner") val winner: String,
    @SerialName("winner_nickname") val winnerNickname: String,
    @SerialName("score_A") val scoreA: Int = 0,
    @SerialName("score_B") val scoreB: Int = 0,
    @SerialName("reason") val reason: String = "",
    @SerialName("summary") val summary: List<String> = emptyList()
) {
    fun toDomain(): FinalVerdict = FinalVerdict(
        winner = winner,
        winnerNickname = winnerNickname,
        scoreA = scoreA,
        scoreB = scoreB,
        reason = reason,
        summary = summary
    )
}