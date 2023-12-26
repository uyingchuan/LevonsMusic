package com.example.levonsmusic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.levonsmusic.MainActivity
import com.example.levonsmusic.MusicApplication
import com.example.levonsmusic.R
import com.example.levonsmusic.broadcast.MusicPlayerBroadcastReceiver
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.player.MusicPlayerPauseEvent
import com.example.levonsmusic.player.MusicPlayerPlayEvent
import com.example.levonsmusic.util.getRoundedCornerBitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MusicPlayerService : Service() {
    private var musicPlayerBroadcastReceiver: MusicPlayerBroadcastReceiver? = null

    companion object {
        const val ACTION_START_MUSIC_SERVICE = "ACTION_START_MUSIC_SERVICE"
    }

    /**
     * 首次创建服务时系统调用此方法
     */
    override

    fun onCreate() {
        Log.d("MusicPlayerService", "OnCreate()")
        super.onCreate()
        // 注册通知接收器
        registerBroadcastReceiver()
    }

    /**
     * 当组件想启动服务时，系统通过调用[startService]启动服务，并会调用此方法
     * 服务启动后一直运行，直到调用[stopSelf]自行停止，或组件调用[stopService]使服务停止
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicPlayerService", "OnStartCommand()")
        intent?.action?.let {
            // 监听开始播放音乐动作，初始化通知栏音乐控件
            if (it == ACTION_START_MUSIC_SERVICE) {
                MusicPlayerNotification.init()
                startForeground(
                    MusicPlayerNotification.ID,
                    MusicPlayerNotification.playerNotification()
                )
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 当服务即将被销毁时系统调用此方法
     */
    override fun onDestroy() {
        unregisterBroadcastReceiver()
        super.onDestroy()
    }

    private fun registerBroadcastReceiver() {
        if (musicPlayerBroadcastReceiver == null) {
            musicPlayerBroadcastReceiver = MusicPlayerBroadcastReceiver()
            registerReceiver(musicPlayerBroadcastReceiver, IntentFilter().apply {
                addAction(MusicPlayerBroadcastReceiver.ACTION_MUSIC_NOTIFICATION)
            })
        }
    }

    private fun unregisterBroadcastReceiver() {
        musicPlayerBroadcastReceiver?.let {
            unregisterReceiver(it)
        }
    }

    /**
     * 当组件想与服务绑定时，系统会调用[bindService]后调用此方法
     * 返回[IBinder]提供一个接口，以供客户端和服务端进行通信
     */
    override fun onBind(intent: Intent?): IBinder? = null
}

object MusicPlayerNotification {
    private const val CHANNEL_ID = "CHANNEL_MUSIC_PLAYER_ID"
    private const val CHANNEL_NAME = "播放器"

    private var notification: Notification? = null
    private var notificationManager: NotificationManager? = null
    private var remoteViews: RemoteViews? = null

    const val ID = 100

    private var loadCoverImageJob: Job? = null

    init {
        EventBus.getDefault().register(this)
    }

    fun playerNotification() = notification

    fun init() {
        notificationManager = MusicApplication.context
            .getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        initNotification()
    }

    /**
     * 初始化notification及remoteView
     */
    private fun initNotification() {
        loadCoverImageJob?.cancel()
        initRemoteViews()

        // 单击notification时触发的intent
        val intent = Intent(MusicApplication.context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            MusicApplication.context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 适配Android8.0以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                IMPORTANCE_HIGH,
            )
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setSound(null, null)
            notificationManager!!.createNotificationChannel(channel)
        }

        // 配置构建
        val builder = NotificationCompat.Builder(MusicApplication.context, CHANNEL_ID)
            .setShowWhen(false) // 隐藏展示时间戳
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
            .setSmallIcon(R.drawable.ic_music_notification)
            .setCustomBigContentView(remoteViews)
            .setContent(remoteViews)
        notification = builder.build()

        // 更新notification上的视图
        updateNotification()

