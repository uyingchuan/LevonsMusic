package com.example.levonsmusic.player

import android.media.MediaPlayer
import android.util.Log

object LevonsMusicPlayer : MusicPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private var playerStatus = MusicPlayerStatus.IDLE

    private val mediaPlayer = MediaPlayer()

    private val playerEventListeners = ArrayList<MusicPlayerListener>()

    init {
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnBufferingUpdateListener(this)
        mediaPlayer.setOnErrorListener(this)
    }

    fun addEventListener(listener: MusicPlayerListener) {
        playerEventListeners.add(listener)
    }

    fun removeEventListener(listener: MusicPlayerListener) {
        playerEventListeners.remove(listener)
    }

    override fun start() {
        Log.d("Player", "Start()")
    }

    override fun stop() {
        Log.d("Player", "Stop()")

    }

    override fun pause() {
        Log.d("Player", "Pause()")

    }

    override fun resume() {
        Log.d("Player", "Resume()")

    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d("Player", "OnCompletion()")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("Player", "OnPrepared()")
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("Player", "OnError()")
        return true
    }
}