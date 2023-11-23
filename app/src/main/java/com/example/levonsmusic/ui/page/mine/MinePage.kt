package com.example.levonsmusic.ui.page.mine

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.levonsmusic.R
import com.example.levonsmusic.component.AppBar
import com.example.levonsmusic.component.BaseComponent
import com.example.levonsmusic.component.DragState
import com.example.levonsmusic.component.DragStatus
import com.example.levonsmusic.component.DraggableHeaderLayout
import com.example.levonsmusic.component.NetworkImage
import com.example.levonsmusic.component.rememberDragState
import com.example.levonsmusic.extension.dp
import com.example.levonsmusic.extension.onClick
import com.example.levonsmusic.extension.pxToDp
import com.example.levonsmusic.extension.toPx
import com.example.levonsmusic.ui.page.login.LoginAccount
import com.example.levonsmusic.ui.page.mine.component.UserInfo
import com.example.levonsmusic.ui.theme.LocalColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.CollapsingToolbarState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

private val stickyTabLayoutHeight = 88.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MinePage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    val viewModel: MineViewModel = hiltViewModel()
    var bodyAlphaValue by remember { mutableFloatStateOf(1f) }
    val toolbarAlphaState = remember { mutableFloatStateOf(0f) }

    val lazyListState = rememberLazyListState()

    // 控制下拉滚动效果
    CompositionLocalProvider(LocalOverscrollConfiguration.provides(null)) {
        Box(modifier = Modifier.fillMaxSize()) {
            TopAppBar(toolbarAlphaState, drawerState)

            BaseComponent(
                liveData = viewModel.liveData,
                loadDataBlock = { viewModel.getPlaylist() }
            ) {
                Box {
                    val dragState = rememberDragState(viewModel.dragStatus)

                    DraggableHeaderLayout(
                        dragState,
                        triggerRadio = 0.24f,
                        maxDragRadio = 0.48f,
                        onOpened = {
                            viewModel.dragStatus = DragStatus.Opened
                        },
                        onExpandingComplete = {
                            viewModel.dragStatus = DragStatus.ExpandingComplete
                        },
                        onClosingComplete = {
                            viewModel.dragStatus = DragStatus.ClosingComplete
                        },
                        background = { alpha ->
                            bodyAlphaValue = alpha
                            HeaderBackground(alpha)
                        }
                    ) {
                        Body(toolbarAlphaState, lazyListState, dragState, 1 - bodyAlphaValue)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    toolbarAlphaState: MutableState<Float>,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()

    AppBar(
        modifier = Modifier
            .background(LocalColors.current.pure.copy(alpha = toolbarAlphaState.value))
            .zIndex(2f)
            .statusBarsPadding(),
        background = Color.Transparent,
        leftIcon = R.drawable.ic_drawer_toggle,
        leftClick = {
            scope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Body(
    toolbarAlphaState: MutableState<Float>,
    lazyListState: LazyListState,
    dragState: DragState,
    bodyAlphaValue: Float,
) {

    val toolbarScaffoldState = rememberCollapsingToolbarScaffoldState()

    // 计算顶部高度
    val density = LocalDensity.current
    val statusTop = WindowInsets.statusBars.getTop(density)
    val toolbarMaxHeight = remember {
        // 状态栏高度+标题栏高度+用户信息卡片高度+
        statusTop.pxToDp + stickyTabLayoutHeight + 300.dp + 320.dp
    }

    val stickyPositionTop = remember { statusTop + stickyTabLayoutHeight.toPx }

    // 计算顶部透明度
    var toolbarAlpha =
        (1 - toolbarScaffoldState.toolbarState.progress) / stickyPositionTop * toolbarMaxHeight.toPx
    if (toolbarAlpha > 1) toolbarAlpha = 1f
    toolbarAlphaState.value = toolbarAlpha

    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(if (dragState.offset > 0) Color.Transparent else LocalColors.current.background),
        state = toolbarScaffoldState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            ScrollHeader(bodyAlphaValue, toolbarMaxHeight)
        }
    ) {
        PlayList(bodyAlphaValue, lazyListState, toolbarScaffoldState.toolbarState)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalToolbarApi::class)
@Composable
private fun PlayList(
    bodyAlphaValue: Float,
    lazyListState: LazyListState,
    toolbarState: CollapsingToolbarState
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: MineViewModel = hiltViewModel()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
            }
            .graphicsLayer { alpha = bodyAlphaValue },
        state = lazyListState
    ) {
        // 吸顶效果
        stickyHeader {
            StickyHeader(lazyListState, coroutineScope, bodyAlphaValue, toolbarState)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1200.dp)
                    .background(Color.Green)
            ) {
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1200.dp)
                    .background(Color.Red)
            ) {
            }
        }
    }
}

@Composable
fun StickyHeader(
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    bodyAlphaValue: Float,
    toolbarState: CollapsingToolbarState
) {
    val viewModel: MineViewModel = hiltViewModel()

    Surface(color = Color.Transparent) {
        var background =
            if (toolbarState.progress > 0.001) LocalColors.current.background else LocalColors.current.pure
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(Color.Cyan)
        )
    }
}

@Composable
private fun CollapsingToolbarScope.ScrollHeader(bodyAlphaValue: Float, toolbarMaxHeight: Dp) {
    val viewModel: MineViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .parallax(1f)
            .height(toolbarMaxHeight)
            .verticalScroll(rememberScrollState())
    ) {
        UserInfo(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = stickyTabLayoutHeight)
                .onClick(enableRipple = false) {
                    viewModel.dragStatus = DragStatus.ExpandingComplete
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 20.dp)
                .background(LocalColors.current.card, RoundedCornerShape(24.dp))
                .height(300.dp)
                .graphicsLayer { alpha = bodyAlphaValue }
        )
    }

    val density = LocalDensity.current
    val statusTop = WindowInsets.statusBars.getTop(density)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(statusTop.pxToDp + stickyTabLayoutHeight)
    )
}

@Composable
private fun HeaderBackground(alphaValue: Float) {
    NetworkImage(
        url = LoginAccount.data!!.profile.backgroundUrl,
        modifier = Modifier
            .fillMaxWidth()
            .height(584.dp)
            .graphicsLayer {
                alpha = alphaValue
            },
        error = R.drawable.ic_bg,
        contentScale = ContentScale.FillBounds
    )
}