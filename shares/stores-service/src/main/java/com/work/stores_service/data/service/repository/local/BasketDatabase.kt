package com.work.stores_service.data.service.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.work.stores_service.data.model.entity.BasketItemEntity

const val BASKET_DATABASE = "e-market-db"

@Database(
    entities = [BasketItemEntity::class],
    version = 1,
)
abstract class BasketDatabase: RoomDatabase() {
    abstract fun basketDao(): BasketDao
}