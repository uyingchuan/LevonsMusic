package com.example.levonsmusic.ui.page.playlist

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.R
import com.example.levonsmusic.component.AppBar
import com.example.levonsmusic.component.AssetIcon
import com.example.levonsmusic.component.BaseComponent
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.onClick
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.extension.toPx
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.ui.page.login.LoginAccount
import com.example.levonsmusic.ui.theme.LocalColors
import com.example.levonsmusic.util.HeaderBackgroundShape
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun PlaylistPage(playlistBean: PlaylistBean) {
    val viewModel: PlaylistViewModel = hiltViewModel()
    viewModel.playlistBean = playlistBean

    val state = rememberCollapsingToolbarScaffoldState()
    val density = LocalDensity.current
    val statusTop = WindowInsets.statusBars.getTop(density)
    val showPlaylistTitle =
        (1 - state.toolbarState.progress) >= (statusTop + 188.dp.toPx) / 584.dp.toPx
    Box {
        CollapsingToolbarScaffold(
            modifier = Modifier.fillMaxSize(),
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                ScrollHeader(playlistBean, state, if (showPlaylistTitle) playlistBean.name else "")
            }
        ) {
            Body()
        }
    }
}

@Composable
private fun CollapsingToolbarScope.ScrollHeader(
    playlistBean: PlaylistBean,
    toolbarState: CollapsingToolbarScaffoldState,
    title: String
) {
    val trigger = remember { 1 - (584f - 88) / 584f }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(584.dp)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(HeaderBackgroundShape(toolbarState.toolbarState.progress * 80f))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.Gray.copy(0.7f),
                            Color.LightGray.copy(0.7f),
                            Color.Gray.copy(0.7f)
                        )
                    )
                )
        ) {
            HeaderBackground(playlistBean)
            HeaderPlaylistInfo(playlistBean, toolbarState)
        }
        var alphaValue = toolbarState.toolbarState.progress
        if (alphaValue < trigger) {
            alphaValue = toolbarState.toolbarState.progress
        }
        HeaderCountInfo(playlistBean, alphaValue)
    }

    AppBar(
        title = title,
        background = Color.Transparent,
        contentColor = Color.White,
        rightIcon = R.drawable.ic_search,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(88.dp)
    )
}

@Composable
private fun BoxScope.HeaderCountInfo(playlistBean: PlaylistBean, alpha: Float) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .graphicsLayer {
                this.alpha = alpha
            }
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 32.dp)
            .padding(bottom = 4.dp)
            .shadow(2.dp, RoundedCornerShape(50))
            .background(LocalColors.current.card),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .onClick { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AssetIcon(
                resId = R.drawable.ic_action_play,
                tint = LocalColors.current.firstIcon,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "播放(${playlistBean.playCount})",
                fontSize = 24.sp,
                color = LocalColors.current.firstText
            )
        }

        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .height(40.dp)
                .width(2.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .onClick { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AssetIcon(
                resId = R.drawable.ic_comment_count,
                tint = LocalColors.current.firstIcon,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "评论(${playlistBean.commentCount})",
                fontSize = 24.sp,
                color = LocalColors.current.firstText
            )
        }

        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .height(40.dp)
                .width(2.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .onClick { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AssetIcon(
                resId = R.drawable.ic_share,
                tint = LocalColors.current.firstIcon,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "转发(${playlistBean.shareCount})",
                fontSize = 24.sp,
                color = LocalColors.current.firstText
            )
        }
    }
}

