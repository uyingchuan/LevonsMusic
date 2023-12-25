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
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.levonsmusic.MainActivity
import com.example.levonsmusic.MusicApplication
import com.example.levonsmusic.R
import com.example.levonsmusic.broadcast.MusicPlayerBroadcastReceiver

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
    private const val CHANNEL_NAME = "CHANNEL_MUSIC_PLAYER_NAME"

    private var notification: Notification? = null
    private var notificationManager: NotificationManager? = null
    private var remoteViews: RemoteViews? = null

    const val ID = 100

    fun playerNotification() = notification

    fun init() {
        notificationManager = MusicApplication.context
            .getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        initNotification()
    }

    private fun initNotification() {
        initRemoteViews()

        // 构建notification
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
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
            .setSmallIcon(R.drawable.ic_music_notification)
            .setCustomBigContentView(remoteViews)
            .setContent(remoteViews)

        notification = builder.build()
        notificationManager!!.notify(ID, notification!!)
    }

    private fun initRemoteViews() {
        remoteViews = RemoteViews(
            MusicApplication.context.packageName,
            R.layout.layout_music_notification,
        )
    }

}