package com.example.levonsmusic.network

import com.example.levonsmusic.MusicApplication
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MusicApiEntryPoint {
    fun getMusicApi(): MusicApiService
}

object MusicApiEntryFinder {
    fun getMusicApi(): MusicApiService {
        return EntryPoints.get(MusicApplication.context, MusicApiEntryPoint::class.java)
            .getMusicApi()
    }
}
