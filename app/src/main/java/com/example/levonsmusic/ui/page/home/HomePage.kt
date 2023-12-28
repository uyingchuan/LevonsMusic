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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.component.HeartbeatLoading
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.ui.page.community.CommunityPage
import com.example.levonsmusic.ui.page.discovery.DiscoveryPage
import com.example.levonsmusic.ui.page.event.EventPage
import com.example.levonsmusic.ui.page.home.component.MiniPlayer
import com.example.levonsmusic.ui.page.home.component.MiniPlayerHeight
import com.example.levonsmusic.ui.page.home.component.PlaylistBottomSheet
import com.example.levonsmusic.ui.page.home.component.TabMenu
import com.example.levonsmusic.ui.page.mine.MineDrawer
import com.example.levonsmusic.ui.page.mine.MinePage
import com.example.levonsmusic.ui.page.podcast.PodcastPage
import com.example.levonsmusic.ui.theme.LocalColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val viewModel: HomeViewModel = hiltViewModel()

    if (viewModel.heartbeatLoading) {
        HeartbeatLoading {}
    }

    ModalNavigationDrawer(
        drawerContent = { MineDrawer(drawerState) }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalColors.current.background)
            ) {
                Body(drawerState)
                MiniPlayer()
                PlaylistBottomSheet()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Body(drawerState: DrawerState) {
    val viewModel: HomeViewModel = hiltViewModel()

    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(
            initialPage = viewModel.selectedHomeTabIndex,
            pageCount = { viewModel.tabMenuItems.size }
        )

        val padding = if (LevonsPlayerController.showMiniPlayer) MiniPlayerHeight else 0.dp

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(bottom = padding),
            state = pagerState
        ) { pagePosition ->
            viewModel.selectedHomeTabIndex = pagerState.currentPage
            when (pagePosition) {
                0 -> DiscoveryPage()
                1 -> PodcastPage()
                2 -> MinePage(drawerState)
                3 -> EventPage()
                4 -> CommunityPage()
            }
        }

        TabMenu(
            viewModel.tabMenuItems,
            pagerState,
            viewModel.selectedHomeTabIndex,
        ) {
            viewModel.selectedHomeTabIndex = it
        }
    }
}