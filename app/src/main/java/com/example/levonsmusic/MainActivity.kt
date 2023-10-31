package com.example.levonsmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        // 全屏应用
        // WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LevonsMusicTheme {
                AppNav.instance = rememberNavController()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .background(color = Color.Magenta)
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}