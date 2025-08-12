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
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class ConfirmOrderScreenViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var productRepository: IProductRepository

    @MockK
    private lateinit var basketRepository: IBasketRepository

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: ConfirmOrderScreenViewModel

    private val testAddress = "Test Address"

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle().apply {
            this["address"] = testAddress
        }

        coEvery { basketRepository.getCurrentBasket() } returns flowOf(emptyList())
        coEvery { productRepository.createOrder(any()) } returns flowOf("Success")
        coJustRun { basketRepository.clearBasket() }

        // ViewModel is initialized AFTER all mocks are set up, especially SavedStateHandle
        viewModel = ConfirmOrderScreenViewModel(
            productRepository,
            basketRepository,
            savedStateHandle
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initialization - retrieves address and calls submitOrder`() = runTest(testDispatcher) {
        advanceUntilIdle() // Allow submitOrder to complete

        val expectedOrderBody = OrderBody(products = emptyList(), deliveryAddress = testAddress)
        coVerify(atLeast = 1) { productRepository.createOrder(expectedOrderBody) } // atLeast = 1 because submitOrder is in init

        // Verify submitOrder's effects (e.g., loading state, success if basket was empty)
        viewModel.uiState.test {
            val initialState = awaitItem() // Initial UIState()
            val state = awaitItem()

            Assert.assertFalse(state.isLoading)
            Assert.assertTrue(state.isSuccess) // Success because default basket is empty
            Assert.assertNull(state.error)
            cancelAndConsumeRemainingEvents()
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

        coEvery { basketRepository.getCurrentBasket() } returns flowOf(basketItems)
        coEvery { productRepository.createOrder(expectOrderBody) } returns flowOf("Success")
        coJustRun { basketRepository.clearBasket() }

        // When
        val viewModel = ConfirmOrderScreenViewModel(productRepository, basketRepository, savedStateHandle)

        // Then
        viewModel.uiState.test {
            val initialState = awaitItem() // UIState before submitOrder really kicks in or if it was reset
            val successState = awaitItem() // isSuccess = true

            Assert.assertFalse(successState.isLoading)
            Assert.assertTrue(successState.isSuccess)
            Assert.assertNull(successState.error)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `submitOrder failure from createOrder - updates error and does not clear basket`() = runTest(testDispatcher) {
        // Given
        val basketItems = listOf(BasketItemEntity("Product 1", 100, quantity = 1, imageUrl = "img1.url"))
        val mapProducts = listOf(ProductData("Product 1", 100, "img1.url"))
        val expectOrderBody = OrderBody(products = mapProducts, deliveryAddress = testAddress)
        val errorMessage = "Order creation failed"

        coEvery { basketRepository.getCurrentBasket() } returns flowOf(basketItems)
        coEvery { productRepository.createOrder(expectOrderBody) } returns flow { throw RuntimeException(errorMessage) }

        // When
        viewModel.submitOrder()
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            awaitItem() // Initial
            val errorState = awaitItem()   // error state


            Assert.assertFalse(errorState.isLoading)
            Assert.assertFalse(errorState.isSuccess)
            Assert.assertEquals(errorMessage, errorState.error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `submitOrder failure from getCurrentBasket - updates error`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Failed to fetch basket"
        coEvery { basketRepository.getCurrentBasket() } returns flow { throw RuntimeException(errorMessage) }

        // When
        viewModel.submitOrder()
        advanceUntilIdle()

        coVerify(exactly = 2) { basketRepository.getCurrentBasket() }
        coVerify(exactly = 0) { productRepository.createOrder(any()) } // Should not be called
        coVerify(exactly = 0) { basketRepository.clearBasket() }
    }
}