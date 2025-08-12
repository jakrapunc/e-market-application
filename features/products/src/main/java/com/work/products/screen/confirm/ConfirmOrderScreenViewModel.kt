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
import com.work.stores_service.extension.toProductList
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
        val args = savedStateHandle.get<String>("address")

        address.value = args

        submitOrder()
    }

    fun submitOrder() {
        _isLoading.value = true

        viewModelScope.launch {
            val basketItems =  try { basketRepository.getCurrentBasket().first() } catch (e: Exception) { emptyList() }
            val summaryProducts = basketItems.toProductList()

            if (summaryProducts.isEmpty()) {
                return@launch
            }

            productRepository.createOrder(
                orderBody = OrderBody(
                    products = summaryProducts,
                    deliveryAddress = address.value ?: "-"
                )
            ).catch {
                _isLoading.value = false
                _error.value = it.message
                _isSuccess.value = false
            }.collect {
                _isLoading.value = false
                _isSuccess.value = true
                _error.value = null

                basketRepository.clearBasket()
            }
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false
    )
}