package com.example.levonsmusic.ui.page.home.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.R
import com.example.levonsmusic.component.AssetIcon
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.onClick
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.player.MusicPlayerMode
import com.example.levonsmusic.ui.page.home.HomeViewModel
import com.example.levonsmusic.ui.page.playlist.SongItemInPlaying
import com.example.levonsmusic.ui.theme.LocalColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaylistBottomSheet() {
    if (!LevonsPlayerController.showPlaylistBottomSheet) return

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween<Float>(300),
        skipHalfExpanded = true,
        confirmValueChange = {
            scope.launch {
                delay(200)
                LevonsPlayerController.showPlaylistBottomSheet =
                    it == ModalBottomSheetValue.Expanded
            }
            true
        },
    )

    LaunchedEffect(LevonsPlayerController.showPlaylistBottomSheet) {
        if (LevonsPlayerController.showPlaylistBottomSheet) {
            sheetState.show()
        }
    }

    BackHandler(enabled = true) {
        scope.launch {
            sheetState.hide()
            LevonsPlayerController.showPlaylistBottomSheet = false
        }
    }

    /**
     * TODO 找找material3的实现吧
     */
    ModalBottomSheetLayout(
        sheetContent = { Content(scope) },
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent
    ) { }
}

@Composable
private fun Content(coroutineScope: CoroutineScope) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(LevonsPlayerController.showPlaylistBottomSheet) {
        if (LevonsPlayerController.showPlaylistBottomSheet) {
            lazyListState.animateScrollToItem(LevonsPlayerController.currentOriginIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(0.dp, 1080.dp)
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(LocalColors.current.pure)
            .padding(top = 48.dp)
    ) {
        PlaylistHeader(coroutineScope)

        LazyColumn(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            state = lazyListState
        ) {
            itemsIndexed(LevonsPlayerController.originPlaylist) { index, item ->
                PlaylistItem(index, item)
            }
        }
    }
}

@Composable
private fun PlaylistItem(index: Int, songDetail: SongDetail) {
    val isPlayingSong = LevonsPlayerController.checkIsSongPlaying(songDetail)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .onClick {
                LevonsPlayerController.playingOriginIndex(index)
            }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPlayingSong) {
            SongItemInPlaying(
                playing = LevonsPlayerController.isPlaying,
                modifier = Modifier.padding(end = 32.dp)
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (isPlayingSong) LocalColors.current.primary else LocalColors.current.firstText,
                        fontSize = 32.sp
                    )
                ) {
                    append(songDetail.name)
                }
                withStyle(
                    style = SpanStyle(
                        color = if (isPlayingSong) LocalColors.current.secondary else LocalColors.current.secondText,
                        fontSize = 24.sp
                    )
                ) {
                    append(" - ${songDetail.ar.getOrNull(0)?.name ?: "未知"}")
                }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (LevonsPlayerController.originPlaylist.size > 1) {
            AssetIcon(
                resId = R.drawable.ic_remove,
                modifier = Modifier
                    .size(80.dp)
                    .onClick {
                        LevonsPlayerController.removeOriginSongByIndex(index)
                    }
                    .padding(24.dp),
                tint = LocalColors.current.firstText,
            )
        }
    }
}

@Composable
private fun PlaylistHeader(coroutineScope: CoroutineScope) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "当前播放",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColors.current.firstText
            )
            Text(
                text = "(${LevonsPlayerController.originPlaylist.size})",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColors.current.secondText
            )
        }

        Row(
            modifier = Modifier
                .onClick {
                    coroutineScope.launch {
                        when (LevonsPlayerController.playMode) {
                            MusicPlayerMode.SINGLE -> LevonsPlayerController.changePlayMode(
                                MusicPlayerMode.RANDOM
                            )

                            MusicPlayerMode.RANDOM -> {
                                try {
                                    homeViewModel.heartbeatLoading = true
                                    LevonsPlayerController.changePlayMode(MusicPlayerMode.HEARTBEAT)
                                    homeViewModel.heartbeatLoading = false
                                } catch (e: Exception) {
                                    homeViewModel.heartbeatLoading = false
                                }
                            }

                            MusicPlayerMode.HEARTBEAT -> LevonsPlayerController.changePlayMode(
                                MusicPlayerMode.LOOP
                            )

                            MusicPlayerMode.LOOP -> LevonsPlayerController.changePlayMode(
                                MusicPlayerMode.SINGLE
                            )
                        }
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val playModeName = when (LevonsPlayerController.playMode) {
                MusicPlayerMode.SINGLE -> "单曲循环"
                MusicPlayerMode.RANDOM -> "随机播放"
                MusicPlayerMode.HEARTBEAT -> "心动模式"
                MusicPlayerMode.LOOP -> "列表循环"
            }
            val playModeIcon = when (LevonsPlayerController.playMode) {
                MusicPlayerMode.SINGLE -> R.drawable.ic_play_mode_single
                MusicPlayerMode.RANDOM -> R.drawable.ic_play_mode_random
                MusicPlayerMode.HEARTBEAT -> R.drawable.ic_play_mode_heartbeat
                MusicPlayerMode.LOOP -> R.drawable.ic_play_mode_loop
            }

            Text(
                text = playModeName,
                fontSize = 32.sp,
                color = LocalColors.current.firstText,
                modifier = Modifier.padding(end = 16.dp)
            )
            AssetIcon(
                resId = playModeIcon,
                modifier = Modifier.size(36.dp),
                tint = LocalColors.current.firstText
            )
        }
    }
}