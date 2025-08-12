package com.work.stores_service.extension

import com.work.stores_service.data.model.entity.BasketItemEntity

fun List<BasketItemEntity>.sumPrice(): Int {
    return this.sumOf { it.quantity * it.price }
}