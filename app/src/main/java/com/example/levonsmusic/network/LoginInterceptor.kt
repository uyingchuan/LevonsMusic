package com.example.levonsmusic.network

import com.example.levonsmusic.ui.page.login.LoginAccount
import okhttp3.Interceptor
import okhttp3.Response

class LoginInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val loginResult = LoginAccount.data
        if (loginResult != null) {
            val request = chain.request()
            val url = if (request.url.toString().contains("?")) {
                request.url.toString() + "&cookie=" + loginResult.cookie
            } else {
                request.url.toString() + "?cookie=" + loginResult.cookie
            }
            val builder = request.newBuilder()
            builder.get().url(url)
            val newRequest = builder.build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(chain.request())
    }
}