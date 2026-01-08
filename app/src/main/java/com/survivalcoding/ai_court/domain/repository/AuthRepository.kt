package com.survivalcoding.ai_court.domain.repository

interface AuthRepository {
    suspend fun login(nickname: String, password: String): Result<String>
    suspend fun logout(): Result<String>
}