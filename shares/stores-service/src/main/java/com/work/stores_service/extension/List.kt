package com.work.stores_service.extension

import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.entity.BasketItemEntity

fun List<BasketItemEntity>.sumPrice(): Int {
    return this.sumOf { it.quantity * it.price }
}

fun List<BasketItemEntity>.toProductList(): List<ProductData> {
    val summaryProducts = mutableListOf<ProductData>()

    this.forEach { basket ->
        repeat(basket.quantity) {
            summaryProducts.add(
                ProductData(
                    name = basket.productName,
                    price = basket.price,
                    imageUrl = basket.imageUrl
                )
            )
        }
    }

    return summaryProducts
}