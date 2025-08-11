package com.work.products.screen.confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.repository.IBasketRepository
import com.work.stores_service.data.service.repository.IProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfirmOrderScreenViewModel(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _isSuccess = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState = combine(
        _isLoading,
        _error
    ) { isLoading, error ->
        UIState(
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UIState()
    )

    fun submitOrder(address: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val basketItems = basketRepository.getCurrentBasket().first()
            val summaryProducts = transformBasketItems(basketItems)

            productRepository.createOrder(
                orderBody = OrderBody(
                    products = summaryProducts,
                    deliveryAddress = address
                )
            ).catch {
                _isLoading.value = false
                _error.value = it.message
            }.collect {
                _isLoading.value = false
                _isSuccess.value = true
            }
        }
    }

    private fun transformBasketItems(basketItems: List<BasketItemEntity>): List<ProductData> {
        val summaryProducts = mutableListOf<ProductData>()

        basketItems.forEach { basket ->
            repeat(basket.quantity) {
                summaryProducts.add(
                    ProductData(
                        name = basket.productName,
                        price = basket.price,
                        imageUrl = basket.imageUrl
                    )
                )
            }
        }

        return summaryProducts
    }

    data class UIState(
        val isLoading: Boolean = false,
        val address: String = "",
        val error: String? = null,
        val isSuccess: Boolean = false
    )
}