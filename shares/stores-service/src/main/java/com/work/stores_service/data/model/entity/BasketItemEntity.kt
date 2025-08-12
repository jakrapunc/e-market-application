package com.work.stores_service.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basket_item")
data class BasketItemEntity(
    @PrimaryKey val productName: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int
)