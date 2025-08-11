package com.work.base.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object StoreScreen : Route

    @Serializable
    data object BasketScreen : Route

    @Serializable
    data class SuccessScreen(val address: String) : Route
}