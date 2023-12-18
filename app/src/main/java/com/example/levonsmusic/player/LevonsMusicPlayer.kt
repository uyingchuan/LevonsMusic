package com.example.levonsmusic.player

import android.media.MediaPlayer
import android.util.Log
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.network.MusicApiEntryFinder
import com.example.levonsmusic.network.MusicApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

object LevonsMusicPlayer : MusicPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private var playerStatus = MusicPlayerStatus.IDLE

    private val mediaPlayer = MediaPlayer()

    private var currentSongDetail: SongDetail? = null

    private val playerEventListeners = ArrayList<MusicPlayerListener>()

    private var songUrlJob: Job? = null

    private val updateProgressTimer: Timer = Timer()
    private var updateProgressTimerTask: TimerTask? = null

    private val musicApi: MusicApiService = MusicApiEntryFinder.getMusicApi()

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

    override fun setDataSource(songDetail: SongDetail) {
        currentSongDetail = songDetail
    }

    override fun start() {
        Log.d("LevonsMusicPlayer", "Start()")
        if (playerStatus == MusicPlayerStatus.STARTED
            || playerStatus == MusicPlayerStatus.PREPARED
            || playerStatus == MusicPlayerStatus.PAUSED
            || playerStatus == MusicPlayerStatus.COMPLETED
        ) stop()

        currentSongDetail?.let {
            LevonsPlayerController.startPlayerService()
            preparePlayingSong(it.id)
        }
    }

    override fun stop() {
        Log.d("LevonsMusicPlayer", "Stop()")
        updateProgressTimerTask?.cancel()
        mediaPlayer.stop()
        setPlayerStatus(MusicPlayerStatus.STOPPED)
        setPlayerStatus(MusicPlayerStatus.IDLE)
    }

    override fun pause() {
        if (playerStatus != MusicPlayerStatus.STARTED) return

        Log.d("LevonsMusicPlayer", "Pause()")
        updateProgressTimerTask?.cancel()
        setPlayerStatus(MusicPlayerStatus.PAUSED)
        mediaPlayer.pause()
    }

    override fun resume() {
        Log.d("LevonsMusicPlayer", "Resume()")
        startPlay()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d("LevonsMusicPlayer", "OnCompletion()")
        updateProgressTimerTask?.cancel()
        setPlayerStatus(MusicPlayerStatus.COMPLETED)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("LevonsMusicPlayer", "OnPrepared()")
        setPlayerStatus(MusicPlayerStatus.PREPARED)
        startPlay()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("LevonsMusicPlayer", "OnError()")
        updateProgressTimerTask?.cancel()
        setPlayerStatus(MusicPlayerStatus.ERROR)
        setPlayerStatus(MusicPlayerStatus.IDLE)
        return true
    }

    /**
     * 启动播放器
     */
    private fun startPlay() {
        mediaPlayer.start()
        setPlayerStatus(MusicPlayerStatus.STARTED)
        updateProgressTimerTask?.cancel()
        updateProgressTimerTask = object : TimerTask() {
            override fun run() {
                setProgress()
            }
        }
        updateProgressTimer.schedule(updateProgressTimerTask, 0, 1000)
        Log.d("LevonsMusicPlayer", "Start()")
    }

    /**
     * 设置当前播放状态并通知监听者状态改变
     */
    private fun setPlayerStatus(status: MusicPlayerStatus) {
        playerStatus = status
        playerEventListeners.forEach {
            it.onStatusChange(status)
        }
        Log.d("LevonsMusicPlayer", "SetPlayerStatus($status)")
    }

    /**
     * 设置并通知监听者进度改变
     */
    private fun setProgress() {
        playerEventListeners.forEach {
            val percentage =
                ((mediaPlayer.currentPosition * 100f / mediaPlayer.duration) + 0.5f).toInt()
            it.onProgress(mediaPlayer.duration, mediaPlayer.currentPosition, percentage)
        }
    }

    /**
     * 获取音频，进行播放前的准备
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun preparePlayingSong(id: Long) {
        songUrlJob?.cancel()
        songUrlJob = GlobalScope.launch(context = Dispatchers.IO) {
            try {
                val url = musicApi.getSongUrl(id).data.firstOrNull()?.url
                    ?: "https://music.163.com/song/media/outer/url?id=$id.mp3"
                mediaPlayer.reset()
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
                Log.d("LevonsMusicPlayer", "PrepareAsync()")
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.d("LevonsMusicPlayer", "StartPlayingSong error with $e")
                    e.printStackTrace()
                    playerEventListeners.forEach {
                        it.onStatusChange(MusicPlayerStatus.ERROR)
                    }
                }
            }
        }
    }
}