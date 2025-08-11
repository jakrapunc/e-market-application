package com.work.products.screen.confirm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.work.base.navigation.Route
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
    private val basketRepository: IBasketRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _isSuccess = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val address = MutableStateFlow<String?>(null)

    val uiState = combine(
        _isLoading,
        _isSuccess,
        _error
    ) { isLoading, isSuccess, error ->
        UIState(
            isLoading = isLoading,
            isSuccess = isSuccess,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UIState()
    )

    init {
        val args = savedStateHandle.toRoute< Route.SuccessScreen>()

        address.value = args.address

        submitOrder()
    }

    fun submitOrder() {
        _isLoading.value = true

        viewModelScope.launch {
            val basketItems = basketRepository.getCurrentBasket().first()
            val summaryProducts = transformBasketItems(basketItems)

            productRepository.createOrder(
                orderBody = OrderBody(
                    products = summaryProducts,
                    deliveryAddress = address.value ?: "-"
                )
            ).catch {
                _isLoading.value = false
                _error.value = it.message
            }.collect {
                _isLoading.value = false
                _isSuccess.value = true

                basketRepository.clearBasket()
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
        val error: String? = null,
        val isSuccess: Boolean = false
    )
}