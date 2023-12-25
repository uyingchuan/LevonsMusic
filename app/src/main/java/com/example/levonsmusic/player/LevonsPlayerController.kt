package com.example.levonsmusic.player

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.levonsmusic.MainActivity
import com.example.levonsmusic.MusicApplication
import com.example.levonsmusic.extension.getNextIndex
import com.example.levonsmusic.extension.toFormatDuration
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.service.MusicPlayerService
import com.example.levonsmusic.util.showTextToast

object LevonsPlayerController : MusicPlayerListener, DefaultLifecycleObserver {
    // 是否展示底部mini播放器
    var showMiniPlayer by mutableStateOf(false)

    // 正在播放的歌曲列表
    val playlist = mutableStateListOf<SongDetail>()

    // 当前播放模式下的原始歌曲列表
    val originPlaylist = mutableStateListOf<SongDetail>()

    // 当前播放歌曲在播放列表中的下标
    var currentIndex by mutableIntStateOf(-1)
        private set

    // 当前播放歌曲在原始播放列表中的下标
    var currentOriginIndex by mutableIntStateOf(-1)
        private set

    // 当前播放歌曲播放进度
    var progress by mutableIntStateOf(0)
        private set

    // 当前播放歌曲当前播放位置
    var currentPosition by mutableStateOf("00:00")
        private set

    // 当前播放歌曲总时长
    var totalDuration by mutableStateOf("00:00")
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var playMode by mutableStateOf(MusicPlayerMode.LOOP)

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    init {
        LevonsMusicPlayer.addEventListener(this)
    }

    /**
     * 播放歌单
     */
    fun startPlaylist(playlist: List<SongDetail>, index: Int) {
        originPlaylist.clear()
        originPlaylist.addAll(playlist)
        generatePlaylist(index)
        startPlaying(playlist[index])
    }

    /**
     * 暂停当前播放
     */
    fun pause() {
        LevonsMusicPlayer.pause()
    }

    /**
     * 恢复当前播放
     */
    fun resume() {
        LevonsMusicPlayer.resume()
    }

    /**
     * 判断传入歌曲是否是当前播放歌曲
     */
    fun checkIsSongPlaying(songDetail: SongDetail): Boolean {
        return originPlaylist.getOrNull(currentOriginIndex)?.id == songDetail.id
    }

    /**
     * 根据播放模式[playMode]以及原始歌单[originPlaylist]生成播放歌单[playlist]
     */
    private fun generatePlaylist(originIndex: Int) {
        when (playMode) {
            MusicPlayerMode.RANDOM -> {
                playlist.clear()
                playlist.addAll(originPlaylist)
            }

            else -> {
                playlist.clear()
                playlist.addAll(originPlaylist)
                currentIndex = originIndex
                currentOriginIndex = originIndex
            }
        }
    }

    /**
     * 开始播放指定歌曲
     */
    private fun startPlaying(songDetail: SongDetail) {
        LevonsMusicPlayer.setDataSource(songDetail)
        LevonsMusicPlayer.start()
    }

    override fun onCreate(owner: LifecycleOwner) {
        requestPermissionLauncher = MainActivity.instance.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { it ->
            it.entries.forEach {
                if (it.key == "android.permission.POST_NOTIFICATIONS" && !it.value) {
                    Log.i("Levons Music", "POST_NOTIFICATIONS permission request refused")
                } else if (it.key == "android.permission.POST_NOTIFICATIONS" && it.value) {
                    startPlayerService()
                }
            }
        }
        super.onCreate(owner)
    }

    /**
     * 自动播放下一曲
     */
    private fun playingNextSong() {
        if (playMode == MusicPlayerMode.SINGLE) {
            resume()
        } else {
            playingIndex(playlist.getNextIndex(currentIndex))
        }
    }

    /**
     * 从[playlist]中检索指定下标歌曲并播放
     */
    private fun playingIndex(index: Int) {
        if (originPlaylist.size <= index) return
        currentIndex = index
        currentOriginIndex = originPlaylist.indexOfFirst { it.id == playlist[index].id }
        startPlaying(playlist[index])
    }

    /**
     * 发送Intent，启动音乐播放器
     */
    fun startPlayerService() {
        // 13.0 需获取通知栏权限
        // 启动音乐播放前检查通知栏权限，主动获取权限以便在通知栏实现播放控件
        // tip: 不强求权限，但每次都询问请求
        if (Build.VERSION.SDK_INT >= 33) {
            val status = ContextCompat.checkSelfPermission(
                MusicApplication.context,
                Manifest.permission.POST_NOTIFICATIONS,
            )
            if (status != PackageManager.PERMISSION_GRANTED) {
                showTextToast("在通知栏显示播放器需要授权通知权限")
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                )
            }
        }
        // 启动播放功能
        val intent = Intent(MusicApplication.context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_START_MUSIC_SERVICE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MusicApplication.context.startForegroundService(intent)
        } else {
            MusicApplication.context.startService(intent)
        }
    }

    override fun onStatusChange(status: MusicPlayerStatus) {
        isPlaying = status == MusicPlayerStatus.STARTED
        when (status) {
            MusicPlayerStatus.COMPLETED -> {
                playingNextSong()
            }

            MusicPlayerStatus.ERROR -> {
                playingNextSong()
            }

            MusicPlayerStatus.STOPPED -> {
                currentPosition = "00:00"
                totalDuration = "00:00"
                progress = 0
            }

            else -> {}
        }
    }

    override fun onProgress(duration: Int, position: Int, percentage: Int) {
        totalDuration = duration.toFormatDuration()
        currentPosition = position.toFormatDuration()
        progress = percentage
    }
}
