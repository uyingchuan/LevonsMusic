package com.example.levonsmusic.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.levonsmusic.R
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier,
    loading: Boolean = true,
    width: Dp = 60.dp,
    height: Dp = 50.dp,
    radius: Boolean = true,
    color: Color = LocalColors.current.primary,
    alignment: Alignment = Alignment.Center,
) {
    val animation by remember {
        mutableStateOf(Animatable(0.4f))
    }

    LaunchedEffect(loading) {
        if (loading) {
            animation.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 450, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                )
            )
        } else {
            animation.stop()
        }
    }

    Box(modifier.fillMaxSize(), alignment) {
        Canvas(
            modifier = Modifier
                .width(width)
                .height(height)
        ) {
            val rectWidth = size.width / 7
            val canvasHeight = size.height

            val rectHeight1 = if (loading) {
                canvasHeight * (0.75f - animation.value * 0.75f + 0.25f)
            } else {
                canvasHeight * 0.7f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(if (radius) rectWidth / 2 else 0f),
                topLeft = Offset(0f, canvasHeight - rectHeight1),
                size = Size(rectWidth, rectHeight1),
            )

            val rectHeight2 = if (loading) {
                canvasHeight * (animation.value * 0.65f + 0.2f)
            } else {
                canvasHeight * 0.52f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(if (radius) rectWidth / 2 else 0f),
                topLeft = Offset(rectWidth * 2, canvasHeight - rectHeight2),
                size = Size(rectWidth, rectHeight2)
            )

            val rectHeight3 = if (loading) {
                canvasHeight * (0.6f - animation.value * 0.6f + 0.4f)
            } else {
                canvasHeight * 0.43f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(if (radius) rectWidth / 2 else 0f),
                topLeft = Offset(rectWidth * 4, canvasHeight - rectHeight3),
                size = Size(rectWidth, rectHeight3)
            )

            val rectHeight4 = if (loading) {
                canvasHeight * (animation.value * 0.45f + 0.3f)
            } else {
                canvasHeight * 0.48f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(if (radius) rectWidth / 2 else 0f),
                topLeft = Offset(rectWidth * 6, canvasHeight - rectHeight4),
                size = Size(rectWidth, rectHeight4)
            )
        }
    }
}

@Composable
fun HeartbeatLoading(
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = Modifier
                .width(200.dp)
                .height(150.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.3f)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AssetImage(
                    R.drawable.heartbeat,
                    modifier = Modifier
                        .size(50.dp)
                )
                Text(
                    text = "正在打开心动模式...",
                    fontSize = 32.sp,
                    color = Color.White
                )
            }
        }
    }
}
