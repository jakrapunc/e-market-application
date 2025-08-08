package com.work.stores_service.data.service.api

import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductService {
    @GET("/storeInfo")
    suspend fun getStoreInfo(): Response<StoreInfoData>

    @GET("/products")
    suspend fun getProducts(): Response<List<ProductData>>

    @POST("/order")
    suspend fun createOrder(
        @Body orderBody: OrderBody
    ): Response<String>

}