package com.work.products.screen

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.work.products.screen.confirm.ConfirmOrderScreenViewModel
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.repository.IBasketRepository
import com.work.stores_service.data.service.repository.IProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
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
class ConfirmOrderScreenViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var productRepository: IProductRepository

    @MockK
    private lateinit var basketRepository: IBasketRepository

    private lateinit var savedStateHandle: SavedStateHandle

    private val testAddress = "Test Address"

    private val mockBasketFlow = MutableSharedFlow<List<BasketItemEntity>>(replay = 1)
    private val mockCreateOrderFlow = MutableSharedFlow<String>(replay = 1)

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle().apply {
            this["address"] = testAddress
        }

        coEvery { basketRepository.getCurrentBasket() } returns mockBasketFlow
        coEvery { productRepository.createOrder(any()) } returns mockCreateOrderFlow
        coEvery { basketRepository.clearBasket() } returns Unit
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initialization - retrieves address and calls submitOrder`() = runTest(testDispatcher) {
        val expectedOrderBody = OrderBody(products = emptyList(), deliveryAddress = testAddress)
        coVerify(exactly = 0) { productRepository.createOrder(expectedOrderBody) }

        val viewModel = ConfirmOrderScreenViewModel(
            productRepository,
            basketRepository,
            savedStateHandle
        )

        // Verify submitOrder's effects (e.g., loading state, success if basket was empty)
        viewModel.uiState.test {
            val initialState = awaitItem() // Initial UIState()'

            mockBasketFlow.emit(listOf(
                BasketItemEntity( "Product 1", 100, quantity = 1, imageUrl = "img1.url"),
            ))
            advanceUntilIdle()
            val awaitResult = awaitItem()

            mockCreateOrderFlow.emit("Success")
            advanceUntilIdle()
            val resultState = awaitItem()

            Assert.assertFalse(resultState.isLoading)
            Assert.assertTrue(resultState.isSuccess)
            Assert.assertNull(resultState.error)
        }
    }

    @Test
    fun `submitOrder success - updates uiState and clears basket`() = runTest(testDispatcher) {
        // Given
        val basketItems = listOf(
            BasketItemEntity( "Product 1", 100, quantity = 1, imageUrl = "img1.url"),
            BasketItemEntity( "Product 2", 200, quantity = 2, imageUrl = "img2.url")
        )
        val mapProducts = listOf(
            ProductData("Product 1", 100, "img1.url"),
            ProductData("Product 2", 200, "img2.url"),
            ProductData("Product 2", 200, "img2.url")
        )
        val expectOrderBody = OrderBody(products = mapProducts, deliveryAddress = testAddress)

        val viewModel = ConfirmOrderScreenViewModel(
            productRepository,
            basketRepository,
            savedStateHandle
        )

        mockBasketFlow.emit(basketItems)

        viewModel.uiState.test {
            val initialState = awaitItem()

            mockBasketFlow.emit(basketItems)
            advanceUntilIdle()
            val awaitResult = awaitItem()

            mockCreateOrderFlow.emit("Success")
            advanceUntilIdle()
            val successState = awaitItem() // isSuccess = true

            Assert.assertFalse(successState.isLoading)
            Assert.assertTrue(successState.isSuccess)
            Assert.assertNull(successState.error)
        }

        coVerify(exactly = 1) { basketRepository.getCurrentBasket() }
        coVerify(exactly = 1) { productRepository.createOrder(expectOrderBody) }
        coVerify(exactly = 1) { basketRepository.clearBasket() }
    }

    @Test
    fun `submitOrder failure from createOrder - updates error and does not clear basket`() = runTest(testDispatcher) {
        // Given
        val basketItems = listOf(BasketItemEntity("Product 1", 100, quantity = 1, imageUrl = "img1.url"))
        val mapProducts = listOf(ProductData("Product 1", 100, "img1.url"))
        val expectOrderBody = OrderBody(products = mapProducts, deliveryAddress = testAddress)
        val errorMessage = "Order creation failed"

        coEvery { productRepository.createOrder(expectOrderBody) } throws Exception(errorMessage)

        // When
        val viewModel = ConfirmOrderScreenViewModel(
            productRepository,
            basketRepository,
            savedStateHandle
        )

        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()

            mockBasketFlow.emit(basketItems)
            advanceUntilIdle()
            val errorState = awaitItem()

            Assert.assertFalse(errorState.isLoading)
            Assert.assertFalse(errorState.isSuccess)
            Assert.assertEquals(errorMessage, errorState.error)
        }

        coVerify(exactly = 0) { basketRepository.clearBasket() }
    }

    @Test
    fun `submitOrder failure from getCurrentBasket - updates error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Failed to fetch basket"
        coEvery { basketRepository.getCurrentBasket() } throws RuntimeException(errorMessage)

        // When
        val viewModel = ConfirmOrderScreenViewModel(
            productRepository,
            basketRepository,
            savedStateHandle
        )

        coVerify(exactly = 0) { productRepository.createOrder(any()) } // Should not be called
        coVerify(exactly = 0) { basketRepository.clearBasket() }
    }
}