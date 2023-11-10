package com.example.levonsmusic.network

import com.example.levonsmusic.model.AccountInfoResult
import com.example.levonsmusic.model.PlaylistResult
import com.example.levonsmusic.model.QRCodeAuthResult
import com.example.levonsmusic.model.QRCodeKeyResult
import com.example.levonsmusic.model.QRCodeValueResult
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date
import javax.inject.Singleton

private const val BASE_URL = "https://ncmusic.sskevan.cn"

@Module
@InstallIn(SingletonComponent::class)
object MusicApiModule {
    @Provides
    @Singleton
    fun provideMusicApi(): MusicApiService {
        val builder = OkHttpClient.Builder().apply {
            addInterceptor(LoggingInterceptor())
            addInterceptor(LoginInterceptor())
        }
        return Retrofit.Builder().client(builder.build())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiService::class.java)
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

    @GET("/user/playlist")
    suspend fun getPlaylist(@Query("uid") uid: String): PlaylistResult
}

