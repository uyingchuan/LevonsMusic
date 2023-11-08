package com.example.levonsmusic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.example.levonsmusic.R
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.pxToDp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leftIcon: Int = R.drawable.ic_back,
    rightIcon: Int? = null,
    dividerVisible: Boolean = false,
    background: Color = LocalColors.current.appBarBackground,
    contentColor: Color = LocalColors.current.appBarContent,
) {
    var leftWidth by remember {
        mutableIntStateOf(0)
    }

    var rightWidth by remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .background(background)
            .fillMaxWidth()
            .height(88.dp)
            .zIndex(1f),
    ) {
        Box(modifier = modifier) {
            // 左侧按钮
            AssetIcon(
                resId = leftIcon,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(50))
                    .padding(20.dp)
                    .size(48.dp)
                    .onGloballyPositioned {
                        leftWidth = it.size.width
                    },
                tint = contentColor
            )

            // 右侧按钮
            if (rightIcon != null) {
                AssetIcon(
                    resId = rightIcon,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(50))
                        .padding(20.dp)
                        .size(48.dp)
                        .onGloballyPositioned {
                            rightWidth = it.size.width
                        },
                    tint = contentColor
                )
            }

            // calc title position
            val padding = maxOf(leftWidth, rightWidth)

            // title
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(horizontal = padding.pxToDp + 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title ?: "",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // divider
            if (dividerVisible) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color(0xFFE5E5E5),
                    thickness = 1.dp
                )

            }
        }
    }
}