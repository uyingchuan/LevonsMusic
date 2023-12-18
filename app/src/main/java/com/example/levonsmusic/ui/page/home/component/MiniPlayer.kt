package com.example.levonsmusic.ui.page.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.levonsmusic.AppNav
import com.example.levonsmusic.R
import com.example.levonsmusic.ScreenPaths
import com.example.levonsmusic.component.AssetIcon
import com.example.levonsmusic.component.AssetImage
import com.example.levonsmusic.component.CircleProgress
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.ui.theme.LocalColors
import androidx.compose.ui.unit.dp as odp

val MiniPlayerHeight = 104.dp

@Composable
fun BoxScope.MiniPlayer() {
    if (LevonsPlayerController.playlist.size > 0) {
        val route = AppNav.instance.currentBackStackEntryAsState().value?.destination?.route
        val padding = animateDpAsState(
            targetValue = if (route == ScreenPaths.home) 81.odp else 0.odp,
            label = "Mini Player Visible"
        )

        AnimatedVisibility(
            modifier = Modifier
                .padding(bottom = padding.value)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            visible = LevonsPlayerController.showMiniPlayer,
            enter = slideInVertically(tween(200)) { fullHeight -> fullHeight },
            exit = slideOutVertically(tween(200)) { fullHeight -> fullHeight },
        ) {
            MiniPlayerContent()
        }
    }
}

@Composable
private fun MiniPlayerContent() {
    val rotateAngle by remember {
        mutableStateOf(Animatable(0f))
    }

    // 当前正在播放的歌曲信息
    val currentSongDetail =
        LevonsPlayerController.originPlaylist[LevonsPlayerController.currentOriginIndex]

    // 控制封面图旋转动画
    LaunchedEffect(LevonsPlayerController.isPlaying) {
        if (LevonsPlayerController.isPlaying) {
            rotateAngle.animateTo(
                targetValue = rotateAngle.value + 360f,
                infiniteRepeatable(
                    tween(durationMillis = 8000, easing = LinearEasing),
                    RepeatMode.Restart
                )
            )
        } else {
            rotateAngle.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MiniPlayerHeight)
            .background(LocalColors.current.bottomMusicPlayBarBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 42.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 旋转封面图
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .offset(y = (-18).dp),
                contentAlignment = Alignment.Center
            ) {
                AssetImage(R.drawable.ic_disc, modifier = Modifier.fillMaxSize())
                NetworkImage(
                    currentSongDetail.al.picUrl,
                    placeholder = R.drawable.ic_default_disk_cover,
                    error = R.drawable.ic_default_disk_cover,
                    modifier = Modifier
                        .size(71.dp)
                        .rotate(rotateAngle.value)
                        .clip(CircleShape)
                )
            }

            // 歌曲名称
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = LocalColors.current.firstText,
                            fontSize = 30.sp
                        )
                    ) {
                        append(currentSongDetail.name)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = LocalColors.current.secondText,
                            fontSize = 24.sp
                        )
                    ) {
                        append(" - ${currentSongDetail.ar[0].name}")
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 22.dp, end = 32.dp, bottom = 8.dp)
            )

            // 播放、暂停按钮
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(75.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (LevonsPlayerController.isPlaying) {
                            LevonsPlayerController.pause()
                        } else {
                            LevonsPlayerController.resume()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AssetIcon(
                    if (LevonsPlayerController.isPlaying) R.drawable.ic_pause_without_circle
                    else R.drawable.ic_play_without_circle,
                    modifier = Modifier.size(30.dp),
                    tint = Color.Gray
                )
                CircleProgress(
                    progress = LevonsPlayerController.progress,
                    modifier = Modifier.size(58.dp)
                )
            }

            // 歌单按钮
            AssetIcon(
                R.drawable.ic_play_list,
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .padding(12.dp),
                tint = Color.Gray,
            )

        }
    }
}