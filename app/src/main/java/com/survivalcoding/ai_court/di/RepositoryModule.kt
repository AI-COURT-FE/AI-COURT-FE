package com.survivalcoding.ai_court.di

import com.survivalcoding.ai_court.data.repository.AuthRepositoryImpl
import com.survivalcoding.ai_court.data.repository.ChatRepositoryImpl
import com.survivalcoding.ai_court.data.repository.FinalVerdictRepositoryImpl
import com.survivalcoding.ai_court.data.repository.RoomRepositoryImpl
import com.survivalcoding.ai_court.domain.repository.AuthRepository
import com.survivalcoding.ai_court.domain.repository.ChatRepository
import com.survivalcoding.ai_court.domain.repository.FinalVerdictRepository
import com.survivalcoding.ai_court.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindFinalVerdictRepository(
        impl: FinalVerdictRepositoryImpl
    ): FinalVerdictRepository
}
