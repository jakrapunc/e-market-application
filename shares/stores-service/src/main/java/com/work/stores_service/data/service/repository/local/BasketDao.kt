package com.work.stores_service.data.service.repository.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.work.stores_service.data.model.entity.BasketItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {
    @Query("SELECT * FROM basket_item")
    fun getAllBasketItems(): Flow<List<BasketItemEntity>>

    @Query("SELECT * FROM basket_item WHERE productName = :productName")
    fun getBasketItemByName(productName: String): Flow<BasketItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(basketItemEntity: BasketItemEntity)

    @Update
    suspend fun update(basketItemEntity: BasketItemEntity)

    @Delete
    suspend fun delete(basketItemEntity: BasketItemEntity)

    @Query("DELETE FROM basket_item")
    suspend fun clearBasket()
}