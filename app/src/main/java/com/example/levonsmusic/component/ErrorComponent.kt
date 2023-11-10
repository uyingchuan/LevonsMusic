package com.example.levonsmusic.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.levonsmusic.R
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun ErrorComponent(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.ic_empty,
    message: String? = null,
    reloadDataBlock: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.clickable {
            reloadDataBlock?.invoke()
        }
    ) {
        Column {
            AssetIcon(
                resId = iconResId,
                modifier = Modifier.size(100.dp),
                tint = LocalColors.current.primary
            )
            Text(
                message ?: "加载失败",
                fontSize = 14.sp,
                color = LocalColors.current.thirdText,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}