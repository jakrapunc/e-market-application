package com.work.stores_service.data.service.repository.remote

import com.work.network.base.ApiManager
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.api.ProductService
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

interface IProductRemote {
    suspend fun getStoreInfo(): Response<StoreInfoData>
    suspend fun getProducts(): Response<List<ProductData>>
    suspend fun createOrder(orderBody: OrderBody): Response<String>
}

class ProductRemote(
    private val apiManager: ApiManager
): IProductRemote {

    fun getProductService(): ProductService {
        return apiManager.init(
            baseUrl = "https://c8d92d0a-6233-4ef7-a229-5a91deb91ea1.mock.pstmn.io",
            converter = GsonConverterFactory.create()
        ).create(ProductService::class.java)
    }

    override suspend fun getStoreInfo(): Response<StoreInfoData> {
        return getProductService().getStoreInfo()
    }

    override suspend fun getProducts(): Response<List<ProductData>> {
        return getProductService().getProducts()
    }

    override suspend fun createOrder(orderBody: OrderBody): Response<String> {
        return getProductService().createOrder(orderBody)
    }
}