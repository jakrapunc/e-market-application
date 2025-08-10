package com.work.emarket

import android.app.Application
import com.work.network.di.networkModule
import com.work.products.di.productsModule
import com.work.stores_service.di.productServiceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EMarketApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@EMarketApplication)
            // Load modules
            modules(appModule)
        }
    }
}

val appModule = listOf(
    networkModule,
    productServiceModule,
    productsModule
)