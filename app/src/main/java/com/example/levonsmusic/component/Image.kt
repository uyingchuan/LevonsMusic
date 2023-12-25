package com.example.levonsmusic.component

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.example.levonsmusic.R
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun AssetImage(
    resId: Int,
    modifier: Modifier = Modifier,
    allowHardware: Boolean = false,
    colorFilter: ColorFilter? = null
) {
    Image(
        rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(resId)
                .allowHardware(allowHardware)
                .build()
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        colorFilter = colorFilter
    )
}

@Composable
fun NetworkImage(
    url: Any?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.ic_default_place_holder,
    error: Int = R.drawable.ic_default_place_holder,
    allowHardware: Boolean = false,
    transformations: List<Transformation> = emptyList(),
    colorFilter: ColorFilter? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val modelBuilder = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .crossfade(false)
        .allowHardware(allowHardware)
        .transformations(transformations)

    if (placeholder != -1) {
        modelBuilder.placeholder(placeholder)
    }
    if (error != -1) {
        modelBuilder.error(error)
    }

    Image(
        painter = rememberAsyncImagePainter(model = modelBuilder.build()),
        contentDescription = null,
        modifier = modifier,
        colorFilter = colorFilter,
        contentScale = contentScale
    )
}

@Composable
fun AssetIcon(
    resId: Int,
    modifier: Modifier = Modifier,
    tint: Color = LocalColors.current.firstIcon
) {
    Icon(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}
