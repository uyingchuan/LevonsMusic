package com.example.levonsmusic.ui.page.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levonsmusic.ui.theme.LocalColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabMenu(
    items: List<TabMenuItem>,
    pagerState: PagerState,
    selectedIndex: Int,
    onChange: ((selected: Int) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    NavigationBar(
        modifier = Modifier
            .shadow(4.dp)
            .background(Color.Transparent),
        containerColor = LocalColors.current.pure
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                modifier = Modifier.background(LocalColors.current.pure),
                selected = selectedIndex == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                        onChange?.invoke(index)
                    }
                },
                icon = {
                    Icon(
                        painterResource(item.icon),
                        item.label,
                        tint = if (selectedIndex == index) LocalColors.current.primary else Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        item.label,
                        color = if (selectedIndex == index) LocalColors.current.primary else Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}

data class TabMenuItem(val label: String, val icon: Int)