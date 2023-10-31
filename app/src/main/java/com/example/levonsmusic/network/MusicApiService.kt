package com.example.levonsmusic.network

import com.example.levonsmusic.model.AccountInfoResult
import com.example.levonsmusic.model.QRCodeAuthResult
import com.example.levonsmusic.model.QRCodeKeyResult
import com.example.levonsmusic.model.QRCodeValueResult
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date
import javax.inject.Singleton

private const val BASE_URL =
    "https://ncmusic.sskevan.cn"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

@Module
@InstallIn(SingletonComponent::class)
object MusicApiModule {
    @Provides
    @Singleton
    fun provideMusicApi(): MusicApiService {
        return retrofit.create(MusicApiService::class.java)
    }
}

interface MusicApiService {
    @GET("/login/qr/key")
    suspend fun getLoginQRCodeKey(): QRCodeKeyResult

    @GET("/login/qr/create")
    suspend fun getLoginQRCodeValue(
        @Query("key") key: String,
        @Query("timeStamp") timeStamp: Long = Date().time
    ): QRCodeValueResult

    @GET("/login/qr/check")
    suspend fun checkQRCodeAuthStatus(
        @Query("key") key: String,
        @Query("timeStamp") timeStamp: Long = Date().time
    ): QRCodeAuthResult

    @GET("/user/account")
    suspend fun getAccountInfo(
        @Query("cookie") cookie: String,
    ): AccountInfoResult
}

