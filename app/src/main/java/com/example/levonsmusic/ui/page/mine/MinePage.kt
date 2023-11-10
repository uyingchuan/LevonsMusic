package com.example.levonsmusic.ui.page.mine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.component.BaseComponent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinePage() {
    val scope = rememberCoroutineScope()

    val viewModel: MineViewModel = hiltViewModel()

    LaunchedEffect(UInt) {
        viewModel.getPlaylist()
    }

    // 控制下拉滚动效果
    CompositionLocalProvider(LocalOverscrollConfiguration.provides(null)) {
        Box(modifier = Modifier.fillMaxSize()) {
            BaseComponent(liveData = viewModel.liveData) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .fillMaxSize()
                )
            }
        }
    }
}