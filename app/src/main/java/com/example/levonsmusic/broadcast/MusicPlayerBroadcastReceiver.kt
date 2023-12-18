package com.example.levonsmusic.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.levonsmusic.MusicApplication

class MusicPlayerBroadcastReceiver : BroadcastReceiver() {
    companion object {
        val ACTION_MUSIC_NOTIFICATION =
            MusicApplication.context.packageName + ".NOTIFICATION_ACTIONS"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
    }


}