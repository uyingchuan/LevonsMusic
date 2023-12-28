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
import com.elvishew.xlog.XLog
import com.example.levonsmusic.MainActivity
import com.example.levonsmusic.MusicApplication
import com.example.levonsmusic.extension.getNextIndex
import com.example.levonsmusic.extension.getPreIndex
import com.example.levonsmusic.extension.toFormatDuration
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.network.MusicApiEntryFinder
import com.example.levonsmusic.network.MusicApiService
import com.example.levonsmusic.service.MusicPlayerService
import com.example.levonsmusic.util.showTextToast
import org.greenrobot.eventbus.EventBus

object LevonsPlayerController : MusicPlayerListener, DefaultLifecycleObserver {
    // 是否展示底部mini播放器
    var showMiniPlayer by mutableStateOf(false)

    // 是否展示播放列表底部弹窗
    var showPlaylistBottomSheet by mutableStateOf(false)

    // 正在播放的歌曲列表
    val playlist = mutableStateListOf<SongDetail>()

    // 当前播放模式下的原始歌曲列表
    val originPlaylist = mutableStateListOf<SongDetail>()

    // 当前播放的歌单ID
    private var currentPlaylistId: Long? = null

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
        private set

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val musicApi: MusicApiService = MusicApiEntryFinder.getMusicApi()

    init {
        LevonsMusicPlayer.addEventListener(this)
    }

    /**
     * 播放歌单
     * [pid] 播放的初始歌单id
     */
    fun startPlaylist(playlist: List<SongDetail>, index: Int, pid: Long) {
        currentPlaylistId = pid
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
     * 获取心动歌单并播放
     * [id] 歌曲ID
     * [pid] 歌单ID
     * [sid] 要开始播放的歌曲id，判定存在时正在播放，不切歌
     */
    suspend fun startHeartbeatList(id: Long, pid: Long, sid: Long? = null) {
        val heartbeatList = musicApi.getHeartbeatList(id, pid, sid).data
        val playlist: List<SongDetail> = heartbeatList.map { it.songInfo }
        playMode = MusicPlayerMode.HEARTBEAT
        showMiniPlayer = true
        if (sid == null) {
            startPlaylist(playlist, 0, pid)
        } else {
            val currentSongDetail = originPlaylist[currentOriginIndex]
            originPlaylist.clear()
            originPlaylist.add(currentSongDetail)
            originPlaylist.addAll(playlist)
            val index = originPlaylist.indexOfFirst { it.id == sid }
            XLog.d(index)
            generatePlaylist(0)
        }
    }

    /**
     * 根据播放模式[playMode]以及原始歌单[originPlaylist]生成播放歌单[playlist]
     * [MusicPlayerMode.HEARTBEAT]模式由外部调用接口重新获取心动歌曲列表，重新生成歌单实现
     */
    private fun generatePlaylist(originIndex: Int) {
        when (playMode) {
            MusicPlayerMode.RANDOM -> {
                val randomList = mutableListOf<SongDetail>()
                randomList.addAll(originPlaylist)
                randomList.shuffle()
                playlist.clear()
                playlist.addAll(randomList)
                val index = playlist.indexOfFirst { it.id == originPlaylist[originIndex].id }
                currentIndex = index
                currentOriginIndex = originIndex
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
        // 发布切换歌曲事件
        EventBus.getDefault().post(MusicPlayerChangeSongEvent(songDetail))
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
     * 播放下一曲
     */
    fun playNextSong() {
        if (playMode == MusicPlayerMode.SINGLE) {
            playingIndex(currentIndex)
        } else {
            playingIndex(playlist.getNextIndex(currentIndex))
        }
    }

    /**
     * 播放上一曲
     */
    fun playPreSong() {
        if (playMode == MusicPlayerMode.SINGLE) {
            playingIndex(currentIndex)
        } else {
            playingIndex(playlist.getPreIndex(currentIndex))
        }
    }

    /**
     * 从[originPlaylist]中删除指定下标[index]歌曲
     * 如果下标[index]的歌曲是正在播放的歌曲，则切换播放原始列表中的下一曲
     */
    fun removeOriginSongByIndex(index: Int) {
        if (originPlaylist.size <= 1) return

        if (index == currentOriginIndex) {
            val nextSongId = originPlaylist[originPlaylist.getNextIndex(currentOriginIndex)].id
            originPlaylist.removeAt(currentOriginIndex)
            playlist.removeAt(currentIndex)
            playingOriginIndex(originPlaylist.indexOfFirst { it.id == nextSongId })
        } else {
            val removeSongId = originPlaylist[index].id
            val currentSongId = originPlaylist[currentOriginIndex].id
            playlist.removeIf { it.id == removeSongId }
            originPlaylist.removeAt(index)
            currentIndex = playlist.indexOfFirst { it.id == currentSongId }
            currentOriginIndex = playlist.indexOfFirst { it.id == currentSongId }
        }
    }

    /**
     * 改变播放模式
     */
    suspend fun changePlayMode(musicPlayerMode: MusicPlayerMode) {
        playMode = musicPlayerMode
        if (musicPlayerMode == MusicPlayerMode.HEARTBEAT) {
            val id = originPlaylist[currentOriginIndex].id
            currentPlaylistId?.let { pid ->
                startHeartbeatList(id, pid, id)
            }
        } else {
            generatePlaylist(currentOriginIndex)
        }
    }

    /**
     * 从[originPlaylist]中检索指定下标歌曲播放
     */
    fun playingOriginIndex(index: Int) {
        if (originPlaylist.size <= index) return
        currentOriginIndex = index
        generatePlaylist(currentOriginIndex)
        currentIndex = playlist.indexOfFirst { it.id == originPlaylist[index].id }
        startPlaying(originPlaylist[index])
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
                playNextSong()
            }

            MusicPlayerStatus.ERROR -> {
                playNextSong()
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
