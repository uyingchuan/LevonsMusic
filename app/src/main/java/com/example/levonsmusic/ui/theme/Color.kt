package com.example.levonsmusic.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

val lightScheme = AppColors(
    statusBar = Color(0xFFEEEEEE),
    pure = Color(0xFFFFFFFF),
    primary = Color(0xFFF0484E),
    primaryVariant = Color(0xFFEC3037),
    secondary = Color(0xFFF0888C),
    background = Color(0xFFEEEEEE),
    firstText = Color(0xFF333333),
    secondText = Color(0xFF666666),
    thirdText = Color(0xFF999999),
    firstIcon = Color(0xFF333333),
    secondIcon = Color(0xFF666666),
    thirdIcon = Color(0xFF999999),
    appBarBackground = Color(0xFFEEEEEE),
    appBarContent = Color(0xFF333333),
    card = Color(0xFFFFFFFF),
    bottomMusicPlayBarBackground = Color(0xFFF5F3F3),
    divider = Color(0xFFDDDDDD),
)

val darkScheme = AppColors(
    statusBar = Color(0xFF222222),
    pure = Color(0xFF000000),
    primary = Color(0xFFF0484E),
    primaryVariant = Color(0xFFEC3037),
    secondary = Color(0xFFF0888C),
    background = Color(0xFF222222),
    firstText = Color(0xFFFFFFFF),
    secondText = Color(0xFFBBBBBB),
    thirdText = Color(0xFF999999),
    firstIcon = Color(0xFFFFFFFF),
    secondIcon = Color(0xFFBBBBBB),
    thirdIcon = Color(0xFF999999),
    appBarBackground = Color(0xFF222222),
    appBarContent = Color(0xFFFFFFFF),
    card = Color(0xFF333333),
    bottomMusicPlayBarBackground = Color(0xFF2C2C2C),
    divider = Color(0xFF555555),
)

@Stable
class AppColors(
    statusBar: Color,
    pure: Color,
    primary: Color,
    primaryVariant: Color,
    secondary: Color,
    background: Color,
    firstText: Color,
    secondText: Color,
    thirdText: Color,
    firstIcon: Color,
    secondIcon: Color,
    thirdIcon: Color,
    appBarBackground: Color,
    appBarContent: Color,
    card: Color,
    bottomMusicPlayBarBackground: Color,
    divider: Color
) {
    val statusBar: Color by mutableStateOf(statusBar)
    val pure: Color by mutableStateOf(pure)
    val primary: Color by mutableStateOf(primary)
    val primaryVariant: Color by mutableStateOf(primaryVariant)
    val secondary: Color by mutableStateOf(secondary)
    val background: Color by mutableStateOf(background)
    val firstText: Color by mutableStateOf(firstText)
    val secondText: Color by mutableStateOf(secondText)
    val thirdText: Color by mutableStateOf(thirdText)
    val firstIcon: Color by mutableStateOf(firstIcon)
    val secondIcon: Color by mutableStateOf(secondIcon)
    val thirdIcon: Color by mutableStateOf(thirdIcon)
    val appBarBackground: Color by mutableStateOf(appBarBackground)
    val appBarContent: Color by mutableStateOf(appBarContent)
    val card: Color by mutableStateOf(card)
    val bottomMusicPlayBarBackground: Color by mutableStateOf(bottomMusicPlayBarBackground)
    val divider: Color by mutableStateOf(divider)
}