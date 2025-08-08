package com.work.stores_service.di

import com.work.stores_service.data.service.repository.IProductRepository
import com.work.stores_service.data.service.repository.ProductRepository
import com.work.stores_service.data.service.repository.remote.IProductRemote
import com.work.stores_service.data.service.repository.remote.ProductRemote
import org.koin.dsl.module

val productServiceModule = module {

    factory<IProductRemote> {
        ProductRemote(get())
    }

    factory<IProductRepository> {
        ProductRepository(get())
    }
}