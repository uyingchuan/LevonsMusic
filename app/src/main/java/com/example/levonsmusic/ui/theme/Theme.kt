package com.example.levonsmusic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalColors = compositionLocalOf { lightScheme }

@Composable
fun LevonsMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val appColors = if (darkTheme) darkScheme else lightScheme

    CompositionLocalProvider(LocalColors provides appColors) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}