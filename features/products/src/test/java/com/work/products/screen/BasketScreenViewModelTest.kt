package com.work.products.screen

import app.cash.turbine.test
import com.work.base.extension.toPriceString
import com.work.products.screen.basket.BasketScreenViewModel
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.IBasketRepository
import io.mockk.every
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
class BasketScreenViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var basketRepository: IBasketRepository
    private lateinit var viewModel: BasketScreenViewModel

    private val basketFlow = MutableSharedFlow<List<BasketItemEntity>>(replay = 1)

    @Before
    fun setup() {
        // Mock the repository call
        every { basketRepository.getCurrentBasket() } returns basketFlow

        // Initialize ViewModel AFTER mocks are set up
        viewModel = BasketScreenViewModel(basketRepository)

        runTest(testDispatcher) { // Use runTest for emitting in setup
            basketFlow.emit(emptyList())
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initial uiState is correct with empty basket`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()

            Assert.assertTrue(initialState.orderList.isEmpty())
            Assert.assertEquals("", initialState.totalPrice) // Assuming 0.toPriceString()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `uiState updates correctly when basket items change`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem()
            Assert.assertEquals(emptyList<BasketItemEntity>(), initialState.orderList) // Consume initial empty state

            awaitItem() // Skip default state

            // Simulate adding items to the basket
            val items1 = listOf(
                BasketItemEntity("Product A", 1000, quantity = 2, imageUrl = "imgA.png"),
                BasketItemEntity( "Product B", 500, quantity = 1, imageUrl = "imgB.png")
            )
            basketFlow.emit(items1)
            advanceUntilIdle() // Allow flow processing

            val state1 = awaitItem()
            val expectedPrice1 = (1000 * 2) + (500 * 1)

            Assert.assertEquals(items1, state1.orderList)
            Assert.assertEquals(expectedPrice1.toPriceString(), state1.totalPrice)

            val items2 = listOf(
                BasketItemEntity("Product A", 1000, quantity = 3, imageUrl = "imgA.png") // Quantity updated
            )
            basketFlow.emit(items2)
            advanceUntilIdle()

            val state2 = awaitItem()
            val expectedPrice2 = 1000 * 3
            Assert.assertEquals(items2, state2.orderList)
            Assert.assertEquals(expectedPrice2.toPriceString(), state2.totalPrice)

            // Simulate basket becoming empty
            basketFlow.emit(emptyList())
            advanceUntilIdle()

            val emptyState = awaitItem()
            Assert.assertTrue(emptyState.orderList.isEmpty())
            Assert.assertEquals(0.toPriceString(), emptyState.totalPrice)

            cancelAndConsumeRemainingEvents()
        }
    }

}