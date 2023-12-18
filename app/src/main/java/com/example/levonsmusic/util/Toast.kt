package com.example.levonsmusic.util

import android.widget.Toast
import com.example.levonsmusic.MusicApplication

fun showTextToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    val toast = Toast.makeText(MusicApplication.context, text, duration)
    toast.show()
}