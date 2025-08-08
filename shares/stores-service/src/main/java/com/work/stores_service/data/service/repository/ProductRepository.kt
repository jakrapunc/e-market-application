package com.work.stores_service.data.service.repository

import com.work.network.repository.NetworkBoundResource
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.repository.remote.IProductRemote
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IProductRepository {
    suspend fun getStoreInfo(): Flow<StoreInfoData>
    suspend fun getProducts(): Flow<List<ProductData>>
    suspend fun createOrder(orderBody: OrderBody): Flow<String>
}

class ProductRepository(
    private val productRemote: IProductRemote
): IProductRepository {

    override suspend fun getStoreInfo(): Flow<StoreInfoData> {
        return object : NetworkBoundResource<StoreInfoData>() {
            override suspend fun createCall(): Response<StoreInfoData> {
                return productRemote.getStoreInfo()
            }
        }.asFlow()
    }

    override suspend fun getProducts(): Flow<List<ProductData>> {
        return object : NetworkBoundResource<List<ProductData>>() {
            override suspend fun createCall(): Response<List<ProductData>> {
                return productRemote.getProducts()
            }
        }.asFlow()
    }

    override suspend fun createOrder(orderBody: OrderBody): Flow<String> {
        return object : NetworkBoundResource<String>() {
            override suspend fun createCall(): Response<String> {
                return productRemote.createOrder(orderBody)
            }
        }.asFlow()
    }
}