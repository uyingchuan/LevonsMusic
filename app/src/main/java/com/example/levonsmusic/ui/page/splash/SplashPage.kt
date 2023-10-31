package com.example.levonsmusic.ui.page.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.levonsmusic.AppNav
import com.example.levonsmusic.R
import com.example.levonsmusic.ScreenPaths
import com.example.levonsmusic.ui.theme.LocalColors
import kotlinx.coroutines.delay

@Composable
fun SplashPage() {
    LaunchedEffect(UInt) {
        delay(1000)
        AppNav.instance.popBackStack()
        AppNav.instance.navigate(ScreenPaths.qrLogin)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColors.current.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier
                .padding(top = 485.dp)
                .size(190.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 480.dp)
                .size(200.dp)
                .clip(RoundedCornerShape(50)),
            tint = LocalColors.current.primary,
        )
    }
}