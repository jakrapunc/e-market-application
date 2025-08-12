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
import kotlinx.coroutines.flow.flowOf
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

    @Before
    fun setUp() {
        every { basketRepository.getCurrentBasket() } returns mockBasketItemsFlow
        coEvery { productRepository.getStoreInfo() } returns flowOf(mockStoreInfo)
        coEvery { productRepository.getProducts() } returns flowOf(mockProductList)

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
        advanceUntilIdle() // Allow initial data loading and flow combinations to settle

        viewModel.uiState.test {
            val actualInitialState = awaitItem()

            // Compare relevant parts, as storeInfo might be populated by init's loadData
            Assert.assertEquals(initialState.products, actualInitialState.products)
            Assert.assertEquals(initialState.orderList, actualInitialState.orderList)
            Assert.assertEquals(initialState.totalItem, actualInitialState.totalItem)
            Assert.assertEquals(initialState.totalPrice, actualInitialState.totalPrice)
            Assert.assertEquals(initialState.isLoading, actualInitialState.isLoading) // isLoading is false by default
            Assert.assertNull(actualInitialState.error) // Error should be null initially

            // storeInfo will be populated by loadData, so we check it in its own test
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadData success - updates storeInfo and productList`() = runTest(testDispatcher) {
        // Given
        val specificStoreInfo = StoreInfoData("Test Store", 5.0, "15:00:00.000Z", "19:00:00.000Z")
        val specificProductList = listOf(ProductData("s1", 100, ""))
        coEvery { productRepository.getStoreInfo() } returns flowOf(specificStoreInfo)
        coEvery { productRepository.getProducts() } returns flowOf(specificProductList)

        // When
        viewModel.loadData()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            // Skip initial state(s) until the loaded data is reflected
            // The number of items to skip might vary based on flow emissions.
            // Await the state that reflects the loaded data.
            // It might be more robust to use awaitItemMatching or something similar if states are complex.
            val loadedState = awaitItem() // Initial emission due to stateIn

            val finalState = awaitItem() // Emission after loadData completes

            Assert.assertNotNull(finalState.storeInfo)
            Assert.assertEquals(specificStoreInfo.name, finalState.storeInfo?.name)
            Assert.assertEquals(specificStoreInfo.openingTime.formatDateTime(), finalState.storeInfo?.openingTime)
            Assert.assertEquals(specificProductList, finalState.products)
            Assert.assertEquals(false, finalState.isLoading)
            Assert.assertNull(finalState.error)
            cancelAndConsumeRemainingEvents()
        }

        coVerify(atLeast = 1) { productRepository.getStoreInfo() } // atLeast = 1 because init also calls it
        coVerify(atLeast = 1) { productRepository.getProducts() }
    }

    @Test
    fun `loadData failure - updates error message`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network Error"
        coEvery { productRepository.getStoreInfo() } throws RuntimeException(errorMessage)
        // Let getProducts succeed to see partial state or make it fail too
        coEvery { productRepository.getProducts() } returns flowOf(mockProductList)


        // When
        val viewModel = StoreScreenViewModel(
            productRepository,
            basketRepository,
            testDispatcher
        )
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state1 = awaitItem() // Initial State from init
            val errorState = awaitItem() // State after loadData with error

            Assert.assertEquals(errorMessage, errorState.error)
            Assert.assertNull(errorState.storeInfo) // Store info failed
            Assert.assertTrue(errorState.products.isEmpty()) // Products still loaded
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `onEvent AddItem - calls basketRepository addToBasket`() = runTest(testDispatcher) {
        // Given
        val productToAdd = mockProductList.first()
        coEvery { basketRepository.addToBasket(any(), any()) } just runs // Ensure the mock doesn't throw

        // When
        viewModel.onEvent(StoreScreenViewModel.UIEvent.AddItem(productToAdd))
        advanceUntilIdle() // Ensure the coroutine in onEvent completes

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
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { basketRepository.removeBasketItem(productToRemove.name) }
    }

    @Test
    fun `uiState reflects updates from localBasket flow`() = runTest(testDispatcher) {
        // Initial state check (after loadData from init)
        advanceUntilIdle()

        viewModel.uiState.test {
            var currentState = awaitItem() // Initial state after loadData
            Assert.assertEquals(0, currentState.totalItem)
            Assert.assertEquals(0.toPriceString(), currentState.totalPrice)

            awaitItem() // Skip loading data state

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

            cancelAndConsumeRemainingEvents()
        }
    }
}