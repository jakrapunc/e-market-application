package com.work.stores_service.di

import android.content.Context
import androidx.room.Room
import com.work.stores_service.data.service.repository.BasketRepository
import com.work.stores_service.data.service.repository.IBasketRepository
import com.work.stores_service.data.service.repository.IProductRepository
import com.work.stores_service.data.service.repository.ProductRepository
import com.work.stores_service.data.service.repository.local.BASKET_DATABASE
import com.work.stores_service.data.service.repository.local.BasketDao
import com.work.stores_service.data.service.repository.local.BasketDatabase
import com.work.stores_service.data.service.repository.remote.IProductRemote
import com.work.stores_service.data.service.repository.remote.ProductRemote
import org.koin.dsl.module

val productServiceModule = module {

    fun provideBasketDatabase(context: Context): BasketDatabase {
        return Room.databaseBuilder(
            context,
            BasketDatabase::class.java,
            BASKET_DATABASE,

        ).build()
    }

    fun provideBasketDao(basketDatabase: BasketDatabase): BasketDao {
        return basketDatabase.basketDao()
    }

    single {
        provideBasketDatabase(get())
    }

    single {
        provideBasketDao(get())
    }

    factory<IBasketRepository> {
        BasketRepository(get())
    }

    factory<IProductRemote> {
        ProductRemote(get())
    }

    factory<IProductRepository> {
        ProductRepository(get())
    }
}