@Composable
private fun HeaderPlaylistInfo(
    playlistBean: PlaylistBean,
    toolbarState: CollapsingToolbarScaffoldState
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 132.dp)
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .graphicsLayer { alpha = toolbarState.toolbarState.progress },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NetworkImage(
            url = playlistBean.coverImgUrl,
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Column(
            modifier = Modifier
                .padding(start = 32.dp)
                .weight(1f)
                .height(240.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = playlistBean.name,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                fontSize = 28.sp,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.height(88.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                NetworkImage(
                    url = LoginAccount.data!!.profile.avatarUrl,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                )
                Text(
                    text = LoginAccount.data!!.profile.nickname,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            Text(
                text = playlistBean.description ?: "暂无描述",
                color = Color.White,
                fontSize = 28.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HeaderBackground(playlistBean: PlaylistBean) {
    NetworkImage(
        url = playlistBean.coverImgUrl,
        placeholder = -1,
        error = -1,
        modifier = Modifier
            .fillMaxWidth()
            .height(584.dp)
            .graphicsLayer { alpha = 0.1f },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body() {
    val viewModel: PlaylistViewModel = hiltViewModel()

    BaseComponent(
        liveData = viewModel.liveData,
        loadDataBlock = { viewModel.getPlaylist() }
    ) {
        CompositionLocalProvider(LocalOverscrollConfiguration.provides(null)) {
            Column {
                PlaylistHeader(viewModel.playlistBean)
                Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
                LazyColumn(Modifier.padding(bottom = 0.dp)) {
                    itemsIndexed(viewModel.songList) { index, item ->
                        SongItem(index, item) {
                            LevonsPlayerController.startPlaylist(viewModel.songList, index)
                            LevonsPlayerController.showMiniPlayer = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistHeader(playlistBean: PlaylistBean) {
    val viewModel: PlaylistViewModel = hiltViewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .onClick {
                LevonsPlayerController.startPlaylist(viewModel.songList, 0)
                LevonsPlayerController.showMiniPlayer = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssetIcon(
            resId = R.drawable.ic_play_list_header_play, tint = LocalColors.current.primary,
            modifier = Modifier
                .padding(32.dp)
                .size(50.dp)
        )

        Text(
            text = "播放全部",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = LocalColors.current.firstText
        )
        Text(
            text = "(${playlistBean.trackCount})",
            fontSize = 28.sp,
            color = LocalColors.current.secondText
        )
    }
}

@Composable
fun SongItem(index: Int, songDetail: SongDetail, onClick: () -> Unit) {
    Row(
        modifier = Modifier.onClick {
            onClick.invoke()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (LevonsPlayerController.checkIsSongPlaying(songDetail)) {
            SongItemInPlaying(
                playing = LevonsPlayerController.checkIsSongPlaying(songDetail),
                modifier = Modifier.width(120.dp)
            )
        } else {
            Text(
                text = (index + 1).toString(),
                fontSize = 30.sp,
                color = LocalColors.current.secondText,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(120.dp)
            )
        }

        Column(
            Modifier
                .padding(vertical = 26.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = songDetail.name,
                fontSize = 32.sp,
                color = LocalColors.current.firstText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = songDetail.ar.getOrNull(0)?.name ?: "未知",
                fontSize = 24.sp,
                color = LocalColors.current.secondText,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        AssetIcon(
            resId = R.drawable.ic_sheet_menu,
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun SongItemInPlaying(
    modifier: Modifier = Modifier,
    playing: Boolean = false,
) {
    val color = LocalColors.current.primary
    val animation by remember {
        mutableStateOf(Animatable(0.4f))
    }

    LaunchedEffect(playing) {
        if (playing) {
            animation.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            animation.stop()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
        ) {
            val rectWidth = size.width / 5
            val canvasHeight = size.height

            val rectHeight1 = if (playing) {
                canvasHeight * (0.75f - animation.value * 0.75f + 0.25f)
            } else {
                canvasHeight * 0.7f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(rectWidth / 2),
                topLeft = Offset(0f, canvasHeight - rectHeight1),
                size = Size(rectWidth, rectHeight1)
            )

            val rectHeight2 = if (playing) {
                canvasHeight * (animation.value * 0.65f + 0.2f)
            } else {
                canvasHeight * 0.9f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(rectWidth / 2),
                topLeft = Offset(rectWidth * 2, canvasHeight - rectHeight2),
                size = Size(rectWidth, rectHeight2)
            )

            val rectHeight3 = if (playing) {
                canvasHeight * (0.6f - animation.value * 0.6f + 0.4f)
            } else {
                canvasHeight * 0.5f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(rectWidth / 2),
                topLeft = Offset(rectWidth * 4, canvasHeight - rectHeight3),
                size = Size(rectWidth, rectHeight3)
            )
        }
    }
}
