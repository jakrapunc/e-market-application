package com.work.stores_service.data.service.repository

import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.local.BasketDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface IBasketRepository {
    fun getCurrentBasket(): Flow<List<BasketItemEntity>>
    fun getBasketItemByName(name: String): Flow<BasketItemEntity?>

    suspend fun addToBasket(productData: ProductData, quantity: Int = 1)
    suspend fun removeBasketItem(productName: String)
    suspend fun clearBasket()
}

class BasketRepository(
    private val basketDao: BasketDao
): IBasketRepository {

    override fun getCurrentBasket(): Flow<List<BasketItemEntity>> {
        return basketDao.getAllBasketItems()
    }

    override fun getBasketItemByName(name: String): Flow<BasketItemEntity?> {
        return basketDao.getBasketItemByName(name)
    }

    override suspend fun addToBasket(productData: ProductData, quantity: Int) {
        val existingItem = basketDao.getBasketItemByName(productData.name).firstOrNull()

        existingItem?.let {
            val newQuantity = it.quantity + quantity
            if (newQuantity > 99) {
                return
            }
            basketDao.update(
                BasketItemEntity(
                    productName = productData.name,
                    price = productData.price,
                    quantity = newQuantity,
                    imageUrl = productData.imageUrl
                )
            )
        } ?: kotlin.run {
            basketDao.insert(
                BasketItemEntity(
                    productName = productData.name,
                    price = productData.price,
                    imageUrl = productData.imageUrl,
                    quantity = 1,
                )
            )
        }
    }

    override suspend fun removeBasketItem(productName: String) {
        val existingItem = basketDao.getBasketItemByName(productName).firstOrNull()

        existingItem?.let {
            if (existingItem.quantity > 1) {
                basketDao.update(
                    BasketItemEntity(
                        productName = existingItem.productName,
                        price = existingItem.price,
                        imageUrl = existingItem.imageUrl,
                        quantity = existingItem.quantity - 1,
                    )
                )
            } else {
                basketDao.delete(existingItem)
            }
        }
    }

    override suspend fun clearBasket() {
        basketDao.clearBasket()
    }
}