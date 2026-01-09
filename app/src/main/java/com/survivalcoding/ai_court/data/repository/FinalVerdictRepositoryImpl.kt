package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.response.FinalJudgementResponseDto
import com.survivalcoding.ai_court.domain.model.FinalVerdict
import com.survivalcoding.ai_court.domain.repository.FinalVerdictRepository
import javax.inject.Inject

class FinalVerdictRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : FinalVerdictRepository {
    override suspend fun requestFinalVerdict(roomCode: String): Resource<FinalVerdict> {
        return try {
            // 1. 방 코드에서 숫자 ID만 추출
            val chatRoomId = roomCode.replace("-", "").toLongOrNull()
                ?: return Resource.Error("유효하지 않은 방 코드입니다.")

            // 2. API 호출
            val response = roomApiService.getFinalJudgement(chatRoomId)

            // 3. BaseResponse 결과 처리 (isSuccessful 대신 success 필드 사용)
            if (response.success) {
                // response.result는 FinalJudgementResponseDto 타입입니다.
                Resource.Success(response.result.toDomain())
            } else {
                // message와 code는 함수()가 아닌 프로퍼티로 접근
                Resource.Error(response.result.toString(), response.code)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "판결문을 불러오는 중 오류가 발생했습니다.")
        }
    }
}

// 4. 새로운 FinalVerdict 구조에 맞춘 매퍼 함수
private fun FinalJudgementResponseDto.toDomain(): FinalVerdict {
    return FinalVerdict(
        winnerNickname = this.winner,
        loserNickname = if (this.winner == this.plaintiff) this.defendant else this.plaintiff,
        // API 응답의 논리/공감 점수를 도메인의 logicA, empathyA 등에 적절히 배분
        // 여기서는 승자의 점수를 기준으로 예시를 짰습니다.
        logicA = this.winnerLogicScore,
        logicB = 100 - this.winnerLogicScore, // 예시: 나머지를 상대 점수로 할당
        empathyA = this.winnerEmpathyScore,
        empathyB = 100 - this.winnerEmpathyScore,
        reason = this.winnerReason,
        // summary가 List<String>이므로 개행 문자로 나누거나 리스트로 변환
        summary = listOf(this.judgmentComment)
    )
}