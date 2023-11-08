package com.example.levonsmusic.ui.page.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.levonsmusic.R
import com.example.levonsmusic.component.AppBar

@Composable
fun PodcastPage() {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AppBar(leftIcon = R.drawable.ic_drawer_toggle, title = "博客")
        Text("博客")
    }
}