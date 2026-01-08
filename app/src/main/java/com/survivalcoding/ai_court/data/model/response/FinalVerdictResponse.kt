package com.survivalcoding.ai_court.data.model.response

import com.survivalcoding.ai_court.domain.model.FinalVerdict
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinalVerdictResponse(
    @SerialName("winner_nickname") val winnerNickname: String = "",
    @SerialName("loser_nickname") val loserNickname: String = "",
    @SerialName("logic_score_A") val logicA: Int,
    @SerialName("logic_score_B") val logicB: Int,
    @SerialName("empathy_score_A") val empathyA: Int,
    @SerialName("empathy_score_B") val empathyB: Int,
    @SerialName("reason") val reason: String = "",
    @SerialName("summary") val summary: List<String> = emptyList()
) {
    fun toDomain(): FinalVerdict = FinalVerdict(
        winnerNickname = winnerNickname,
        loserNickname = loserNickname,
        logicA = logicA,
        logicB = logicB,
        empathyA = empathyA,
        empathyB = empathyB,
        reason = reason,
        summary = summary
    )
}