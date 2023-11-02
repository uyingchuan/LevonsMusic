package com.example.levonsmusic.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val startNs = System.currentTimeMillis()
        val request = chain.request()
        Log.d("OKHttp", "OKHttp=====> Start")
        Log.d("OKHttp", "OKHttp=====> " + request.url().toString())

        val response = chain.proceed(request)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        Log.d(
            "OKHTTP",
            "OKHttp=====> ${response.code()}${
                if (response.message().isEmpty()) "" else ' ' + response.message()
            } (${tookMs}ms)"
        )

        val responseBody = response.body()!!

        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer

        val charset: Charset = responseBody.contentType()?.charset()!!

        if (responseBody.contentLength() != 0L) {
            Log.d("OKHTTP", "OKHttp=====> ${buffer.clone().readString(charset)}")
        }

        Log.d("OKHTTP", "OKHttp=====> End")
        return response
    }
}