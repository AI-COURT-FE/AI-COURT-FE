package com.survivalcoding.ai_court.data.repository

import com.survivalcoding.ai_court.data.api.RoomApiService
import com.survivalcoding.ai_court.data.model.request.LoginRequestDto
import com.survivalcoding.ai_court.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val roomApiService: RoomApiService
) : AuthRepository {
    override suspend fun login(nickname: String, password: String): Result<String> = runCatching {
        val body = roomApiService.login(LoginRequestDto(nickname, password))

        // 서버가 내려주는 비즈니스 성공/실패
        if (!body.success) {
            throw IllegalStateException(body.result) // 보통 실패 사유 문자열
        }
        body.result // 보통 "로그인 성공" 같은 메시지
    }

    override suspend fun logout(): Result<String> = runCatching {
        val body = roomApiService.logout()
        if (!body.success) {
            throw IllegalStateException(body.result)
        }
        body.result
    }
}