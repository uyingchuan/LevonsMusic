package com.example.levonsmusic.extension

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

const val APP_DESIGN_WIDTH = 750

val Number.dp
    get() = Dp(
        toFloat()
                * Resources.getSystem().displayMetrics.widthPixels
                / APP_DESIGN_WIDTH
                / Resources.getSystem().displayMetrics.density
    )

val Number.sp
    get() = (toFloat() *
            Resources.getSystem().displayMetrics.widthPixels
            / APP_DESIGN_WIDTH
            / Resources.getSystem().displayMetrics.scaledDensity).sp

val Dp.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value,
        Resources.getSystem().displayMetrics
    )
