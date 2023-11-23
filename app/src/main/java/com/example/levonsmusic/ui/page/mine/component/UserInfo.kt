package com.example.levonsmusic.ui.page.mine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.levonsmusic.R
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.ui.page.login.LoginAccount
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun UserInfo(modifier: Modifier = Modifier) {
    val profile = LoginAccount.data!!.profile

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(top = 60.dp, start = 32.dp, end = 32.dp)
                .height(240.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(LocalColors.current.card),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = profile.nickname,
                fontSize = 40.sp,
                color = LocalColors.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 64.dp)
            )
            Text(
                text = "${profile.follows} 关注  " +
                        "｜  ${profile.followeds} 粉丝",
                fontSize = 32.sp,
                color = LocalColors.current.secondText,
                modifier = Modifier.padding(top = 36.dp)
            )
        }

        NetworkImage(
            url = profile.avatarUrl,
            placeholder = R.drawable.ic_default_avator,
            error = R.drawable.ic_default_avator,
            modifier = Modifier
                .size(120.dp)
                .clip(
                    RoundedCornerShape(50)
                )
        )
    }
}