package com.example.levonsmusic.player

interface MusicPlayerListener {
    fun onStatusChange(status: MusicPlayerStatus)
    fun onProgress(duration: Int, position: Int, percentage: Int)
}