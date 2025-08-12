package com.work.stores_service.repository

import app.cash.turbine.test
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.repository.ProductRepository
import com.work.stores_service.data.service.repository.remote.IProductRemote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var productRemote: IProductRemote
    private lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        productRepository = ProductRepository(productRemote)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // --- getStoreInfo Tests ---

    @Test
    fun `getStoreInfo success - calls remote and returns data from flow`() = runTest {
        // Given
        val mockStoreInfoData = StoreInfoData(
            name = "Test Coffee Shop",
            rating = 4.8,
            openingTime = "2023-01-01T08:00:00.000Z",
            closingTime = "2023-01-01T18:00:00.000Z"
        )
        val successResponse = Response.success(mockStoreInfoData)
        coEvery { productRemote.getStoreInfo() } returns successResponse

        // When
        val resultFlow = productRepository.getStoreInfo()

        // Then
        resultFlow.test {
            val result = awaitItem()
            Assert.assertEquals(mockStoreInfoData, result)
            awaitComplete()
        }
        coVerify(exactly = 1) { productRemote.getStoreInfo() }
    }

    @Test
    fun `getStoreInfo remote error - calls remote, flow emits error`() = runTest {
        // Given
        val errorMessage = "Network Error"
        val errorResponseBody = """{"error":"$errorMessage"}""".toResponseBody(null)
        val errorResponse = Response.error<StoreInfoData>(400, errorResponseBody)
        coEvery { productRemote.getStoreInfo() } returns errorResponse

        // When
        val resultFlow = productRepository.getStoreInfo()
        advanceUntilIdle()

        // Then
        resultFlow.test {
            val error = awaitError()
            Assert.assertTrue(error is Exception)
        }
        coVerify(exactly = 1) { productRemote.getStoreInfo() }
    }

    @Test
    fun `getStoreInfo remote throws IOException - calls remote, flow emits error`() = runTest {
        // Given
        val exception = IOException("Network failure")
        coEvery { productRemote.getStoreInfo() } throws exception

        // When
        val resultFlow = productRepository.getStoreInfo()

        // Then
        resultFlow.test {
            val emittedError = awaitError()
            Assert.assertTrue(emittedError is IOException)
        }
        coVerify(exactly = 1) { productRemote.getStoreInfo() }
    }

    // --- getProducts Tests ---

    @Test
    fun `getProducts success - calls remote and returns data from flow`() = runTest {
        // Given
        val mockProductList = listOf(
            ProductData("Latte", 50, "latte.jpg"),
            ProductData("Mocha", 60, "mocha.jpg")
        )
        val successResponse = Response.success(mockProductList)
        coEvery { productRemote.getProducts() } returns successResponse

        // When
        val resultFlow = productRepository.getProducts()

        // Then
        resultFlow.test {
            val result = awaitItem()
            Assert.assertEquals(mockProductList, result)
            awaitComplete()
        }
        coVerify(exactly = 1) { productRemote.getProducts() }
    }

    @Test
    fun `getProducts remote error - calls remote, flow emits error`() = runTest {
        // Given
        val errorResponseBody = """{"error":"Product fetch error"}""".toResponseBody(null)
        val errorResponse = Response.error<List<ProductData>>(404, errorResponseBody)
        coEvery { productRemote.getProducts() } returns errorResponse

        // When
        val resultFlow = productRepository.getProducts()

        // Then
        resultFlow.test {
            val error = awaitError()
            Assert.assertTrue(error is Exception)
        }
        coVerify(exactly = 1) { productRemote.getProducts() }
    }

    @Test
    fun `getProducts remote throws IOException - calls remote, flow emits error`() = runTest {
        // Given
        val exception = IOException("Product network failure")
        coEvery { productRemote.getProducts() } throws exception

        // When
        val resultFlow = productRepository.getProducts()

        // Then
        resultFlow.test {
            val emittedError = awaitError()
            Assert.assertTrue(emittedError is IOException)
        }

        coVerify(exactly = 1) { productRemote.getProducts() }
    }

    @Test
    fun `createOrder success - calls remote and returns data from flow`() = runTest {
        // Given
        val orderBody = OrderBody(
            products = listOf(ProductData("Latte", 50, "latte.jpg")),
            deliveryAddress = "123 Main St"
        )
        val successMessage = "Success"
        val successResponse = Response.success(successMessage)
        coEvery { productRemote.createOrder(orderBody) } returns successResponse

        // When
        val resultFlow = productRepository.createOrder(orderBody)

        // Then
        resultFlow.test {
            Assert.assertEquals(successMessage, awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { productRemote.createOrder(orderBody) }
    }

    @Test
    fun `createOrder remote error - calls remote, flow emits error`() = runTest {
        // Given
        val orderBody = OrderBody(products = emptyList(), deliveryAddress = "Addr")
        val errorResponseBody = """{"error":"Order creation failed"}""".toResponseBody(null)
        val errorResponse = Response.error<String>(500, errorResponseBody)
        coEvery { productRemote.createOrder(orderBody) } returns errorResponse

        // When
        val resultFlow = productRepository.createOrder(orderBody)

        // Then
        resultFlow.test {
            val error = awaitError()
            Assert.assertTrue(error is Exception)
        }
        coVerify(exactly = 1) { productRemote.createOrder(orderBody) }
    }

    @Test
    fun `createOrder remote throws IOException - calls remote, flow emits error`() = runTest {
        // Given
        val orderBody = OrderBody(products = emptyList(), deliveryAddress = "Addr")
        val exception = IOException("Failure")
        coEvery { productRemote.createOrder(orderBody) } throws exception

        // When
        val resultFlow = productRepository.createOrder(orderBody)

        // Then
        resultFlow.test {
            val emittedError = awaitError()
            Assert.assertTrue(emittedError is IOException)
        }
        coVerify(exactly = 1) { productRemote.createOrder(orderBody) }
    }
}