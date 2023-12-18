package com.example.levonsmusic.extension

import java.util.Locale

/**
 * format ms to 00:00
 */
fun Int.toFormatDuration(): String {
    val second = this / 1000
    return if (second < 0) {
        "00:00"
    } else if (second < 60) {
        String.format(Locale.getDefault(), "00:%02d", second)
    } else if (second < 3600) {
        String.format(Locale.getDefault(), "%02d:%02d", second / 60, second % 60)
    } else {
        String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            second / 3600,
            second % 3600 / 60,
            second % 60
        )
    }
}