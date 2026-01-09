package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.domain.model.FinalVerdict
import com.survivalcoding.ai_court.domain.repository.FinalVerdictRepository
import javax.inject.Inject

class FinalVerdictRepositoryImpl @Inject constructor(
    private val api: RoomApiService
) : FinalVerdictRepository {

    override suspend fun getFinalJudgement(chatRoomId: Long): Resource<FinalVerdict> {
        return try {
            val res = api.getFinalJudgement(chatRoomId)

            if (!res.success) {
                val msg = res.message ?: when (res.code) {
                    401 -> "로그인이 필요해요."
                    403 -> "채팅방 멤버만 판결문을 볼 수 있어요."
                    404 -> "아직 판결문이 생성되지 않았어요."
                    else -> "판결문 조회 실패 (code=${res.code})"
                }
                return Resource.Error(msg)
            }

            // BaseResponse.result는 non-null
            val dto = res.result

            val plaintiff = dto.plaintiff
            val defendant = dto.defendant
            val winner = dto.winner
            val loser = if (winner == plaintiff) defendant else plaintiff

            // 서버는 winner 점수만 줌 → loser는 임시로 100 - winner (추후 BE가 둘 다 주면 여기만 수정)
            val winnerLogic = dto.winnerLogicScore.coerceIn(0, 100)
            val winnerEmpathy = dto.winnerEmpathyScore.coerceIn(0, 100)
            val loserLogic = (100 - winnerLogic).coerceIn(0, 100)
            val loserEmpathy = (100 - winnerEmpathy).coerceIn(0, 100)

            val (plaintiffLogic, defendantLogic) =
                if (winner == plaintiff) winnerLogic to loserLogic else loserLogic to winnerLogic
            val (plaintiffEmpathy, defendantEmpathy) =
                if (winner == plaintiff) winnerEmpathy to loserEmpathy else loserEmpathy to winnerEmpathy

            Resource.Success(
                FinalVerdict(
                    winnerNickname = winner,
                    loserNickname = loser,
                    plaintiffNickname = plaintiff,
                    defendantNickname = defendant,
                    plaintiffLogicScore = plaintiffLogic,
                    defendantLogicScore = defendantLogic,
                    plaintiffEmpathyScore = plaintiffEmpathy,
                    defendantEmpathyScore = defendantEmpathy,
                    judgmentComment = dto.judgmentComment,
                    winnerReason = dto.winnerReason,
                    loserReason = dto.loserReason
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "네트워크 오류")
        }
    }
}