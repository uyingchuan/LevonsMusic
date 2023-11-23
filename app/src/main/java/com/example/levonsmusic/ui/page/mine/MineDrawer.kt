package com.example.levonsmusic.ui.page.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.levonsmusic.ui.theme.LocalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MineDrawer(drawerState: DrawerState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColors.current.background),
    ) {
    }
}