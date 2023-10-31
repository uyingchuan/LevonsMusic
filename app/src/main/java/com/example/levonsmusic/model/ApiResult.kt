package com.example.levonsmusic.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
open class ApiResult(val code: Int = 0, val message: String? = null) : Serializable {
    open fun successful(): Boolean = code == 200

}