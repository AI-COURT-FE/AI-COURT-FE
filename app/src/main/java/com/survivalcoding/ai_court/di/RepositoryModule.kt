package com.survivalcoding.ai_court.di

import com.survivalcoding.ai_court.data.repository.ChatRepositoryImpl
import com.survivalcoding.ai_court.data.repository.RoomRepositoryImpl
import com.survivalcoding.ai_court.domain.repository.ChatRepository
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
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
