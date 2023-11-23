package com.example.levonsmusic.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * [enableRipple] 开启水波纹
 * [rippleColor] 水波纹颜色
 */
@Composable
fun Modifier.onClick(
    enableRipple: Boolean = true,
    rippleColor: Color = Color.Unspecified,
    onClick: () -> Unit,
) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = if (enableRipple) rememberRipple(color = rippleColor, bounded = true) else null
) {
    onClick.invoke()
}