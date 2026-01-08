package com.survivalcoding.ai_court.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.survivalcoding.ai_court.BuildConfig
import com.survivalcoding.ai_court.data.api.SessionCookieJar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSessionCookieJar(): SessionCookieJar = SessionCookieJar()

    @Provides
    @Singleton
    fun providesOkHttpClient(cookieJar: SessionCookieJar): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar) // 세션 유지 핵심
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesConverterFactory(json: Json): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun providesRetrofit(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        val baseUrl = BuildConfig.BASE_URL ?: "http://10.0.2.2:8080/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
    }
}