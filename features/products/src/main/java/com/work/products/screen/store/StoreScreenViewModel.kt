package com.work.products.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.base.extension.formatDateTime
import com.work.base.extension.toPriceString
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.IBasketRepository
import com.work.stores_service.data.service.repository.IProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreScreenViewModel(
    private val productRepository: IProductRepository,
    private val basketRepository: IBasketRepository,
    private val coroutineDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _storeInfo = MutableStateFlow<StoreInfoData?>(null)
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val _localBasket = basketRepository.getCurrentBasket().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    val uiState = combine(
        _storeInfo,
        _productList,
        _isLoading,
        _error,
        _localBasket
    ) { store, products, isLoading, error, localBasket ->
        UIState(
            storeInfo = store?.copy(
                openingTime = store.openingTime.formatDateTime() ?: "0.00",
                closingTime = store.closingTime.formatDateTime() ?: "24.00"
            ),
            products = products,
            isLoading = isLoading,
            error = error,
            orderList = localBasket,
            totalItem = localBasket.sumOf { it.quantity },
            totalPrice = sumPrice(localBasket)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UIState()
    )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            withContext(coroutineDispatcher) {
                try {
                    val storeData = async { productRepository.getStoreInfo().first() }
                    val products = async { productRepository.getProducts().first() }

                    val storeResult = storeData.await()
                    val productResult = products.await()
                    _storeInfo.update { storeResult }
                    _productList.update { productResult }
                } catch (e: Exception) {
                    _error.update { e.message }
                }
            }
        }
    }

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.AddItem -> {
                viewModelScope.launch {
                    basketRepository.addToBasket(event.data, 1)
                }
            }
            is UIEvent.RemoveItem -> {
                viewModelScope.launch {
                    basketRepository.removeBasketItem(event.data.name)
                }
            }
        }
    }

    private fun sumPrice(orderList: List<BasketItemEntity>): String {
        return orderList.sumOf { it.quantity * it.price }.toPriceString()
    }

    sealed interface UIEvent {
        data class AddItem(val data: ProductData): UIEvent
        data class RemoveItem(val data: ProductData): UIEvent
    }

    data class UIState(
        val storeInfo: StoreInfoData? = null,
        val products: List<ProductData> = emptyList(),
        val orderList: List<BasketItemEntity> = emptyList(),
        val totalItem: Int = 0,
        val totalPrice: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}