package com.work.network.base

import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager(
    private val networkClient: NetworkClient,
    private val builder: Retrofit.Builder
) {

    fun init(
        baseUrl: String,
        converter: Converter.Factory = GsonConverterFactory.create()
    ): Retrofit = builder
        .baseUrl(baseUrl)
        .client(networkClient.getClient())
        .addConverterFactory(converter)
        .build()
}