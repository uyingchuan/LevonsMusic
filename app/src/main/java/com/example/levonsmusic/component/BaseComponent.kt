package com.example.levonsmusic.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import com.example.levonsmusic.util.RequestState

@Composable
fun <T> BaseComponent(
    modifier: Modifier = Modifier,
    liveData: LiveData<RequestState<T>>,
    loadDataBlock: (() -> Unit)? = null,
    reloadDataBlock: (() -> Unit)? = null,
    content: @Composable (data: T) -> Unit
) {
    val requestState by liveData.observeAsState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (requestState) {
            is RequestState.Loading -> {
                LoadingComponent()
            }

            is RequestState.Success -> {
                content.invoke((requestState as RequestState.Success<T>).data)
            }

            is RequestState.Fail -> {
                ErrorComponent(
                    message = "错误码：${(requestState as RequestState.Fail).errorCode}；${(requestState as RequestState.Fail).errorMsg}，点我重试",
                    reloadDataBlock = reloadDataBlock
                )
            }

            is RequestState.Error -> {
                ErrorComponent()
            }

            else -> {
                loadDataBlock?.invoke()
            }
        }
    }
}