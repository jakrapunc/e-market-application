package com.work.network.base

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class NetworkClient(
    private val context: Context
) {

    companion object {
        const val CONTENT_TYPE: String = "Content-Type"
        const val TIME_OUT: Long = 30
    }

    fun getClient() : OkHttpClient {
        return OkHttpClient().newBuilder().apply {
            readTimeout(TIME_OUT, TimeUnit.SECONDS)
            connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            addInterceptor(getRequestInterceptor())
            addInterceptor(
                HttpLoggingInterceptor(NetworkHttpLogger()).setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            )
        }.build()
    }

    private fun getRequestInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder().apply {
                addHeader(CONTENT_TYPE, "application/json;charset=utf-8")
            }.build()
            chain.proceed(request)
        }
    }
}