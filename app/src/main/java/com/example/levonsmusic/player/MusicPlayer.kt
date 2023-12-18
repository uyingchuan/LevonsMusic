package com.example.levonsmusic.player

import com.example.levonsmusic.model.SongDetail

interface MusicPlayer {
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun setDataSource(songDetail: SongDetail)
}