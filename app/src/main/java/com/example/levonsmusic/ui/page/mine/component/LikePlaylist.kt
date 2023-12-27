package com.example.levonsmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.AppNav
import com.example.levonsmusic.R
import com.example.levonsmusic.ScreenPaths
import com.example.levonsmusic.component.AssetIcon
import com.example.levonsmusic.component.HeartbeatLoading
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.ui.page.mine.MineViewModel
import com.example.levonsmusic.ui.theme.LocalColors
import kotlinx.coroutines.launch

@Composable
fun LikePlaylist(playlist: PlaylistBean, verticalPadding: Dp = 32.dp) {
    val viewModel: MineViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var loading by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .background(LocalColors.current.card)
            .fillMaxWidth()
            .clickable {
                val destination = AppNav.instance.graph.findNode(ScreenPaths.playlist)
                val bundle = bundleOf(Pair("playlistBean", playlist))
                AppNav.instance.navigate(destination!!.id, bundle)
            }
            .padding(horizontal = 32.dp, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            HeartbeatLoading {

            }
        }

        NetworkImage(
            url = playlist.coverImgUrl,
            modifier = Modifier
                .padding(end = 20.dp)
                .size(110.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = playlist.name,
                fontSize = 30.sp,
                color = LocalColors.current.firstText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "共${playlist.trackCount}首",
                fontSize = 24.sp,
                color = LocalColors.current.secondText,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    viewModel.playHeartBeatMode()
                    loading = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalColors.current.background,
            ),
            modifier = Modifier
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            content = {
                AssetIcon(
                    R.drawable.ic_play_mode_heartbeat,
                    modifier = Modifier.size(32.dp),
                    tint = LocalColors.current.primary
                )
                Text(text = " 心动模式", fontSize = 24.sp, color = LocalColors.current.secondText)
            }
        )
    }
}