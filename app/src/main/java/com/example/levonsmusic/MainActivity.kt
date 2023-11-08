package com.example.levonsmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.example.levonsmusic.ui.theme.LevonsMusicTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init xLog
        val config = LogConfiguration.Builder()
            .logLevel(LogLevel.ALL)
            .enableBorder()
            .build()
        XLog.init(config)

        setContent {
            LevonsMusicTheme {
                AppNav.instance = rememberNavController()
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 沉浸式状态栏
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.statusBarColor = Color.Transparent.toArgb()
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !isSystemInDarkTheme()

                    AppNavGraph()
                }
            }
        }
    }
}