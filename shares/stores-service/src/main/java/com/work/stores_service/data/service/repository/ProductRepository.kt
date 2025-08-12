package com.work.stores_service.data.service.repository

import com.work.network.repository.NetworkBoundResource
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.repository.remote.IProductRemote
import kotlinx.coroutines.delay
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
//                delay(2000)
//
//                return Response.success(
//                    StoreInfoData(
//                        name = "The Coffee Shop",
//                        rating = 4.5,
//                        openingTime = "15:01:01.772Z",
//                        closingTime = "19:45:51.365Z",
//                    )
//                )
                return productRemote.getStoreInfo()
            }
        }.asFlow()
    }

    override suspend fun getProducts(): Flow<List<ProductData>> {
        return object : NetworkBoundResource<List<ProductData>>() {
            override suspend fun createCall(): Response<List<ProductData>> {
//                delay(2000)
//
//                return Response.success(
//                    listOf(
//                        ProductData(
//                            name = "Latte",
//                            price = 50,
//                            imageUrl = "https://www.nespresso.com/ncp/res/uploads/recipes/nespresso-recipes-Latte-Art-Tulip.jpg"
//                        ),
//                        ProductData(
//                            name = "Dark Tiramisu Mocha",
//                            price = 75,
//                            imageUrl = "https://www.nespresso.com/shared_res/mos/free_html/sg/b2b/b2ccoffeerecipes/listing-image/image/dark-tiramisu-mocha.jpg"
//                        )
//                    )
//                )
                return productRemote.getProducts()
            }
        }.asFlow()
    }

    override suspend fun createOrder(orderBody: OrderBody): Flow<String> {
        return object : NetworkBoundResource<String>() {
            override suspend fun createCall(): Response<String> {
//                delay(2000)
//
//                return Response.success("Success")
                return productRemote.createOrder(orderBody)
            }
        }.asFlow()
    }
}