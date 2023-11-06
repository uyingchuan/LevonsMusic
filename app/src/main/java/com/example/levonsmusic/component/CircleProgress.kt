package com.example.levonsmusic.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.levonsmusic.ui.theme.LocalColors
import kotlin.math.min

@Composable
fun CircleProgress(progress: Int, modifier: Modifier = Modifier) {
    val sweepAngle = progress / 100f * 360
    val activeColor = LocalColors.current.primary

    Canvas(modifier = modifier) {
        val size = min(size.width, size.height)
        drawCircle(color = Color.LightGray, radius = size / 2, style = Stroke(width = 4f))
        drawArc(
            color = activeColor,
            style = Stroke(width = 4f),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false
        )
    }
}