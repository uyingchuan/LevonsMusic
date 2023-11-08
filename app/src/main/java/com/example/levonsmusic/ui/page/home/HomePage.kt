package com.example.levonsmusic.ui.page.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.levonsmusic.R
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.ui.page.community.CommunityPage
import com.example.levonsmusic.ui.page.discovery.DiscoveryPage
import com.example.levonsmusic.ui.page.event.EventPage
import com.example.levonsmusic.ui.page.home.component.MiniPlayer
import com.example.levonsmusic.ui.page.home.component.MiniPlayerHeight
import com.example.levonsmusic.ui.page.home.component.TabMenu
import com.example.levonsmusic.ui.page.home.component.TabMenuItem
import com.example.levonsmusic.ui.page.mine.MinePage
import com.example.levonsmusic.ui.page.podcast.PodcastPage
import com.example.levonsmusic.ui.theme.LocalColors

private val tabMenuItems = listOf(
    TabMenuItem("发现", R.drawable.ic_discovery),
    TabMenuItem("播客", R.drawable.ic_podcast),
    TabMenuItem("我的", R.drawable.ic_mine),
    TabMenuItem("k歌", R.drawable.ic_sing),
    TabMenuItem("云村", R.drawable.ic_cloud_country),
)


var selectedHomeTabIndex by mutableIntStateOf(2)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColors.current.background)
        ) {
            Body()
            MiniPlayer()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Body() {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(
            initialPage = selectedHomeTabIndex,
            pageCount = { tabMenuItems.size }
        )

        val padding = if (LevonsPlayerController.showMiniPlayer) MiniPlayerHeight else 0.dp

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(bottom = padding),
            state = pagerState
        ) { pagePosition ->
            selectedHomeTabIndex = pagerState.currentPage
            when (pagePosition) {
                0 -> DiscoveryPage()
                1 -> PodcastPage()
                2 -> MinePage()
                3 -> EventPage()
                4 -> CommunityPage()
            }
        }

        TabMenu(
            tabMenuItems,
            pagerState,
            selectedHomeTabIndex,
        ) {
            selectedHomeTabIndex = it
        }
    }
}