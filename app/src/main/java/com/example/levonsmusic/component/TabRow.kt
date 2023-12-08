package com.example.levonsmusic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.sp
import com.example.levonsmusic.ui.theme.LocalColors

@Composable
fun TabRowComponent(
    tabs: List<String>,
    selectedIndex: Int = 0,
    containerColor: Color = LocalColors.current.background,
    textColor: Color = LocalColors.current.firstText,
    selectedTextColor: Color = LocalColors.current.secondText,
    indicatorColor: Brush = Brush.horizontalGradient(
        listOf(LocalColors.current.primary, LocalColors.current.secondary)
    ),
    style: TabRowStyle,
    onSelected: ((index: Int) -> Unit)? = null,
) {
    if (style.isScrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 0.dp,
            containerColor = containerColor,
            indicator = {
                if (style.indicatorBuilder != null) {
                    style.indicatorBuilder.invoke(it[selectedIndex], selectedIndex)
                } else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(it[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Divider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPadding)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent,
                        )
                    }
                }
            },
            divider = {
                Divider(color = Color.Transparent)
            }
        ) {
            tabs.forEachIndexed { index, text ->
                Box(
                    modifier = style.modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onSelected?.invoke(index)
                                }
                            )
                        }
                        .drawBehind {
                            style.tabDrawBehindBlock?.invoke(this, index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val isSelected = selectedIndex == index
                    Text(
                        text = text,
                        fontSize = if (isSelected) style.selectedTextSiz else style.textSize,
                        fontWeight = if (isSelected) style.selectedFontWeight else style.fontWeight,
                        color = if (isSelected) selectedTextColor else textColor,
                        textAlign = TextAlign.Center,
                    )
                }

            }
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = containerColor,
            indicator = {
                if (style.indicatorBuilder != null) {
                    style.indicatorBuilder.invoke(it[selectedIndex], selectedIndex)
                } else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(it[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Divider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPadding)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent,
                        )
                    }
                }
            },
            divider = {
                Divider(color = Color.Transparent)
            }
        ) {
            tabs.forEachIndexed { index, text ->
                Box(
                    modifier = style.modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onSelected?.invoke(index)
                                }
                            )
                        }
                        .drawBehind {
                            style.tabDrawBehindBlock?.invoke(this, index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val isSelected = selectedIndex == index
                    Text(
                        text = text,
                        fontSize = if (isSelected) style.selectedTextSiz else style.textSize,
                        fontWeight = if (isSelected) style.selectedFontWeight else style.fontWeight,
                        color = if (isSelected) selectedTextColor else textColor,
                        textAlign = TextAlign.Center,
                    )
                }

            }
        }
    }
}

data class TabRowStyle(
    val modifier: Modifier = Modifier,
    val selectedTextSiz: TextUnit = 36.sp,
    val textSize: TextUnit = 36.sp,
    val selectedFontWeight: FontWeight = FontWeight.Bold,
    val fontWeight: FontWeight = FontWeight.Normal,
    val indicatorWidth: Dp = 142.dp,
    val indicatorHeight: Dp = 12.dp,
    val indicatorPadding: Dp = 0.dp,
    val isScrollable: Boolean = false,
    val tabDrawBehindBlock: (DrawScope.(position: Int) -> Unit)? = null,
    val indicatorBuilder: ((tabPosition: TabPosition, selectedIndex: Int) -> Unit)? = null,
)
