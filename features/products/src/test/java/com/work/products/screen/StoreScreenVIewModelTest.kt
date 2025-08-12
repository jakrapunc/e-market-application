package com.work.products.screen

import app.cash.turbine.test
import com.work.base.extension.formatDateTime
import com.work.base.extension.toPriceString
import com.work.products.screen.store.StoreScreenViewModel
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.IBasketRepository
import com.work.stores_service.data.service.repository.IProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class StoreScreenVIewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var productRepository: IProductRepository
    @MockK
    private lateinit var basketRepository: IBasketRepository
    @MockK
    private lateinit var viewModel: StoreScreenViewModel

    // Mock data
    private val mockStoreInfo = StoreInfoData(
        "Test Store",
        5.0,
        "15:00:00.000Z",
        "19:00:00.000Z",
    )
    private val mockProductList = listOf(
        ProductData("1", 10, "Desc A"),
        ProductData("2", 20, "imgB.png")
    )
    private val mockBasketItemsFlow = MutableSharedFlow<List<BasketItemEntity>>(replay = 1)
    private val mockStoreInfoFlow = MutableSharedFlow<StoreInfoData>(replay = 1)
    private val mockProductListFlow = MutableSharedFlow<List<ProductData>>(replay = 1)

    @Before
    fun setUp() {
        every { basketRepository.getCurrentBasket() } returns mockBasketItemsFlow
        coEvery { productRepository.getStoreInfo() } returns mockStoreInfoFlow
        coEvery { productRepository.getProducts() } returns mockProductListFlow

        viewModel = StoreScreenViewModel(
            productRepository,
            basketRepository,
            testDispatcher // Use the test dispatcher
        )

        runTest(testDispatcher) {
            mockBasketItemsFlow.emit(emptyList())
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initial uiState is correct`() = runTest(testDispatcher) {
        val initialState = StoreScreenViewModel.UIState()

        viewModel.uiState.test {
            val actualInitialState = awaitItem()

            // Compare relevant parts, as storeInfo might be populated by init's loadData
            Assert.assertEquals(initialState.products, actualInitialState.products)
            Assert.assertEquals(initialState.orderList, actualInitialState.orderList)
            Assert.assertEquals(initialState.totalItem, actualInitialState.totalItem)
            Assert.assertEquals(initialState.totalPrice, actualInitialState.totalPrice)
            Assert.assertEquals(initialState.isLoading, actualInitialState.isLoading)
            Assert.assertNull(actualInitialState.error) // Error should be null initially
        }
    }

    @Test
    fun `loadData success - updates storeInfo and productList`() = runTest(testDispatcher) {
        // Given
        viewModel.uiState.test {
            val initialState = awaitItem()

            mockStoreInfoFlow.emit(mockStoreInfo)
            mockProductListFlow.emit(mockProductList)

            val finalState = awaitItem()

            Assert.assertNotNull(finalState.storeInfo)
            Assert.assertEquals(mockStoreInfo.name, finalState.storeInfo?.name)
            Assert.assertEquals(mockStoreInfo.openingTime.formatDateTime(), finalState.storeInfo?.openingTime)
            Assert.assertEquals(mockProductList, finalState.products)
            Assert.assertEquals(false, finalState.isLoading)
            Assert.assertNull(finalState.error)
        }

        coVerify(atLeast = 1) { productRepository.getStoreInfo() } // atLeast = 1 because init also calls it
        coVerify(atLeast = 1) { productRepository.getProducts() }
    }

    @Test
    fun `loadData failure - updates error message`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network Error"
        val exception = RuntimeException(errorMessage)

        coEvery { productRepository.getStoreInfo() } throws exception

        val viewModel = StoreScreenViewModel(
            productRepository,
            basketRepository,
            testDispatcher // Use the test dispatcher
        )

        viewModel.uiState.test {
            val initialState = awaitItem() // Initial State from init
            val errorState = awaitItem() // State after loadData with error

            Assert.assertEquals(errorMessage, errorState.error)
        }

        coVerify(atLeast = 1) { productRepository.getStoreInfo() } // atLeast = 1 because init also calls it
        coVerify(atLeast = 1) { productRepository.getProducts() }
    }

    @Test
    fun `onEvent AddItem - calls basketRepository addToBasket`() = runTest(testDispatcher) {
        // Given
        val productToAdd = mockProductList.first()
        coEvery { basketRepository.addToBasket(any(), any()) } just runs // Ensure the mock doesn't throw

        // When
        viewModel.onEvent(StoreScreenViewModel.UIEvent.AddItem(productToAdd))

        // Then
        coVerify(exactly = 1) { basketRepository.addToBasket(productToAdd, 1) }
    }

    @Test
    fun `onEvent RemoveItem - calls basketRepository removeBasketItem`() = runTest(testDispatcher) {
        // Given
        val productToRemove = mockProductList.first()
        coEvery { basketRepository.removeBasketItem(any()) } just runs

        // When
        viewModel.onEvent(StoreScreenViewModel.UIEvent.RemoveItem(productToRemove))

        // Then
        coVerify(exactly = 1) { basketRepository.removeBasketItem(productToRemove.name) }
    }

    @Test
    fun `uiState reflects updates from localBasket flow`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var currentState = awaitItem() // Initial state after loadData
            Assert.assertEquals(0, currentState.totalItem)
            Assert.assertEquals(0.toPriceString(), currentState.totalPrice)

            // When basket updates
            val basketItem1 = BasketItemEntity(productName = "Product A", price = 1000, quantity = 2, imageUrl = "")
            val updatedBasket1 = listOf(basketItem1)
            mockBasketItemsFlow.emit(updatedBasket1)
            advanceUntilIdle() // Allow combine to process the new basket items

            currentState = awaitItem() // Get the updated state
            Assert.assertEquals(2, currentState.totalItem)
            Assert.assertEquals((1000 * 2).toPriceString(), currentState.totalPrice)
            Assert.assertTrue(currentState.orderList.any { it.productName == "Product A" && it.quantity == 2 })

            // When basket updates again
            val basketItem2 = BasketItemEntity(productName = "Product B", price = 2000, quantity = 1, imageUrl = "")
            val updatedBasket2 = listOf(basketItem1, basketItem2)
            mockBasketItemsFlow.emit(updatedBasket2)
            advanceUntilIdle()

            currentState = awaitItem()
            Assert.assertEquals(3, currentState.totalItem)
            Assert.assertEquals(((1000 * 2) + (2000 * 1)).toPriceString(), currentState.totalPrice)
            Assert.assertTrue(currentState.orderList.any { it.productName == "Product A" && it.quantity == 2 })
            Assert.assertTrue(currentState.orderList.any { it.productName == "Product B" && it.quantity == 1 })

            // When basket is emptied
            mockBasketItemsFlow.emit(emptyList())
            advanceUntilIdle()

            currentState = awaitItem()
            Assert.assertEquals(0, currentState.totalItem)
            Assert.assertEquals(0.toPriceString(), currentState.totalPrice)
            Assert.assertTrue(currentState.orderList.isEmpty())
        }
    }
}