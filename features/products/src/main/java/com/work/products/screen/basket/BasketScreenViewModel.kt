package com.work.products.screen.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.base.extension.toPriceString
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.IBasketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class BasketScreenViewModel(
    val basketRepository: IBasketRepository
): ViewModel() {
    private val _currentBasket = basketRepository.getCurrentBasket().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val uiState = _currentBasket.map { basket ->
        UIState(
            orderList = basket,
            totalPrice = basket.sumOf { it.quantity * it.price }.toPriceString()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UIState(emptyList())
    )

    data class UIState(
        val orderList: List<BasketItemEntity>,
        val totalPrice: String = "",
    )
}