package com.example.levonsmusic.ui.page.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.levonsmusic.R
import com.example.levonsmusic.ui.page.home.component.TabMenuItem
import com.example.levonsmusic.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel() {
    var heartbeatLoading by mutableStateOf(false)
    var selectedHomeTabIndex by mutableIntStateOf(2)

    val tabMenuItems = listOf(
        TabMenuItem("发现", R.drawable.ic_discovery),
        TabMenuItem("播客", R.drawable.ic_podcast),
        TabMenuItem("我的", R.drawable.ic_mine),
        TabMenuItem("k歌", R.drawable.ic_sing),
        TabMenuItem("云村", R.drawable.ic_cloud_country),
    )
}