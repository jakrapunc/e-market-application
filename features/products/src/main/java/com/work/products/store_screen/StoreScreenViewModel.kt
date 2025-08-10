package com.work.products.store_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.base.extension.formatDateTime
import com.work.base.extension.toPriceString
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
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
    private val coroutineDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _storeInfo = MutableStateFlow<StoreInfoData?>(null)
    private val _productList = MutableStateFlow<List<ProductData>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _orderList = MutableStateFlow<Map<ProductData, Int>>(emptyMap())

    val uiState = combine(
        _storeInfo,
        _productList,
        _isLoading,
        _error,
        _orderList
    ) { store, products, isLoading, error, orderList ->
        UIState(
            storeInfo = store?.copy(
                openingTime = store.openingTime.formatDateTime() ?: "0.00",
                closingTime = store.closingTime.formatDateTime() ?: "24.00"
            ),
            products = products,
            isLoading = isLoading,
            error = error,
            orderList = orderList,
            totalItem = orderList.values.sum(),
            totalPrice = sumPrice(orderList)
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
                _orderList.update {
                    it.toMutableMap().apply {
                        if ((this[event.data] ?: 0) < 99) {
                            this[event.data] = (this[event.data] ?: 0) + 1
                        }
                    }
                }
            }
            is UIEvent.RemoveItem -> {
                _orderList.update {
                    it.toMutableMap().apply {
                        if (this[event.data] != null && (this[event.data] ?: 0) > 0) {
                            this[event.data] = (this[event.data] ?: 0) - 1
                        }
                    }
                }
            }
            is UIEvent.UpdateItem -> {
                _orderList.update {
                    it.toMutableMap().apply {
                        this[event.data] = event.count
                    }
                }
            }
        }
    }

    private fun sumPrice(orderList: Map<ProductData, Int>): String {
        var sum = 0
        orderList.forEach { (product, count) ->
            sum += product.price * count
        }
        return sum.toPriceString()
    }

    sealed interface UIEvent {
        data class AddItem(val data: ProductData): UIEvent
        data class RemoveItem(val data: ProductData): UIEvent
        data class UpdateItem(val data: ProductData, val count: Int): UIEvent
    }

    data class UIState(
        val storeInfo: StoreInfoData? = null,
        val products: List<ProductData> = emptyList(),
        val orderList: Map<ProductData, Int> = emptyMap(),
        val totalItem: Int = 0,
        val totalPrice: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}