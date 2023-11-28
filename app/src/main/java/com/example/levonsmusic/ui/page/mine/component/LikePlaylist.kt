package com.example.levonsmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.levonsmusic.R
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun LikePlaylist(playlist: PlaylistBean, verticalPadding: Dp = 32.dp) {
    Row(
        modifier = Modifier
            .background(LocalColors.current.card)
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 32.dp, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Icon(
            painter = painterResource(id = R.drawable.ic_sheet_menu),
            contentDescription = "",
            modifier = Modifier
                .height(30.dp)
        )
    }
}