package com.example.levonsmusic.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levonsmusic.model.ApiResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    protected fun <T : ApiResult> launch(
        liveData: MutableLiveData<RequestState<T>>?,
        requestBlock: suspend () -> T,
        failBlock: (suspend (code: Int?, message: String?) -> Unit)? = null,
        completeBlock: (suspend (result: T) -> Unit)? = null
    ): Job {
        return viewModelScope.launch {
            runCatching {
                liveData?.value = RequestState.Loading
                requestBlock.invoke()
            }.onSuccess { result ->
                if (result.successful()) {
                    completeBlock?.invoke(result)
                    liveData?.value = RequestState.Success(result)
                } else {
                    failBlock?.invoke(result.code, result.message ?: "")
                    liveData?.value = RequestState.Fail(
                        result.code.toString(),
                        result.message ?: "请求发生错误"
                    )
                }
            }.onFailure { e ->
                liveData?.value = RequestState.Error(e)
            }
        }
    }
}

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Fail(val errorCode: String, val errorMsg: String) : RequestState<Nothing>()
    data class Error(val exception: Throwable) : RequestState<Nothing>()
}
