package com.survivalcoding.ai_court.data.model.response

import com.survivalcoding.ai_court.domain.model.FinalVerdict
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinalVerdictResponse(
    @SerialName("winner_nickname") val winnerNickname: String = "",
    @SerialName("loser_nickname") val loserNickname: String = "",
    @SerialName("logic_score_A") val plaintiffLogicScore: Int,
    @SerialName("logic_score_B") val defendantLogicScore: Int,
    @SerialName("empathy_score_A") val plaintiffEmpathyScore: Int,
    @SerialName("empathy_score_B") val defendantEmpathyScore: Int,
    @SerialName("judgmentComment") val judgmentComment : String,
    @SerialName("winnerReason") val winnerReason: String = "",
    @SerialName("loserReason") val loserReason: String = "",
) {
    fun toDomain(): FinalVerdict = FinalVerdict(
        winnerNickname = winnerNickname,
        loserNickname = loserNickname,
        plaintiffLogicScore = plaintiffLogicScore,
        defendantLogicScore = defendantLogicScore,
        plaintiffEmpathyScore = plaintiffEmpathyScore,
        defendantEmpathyScore = defendantEmpathyScore,
        judgmentComment = judgmentComment,
        winnerReason = winnerReason,
        loserReason = loserReason
    )
}