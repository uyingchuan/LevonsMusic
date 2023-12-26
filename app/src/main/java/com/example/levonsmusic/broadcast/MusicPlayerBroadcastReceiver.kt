package com.example.levonsmusic.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.levonsmusic.MusicApplication
import com.example.levonsmusic.player.LevonsPlayerController

class MusicPlayerBroadcastReceiver : BroadcastReceiver() {
    companion object {
        val ACTION_MUSIC_NOTIFICATION =
            MusicApplication.context.packageName + ".NOTIFICATION_ACTIONS"
        const val KEY_EXTRA = "action_extra"
        const val ACTION_PLAY = "action_play"
        const val ACTION_PRE = "action_pre"
        const val ACTION_NEXT = "action_next"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getStringExtra(KEY_EXTRA)?.run {
            when (this) {
                ACTION_PLAY -> {
                    if (LevonsPlayerController.isPlaying) {
                        LevonsPlayerController.pause()
                    } else {
                        LevonsPlayerController.resume()
                    }
                }

                ACTION_NEXT -> {
                    LevonsPlayerController.playNextSong()
                }

                ACTION_PRE -> {
                    LevonsPlayerController.playPreSong()
                }
            }
        }
    }


}