        // 发送notification
        notificationManager!!.notify(ID, notification!!)
    }

    /**
     * 初始化remoteView，注册相关intent
     */
    private fun initRemoteViews() {
        remoteViews = RemoteViews(
            MusicApplication.context.packageName,
            R.layout.layout_music_notification,
        )

        // 播放、暂停事件
        val playIntent = Intent(MusicPlayerBroadcastReceiver.ACTION_MUSIC_NOTIFICATION).apply {
            putExtra(
                MusicPlayerBroadcastReceiver.KEY_EXTRA,
                MusicPlayerBroadcastReceiver.ACTION_PLAY
            )
        }
        val playPendingIntent = PendingIntent.getBroadcast(
            MusicApplication.context,
            1,
            playIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews?.setOnClickPendingIntent(R.id.ivPlay, playPendingIntent)

        // 上一曲
        val preIntent = Intent(MusicPlayerBroadcastReceiver.ACTION_MUSIC_NOTIFICATION).apply {
            putExtra(
                MusicPlayerBroadcastReceiver.KEY_EXTRA,
                MusicPlayerBroadcastReceiver.ACTION_PRE
            )
        }
        val prePendingIntent = PendingIntent.getBroadcast(
            MusicApplication.context,
            2,
            preIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews?.setOnClickPendingIntent(R.id.ivPre, prePendingIntent)

        // 下一曲
        val nextIntent = Intent(MusicPlayerBroadcastReceiver.ACTION_MUSIC_NOTIFICATION).apply {
            putExtra(
                MusicPlayerBroadcastReceiver.KEY_EXTRA,
                MusicPlayerBroadcastReceiver.ACTION_NEXT
            )
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            MusicApplication.context,
            3,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews?.setOnClickPendingIntent(R.id.ivNext, nextPendingIntent)
    }

    /**
     * 更新remoteView
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun updateNotification() {
        val index = LevonsPlayerController.currentOriginIndex
        val currentSongDetail = LevonsPlayerController.originPlaylist.getOrNull(index)
        currentSongDetail?.let { detail ->
            remoteViews?.run {
                // 设置默认底图
                setImageViewResource(
                    R.id.ivCover,
                    R.drawable.ic_default_disk_cover
                )
                // 设置歌名
                setTextViewText(R.id.tvSongName, detail.name)
                // 设置作者名
                setTextViewText(R.id.tvAuthor, detail.ar.getOrNull(0)?.name ?: "未知")
                // 设置播放暂停按钮
                setImageViewResource(
                    R.id.ivPlay,
                    if (LevonsPlayerController.isPlaying) R.drawable.ic_music_notification_pause else R.drawable.ic_music_notification_play
                )
                // 制作封面图bitmap并设置
                loadCoverImageJob?.cancel()
                loadCoverImageJob = GlobalScope.launch {
                    MusicApplication.context.getImageBitmapByUrl(detail.al.picUrl)?.let {
                        setImageViewBitmap(R.id.ivCover, getRoundedCornerBitmap(it, 30))
                    }
                }
            }
        }
    }

    /**
     * 监听EventBus中的歌曲暂停事件，更新remoteView
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MusicPlayerPauseEvent) {
        remoteViews?.run {
            setImageViewResource(R.id.ivPlay, R.drawable.ic_music_notification_play)
            notificationManager!!.notify(ID, notification!!)
        }
    }

    /**
     * 监听EventBus中的播放歌曲事件，更新remoteView
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MusicPlayerPlayEvent) {
        remoteViews?.run {
            setImageViewResource(R.id.ivPlay, R.drawable.ic_music_notification_pause)
            notificationManager!!.notify(ID, notification!!)
        }
    }

    private suspend fun Context.getImageBitmapByUrl(url: String): Bitmap? {
        val request = ImageRequest.Builder(this)
            .size(200)
            .data(url)
            .allowHardware(false)
            .build()
        val result = (imageLoader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}