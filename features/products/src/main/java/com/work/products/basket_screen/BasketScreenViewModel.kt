package com.work.products.basket_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.base.extension.toPriceString
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.IBasketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BasketScreenViewModel(
    val basketRepository: IBasketRepository
): ViewModel() {
    private val _currentBasket = basketRepository.getCurrentBasket().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentAddress = MutableStateFlow("")

    val uiState = combine(
        _currentBasket,
        _currentAddress,
    ) { orderList, currentAddress ->
        UIState(
            orderList = orderList,
            address = currentAddress,
            totalPrice = orderList.sumOf { it.quantity * it.price }.toPriceString()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UIState(emptyList())
    )

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.AddAddress -> {
                _currentAddress.value = event.address
            }
        }
    }

    sealed interface UIEvent {
        data class AddAddress(val address: String) : UIEvent
    }

    data class UIState(
        val orderList: List<BasketItemEntity>,
        val totalPrice: String = "",
        val address: String = ""
    )
}