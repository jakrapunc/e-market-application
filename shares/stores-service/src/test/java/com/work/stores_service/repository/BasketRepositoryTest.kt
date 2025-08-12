package com.work.stores_service.repository

import app.cash.turbine.test
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.entity.BasketItemEntity
import com.work.stores_service.data.service.repository.BasketRepository
import com.work.stores_service.data.service.repository.local.BasketDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BasketRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var basketDao: BasketDao
    private lateinit var basketRepository: BasketRepository

    @Before
    fun setUp() {
        basketDao = mockk(relaxUnitFun = true)
        basketRepository = BasketRepository(basketDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getCurrentBasket calls dao and returns flow`() = runTest {
        // Given
        val expectedFlow = flowOf(listOf(BasketItemEntity("Product 1", 10, quantity = 1, imageUrl = "img1")))
        every { basketDao.getAllBasketItems() } returns expectedFlow

        // When
        val resultFlow = basketRepository.getCurrentBasket()

        // Then
        resultFlow.test {
            val result = awaitItem()
            Assert.assertEquals(expectedFlow.first(), result)

            awaitComplete()
        }
        verify(exactly = 1) { basketDao.getAllBasketItems() }
    }

    @Test
    fun `getBasketItemByName calls dao and returns flow`() = runTest {
        // Given
        val productName = "Product 1"
        val expectedEntity = BasketItemEntity(productName, 10, quantity = 1, imageUrl = "img1")
        val expectedFlow = flowOf(expectedEntity)
        every { basketDao.getBasketItemByName(productName) } returns expectedFlow

        // When
        val resultFlow = basketRepository.getBasketItemByName(productName)

        // Then
        resultFlow.test {
            val result = awaitItem()
            Assert.assertEquals(expectedEntity, result)
            awaitComplete()
        }
        verify(exactly = 1) { basketDao.getBasketItemByName(productName) }
    }

    @Test
    fun `addToBasket new item - inserts item into dao`() = runTest {
        // Given
        val productData = ProductData("New Product", 20, "new_img.png")
        val expectedEntity = BasketItemEntity(
            productName = productData.name,
            price = productData.price,
            imageUrl = productData.imageUrl,
            quantity = 1
        )
        every { basketDao.getBasketItemByName(productData.name) } returns flowOf(null)
        coEvery { basketDao.insert(any()) } just runs

        // When
        basketRepository.addToBasket(productData, 1)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productData.name) }
        coVerify(exactly = 1) { basketDao.insert(expectedEntity) }
        coVerify(exactly = 0) { basketDao.update(any()) } // Ensure update was not called
    }

    @Test
    fun `addToBasket existing item - updates item quantity in dao`() = runTest {
        // Given
        val productName = "Existing Product"
        val productData = ProductData(productName, 25, "existing_img.png")
        val existingItem = BasketItemEntity(productName, 25, quantity = 5, imageUrl = "existing_img.png")
        val addQuantity = 2
        val expectedQuantity = 7
        val expectedEntity = existingItem.copy(quantity = expectedQuantity)

        every { basketDao.getBasketItemByName(productName) } returns flowOf(existingItem)
        coEvery { basketDao.update(any()) } just runs

        // When
        basketRepository.addToBasket(productData, addQuantity)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 1) { basketDao.update(expectedEntity) }
        coVerify(exactly = 0) { basketDao.insert(any()) }
    }

    @Test
    fun `addToBasket existing item - quantity over 99 - does not update`() = runTest {
        // Given
        val productName = "Max Quantity Product"
        val productData = ProductData(productName, 30, "max_img.png")
        val existingItem = BasketItemEntity(productName, 30, quantity = 98, imageUrl = "max_img.png")
        val addQuantity = 3

        every { basketDao.getBasketItemByName(productName) } returns flowOf(existingItem)

        // When
        basketRepository.addToBasket(productData, addQuantity)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 0) { basketDao.update(any()) }
        coVerify(exactly = 0) { basketDao.insert(any()) }
    }

    @Test
    fun `addToBasket existing item - new quantity is exactly 99 - updates item`() = runTest {
        // Given
        val productName = "Almost Max Product"
        val productData = ProductData(productName, 25, "img.png")
        val existingItem = BasketItemEntity(productName, 25, quantity = 98, imageUrl = "img.png")
        val addQuantity = 1
        val expectedQuantity = 99
        val expectedEntity = existingItem.copy(quantity = expectedQuantity)

        every { basketDao.getBasketItemByName(productName) } returns flowOf(existingItem)
        coEvery { basketDao.update(any()) } just runs

        // When
        basketRepository.addToBasket(productData, addQuantity)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 1) { basketDao.update(expectedEntity) }
    }

    @Test
    fun `removeBasketItem item quantity greater than 1 - updates quantity in dao`() = runTest {
        // Given
        val productName = "ProductToRemove"
        val existingItem = BasketItemEntity(productName, 15, quantity = 3, imageUrl = "remove_img.png")
        val expectedEntity = existingItem.copy(quantity = existingItem.quantity - 1)

        every { basketDao.getBasketItemByName(productName) } returns flowOf(existingItem)
        coEvery { basketDao.update(any()) } just runs

        // When
        basketRepository.removeBasketItem(productName)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 1) { basketDao.update(expectedEntity) }
        coVerify(exactly = 0) { basketDao.delete(any()) }
    }

    @Test
    fun `removeBasketItem item quantity equals 1 - deletes item from dao`() = runTest {
        // Given
        val productName = "LastProductToRemove"
        val existingItem = BasketItemEntity(productName, 15, quantity = 1, imageUrl = "")

        every { basketDao.getBasketItemByName(productName) } returns flowOf(existingItem)
        coEvery { basketDao.delete(any()) } just runs

        // When
        basketRepository.removeBasketItem(productName)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 1) { basketDao.delete(existingItem) }
        coVerify(exactly = 0) { basketDao.update(any()) }
    }

    @Test
    fun `removeBasketItem item does not exist - does nothing`() = runTest {
        // Given
        val productName = "NonExistProduct"
        every { basketDao.getBasketItemByName(productName) } returns flowOf(null)

        // When
        basketRepository.removeBasketItem(productName)

        // Then
        coVerify(exactly = 1) { basketDao.getBasketItemByName(productName) }
        coVerify(exactly = 0) { basketDao.update(any()) }
        coVerify(exactly = 0) { basketDao.delete(any()) }
    }

    @Test
    fun `clearBasket calls dao clearBasket`() = runTest {
        // Given
        coEvery { basketDao.clearBasket() } just runs

        // When
        basketRepository.clearBasket()

        // Then
        coVerify(exactly = 1) { basketDao.clearBasket() }
    }

}