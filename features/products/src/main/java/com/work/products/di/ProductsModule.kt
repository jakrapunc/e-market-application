package com.work.products.di

import com.work.products.store_screen.StoreScreenViewModel
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
}