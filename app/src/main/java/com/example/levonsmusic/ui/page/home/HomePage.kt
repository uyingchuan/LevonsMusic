package com.example.levonsmusic.ui.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun HomePage() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(LocalColors.current.card))
}