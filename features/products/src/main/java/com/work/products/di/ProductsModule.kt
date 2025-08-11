package com.work.products.di

import com.work.products.screen.basket.BasketScreenViewModel
import com.work.products.screen.store.StoreScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val productsModule = module {

    viewModel {
        StoreScreenViewModel(
            get(),
            get(),
            get(named("io")),
        )
    }

    viewModel {
        BasketScreenViewModel(
            get(),
        )
    }
}