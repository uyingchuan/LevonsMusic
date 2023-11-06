package com.example.levonsmusic.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.levonsmusic.model.SongResult

object LevonsPlayerController : MusicPlayerListener {
    var showMiniPlayer by mutableStateOf(false)

    val playlist = mutableStateListOf<SongResult>()

    var isPlaying by mutableStateOf(false)

    var duration by mutableIntStateOf(0)
    var progress by mutableIntStateOf(0)


    var playMode by mutableStateOf(MusicPlayerMode.LOOP)

    init {
        LevonsMusicPlayer.addEventListener(this)
    }

    override fun onStatusChange(status: MusicPlayerStatus) {
    }

    override fun onProgress(duration: Int, position: Int, percentage: Int) {
    }
}
