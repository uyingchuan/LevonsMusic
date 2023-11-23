package com.example.levonsmusic.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun DraggableHeaderLayout(
    state: DragState,
    triggerRadio: Float = 0.6f,
    maxDragRadio: Float = 1f,
    dragEnabled: Boolean = true,
    onOpened: () -> Unit,
    onExpandingComplete: () -> Unit,
    onClosingComplete: () -> Unit,
    background: @Composable (alpha: Float) -> Unit,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var backgroundHeight by remember {
        mutableIntStateOf(1)
    }
    var backgroundAlpha = remember { 1f }
    val openTriggerPx = backgroundHeight * triggerRadio
    val maxDrag = backgroundHeight * maxDragRadio

    if (!state.isDragging && maxDrag == state.offset && state.isExpandingComplete()) {
        onOpened()
    }

    val updateOnClosingComplete = rememberUpdatedState(onClosingComplete)
    val updateOnExpandingComplete = rememberUpdatedState(onExpandingComplete)
    val nestedScrollConnection = remember(state, coroutineScope) {
        DragNestedScrollConnection(
            state,
            coroutineScope,
            updateOnExpandingComplete.value,
            updateOnClosingComplete.value
        )
    }.apply {
        this.enabled = dragEnabled
        this.openTrigger = openTriggerPx
    }

    LaunchedEffect(key1 = state.isDragging, key2 = state.dragStatus) {
        if (!state.isDragging) {
            when (state.dragStatus) {
                DragStatus.ExpandingComplete -> {
                    state.animateOffsetTo(maxDrag)
                }

                DragStatus.Idle -> {
                    state.animateOffsetTo(0f)
                }

                DragStatus.Opened -> {
                    state.animateOffsetTo(0f)
                }

                else -> {}
            }
        }
    }

    Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        Box(modifier = Modifier.onGloballyPositioned {
            backgroundHeight = it.size.height
        }) {
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                if (state.offset >= 0) {
                    var alpha = state.offset / maxDrag
                    if (alpha > 1f) {
                        alpha = 1f
                    }
                    backgroundAlpha = alpha
                }
                background(backgroundAlpha)
            }
        }
        Box(modifier = Modifier.offset {
            IntOffset(0, state.offset.toInt().coerceAtMost(maxDrag.toInt()))
        }) {
            content()
        }
    }
}

@Composable
fun rememberDragState(dragStatus: DragStatus): DragState {
    return remember {
        DragState(dragStatus)
    }.apply {
        this.dragStatus = dragStatus
    }
}

sealed class DragStatus {
    object Idle : DragStatus()
    object Opened : DragStatus()
    object ExpandingComplete : DragStatus()
    object ClosingComplete : DragStatus()
}

@Stable
class DragState(dragStatus: DragStatus) {
    var dragStatus: DragStatus by mutableStateOf(dragStatus)
    var isDragging: Boolean by mutableStateOf(false)

    private val mutatorMutex = MutatorMutex()
    private val _offset = Animatable(0f)
    val offset get() = _offset.value

    internal suspend fun animateOffsetTo(offset: Float) {
        mutatorMutex.mutate {
            _offset.animateTo(offset, tween(300))
        }
    }

    internal suspend fun dispatchScrollDelta(delta: Float) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _offset.snapTo(_offset.value + delta)
        }
    }

    fun isIdle() = dragStatus == DragStatus.Idle
    fun isOpened() = dragStatus == DragStatus.Opened
    fun isExpandingComplete() = dragStatus == DragStatus.ExpandingComplete
    fun isClosingComplete() = dragStatus == DragStatus.ClosingComplete
}

private const val DRAG_MULTIPLIER = 0.6f

private class DragNestedScrollConnection(
    private val state: DragState,
    private val coroutineScope: CoroutineScope,
    private val onExpandingComplete: () -> Unit,
    private val onClosingComplete: () -> Unit
) : NestedScrollConnection {
    var openTrigger = 0f
    var enabled = false

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (!state.isExpandingComplete() && state.offset >= openTrigger) {
            onExpandingComplete()
        }
        state.isDragging = false
        return Velocity.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero
        source == NestedScrollSource.Drag && available.y > 0 -> onScroll(available)
        else -> Offset.Zero
    }

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero
        source == NestedScrollSource.Drag && available.y < 0 -> onScroll(available)
        else -> Offset.Zero
    }

    private fun onScroll(available: Offset): Offset {
        state.isDragging = true

        val newOffset = (available.y * DRAG_MULTIPLIER + state.offset).coerceAtLeast(0f)
        val dragConsumed = newOffset - state.offset
        return if (dragConsumed.absoluteValue >= 0.5f) {
            if (!state.isClosingComplete() && state.offset >= openTrigger) {
                onClosingComplete()
            }
            coroutineScope.launch {
                state.dispatchScrollDelta(dragConsumed)
            }
            Offset(0f, dragConsumed / DRAG_MULTIPLIER)
        } else {
            Offset.Zero
        }
    }
}