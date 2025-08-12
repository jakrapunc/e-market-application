package com.work.stores_service.repository.remote

import com.work.network.base.ApiManager
import com.work.stores_service.data.model.ProductData
import com.work.stores_service.data.model.StoreInfoData
import com.work.stores_service.data.model.request.OrderBody
import com.work.stores_service.data.service.api.ProductService
import com.work.stores_service.data.service.repository.remote.ProductRemote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRemoteTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var apiManager: ApiManager
    @MockK
    private lateinit var mockProductService: ProductService

    @MockK
    private lateinit var retrofit: Retrofit
    private lateinit var productRemote: ProductRemote

    // To capture the converter factory if needed for verification
    private val converterFactorySlot = slot<Converter.Factory>()

    @Before
    fun setUp() {
        every {
            apiManager.init(
                baseUrl = any(),
                converter = capture(converterFactorySlot)
            )
        } returns retrofit

        every { retrofit.create(ProductService::class.java) } returns mockProductService

        productRemote = ProductRemote(apiManager)
    }

    @Test
    fun `getProductService initializes ApiManager and creates ProductService`() {
        // When
        val service = productRemote.getProductService()

        // Then
        Assert.assertEquals(mockProductService, service) // Ensure the mocked service is returned

        verify {
            apiManager.init(
                baseUrl = "https://c8d92d0a-6233-4ef7-a229-5a91deb91ea1.mock.pstmn.io",
                converter = any() // We captured it, can assert on converterFactorySlot.captured
            )
        }

        Assert.assertTrue(converterFactorySlot.captured is GsonConverterFactory) // Verify the type of converter
        verify { retrofit.create(ProductService::class.java) }
    }

    @Test
    fun `getStoreInfo calls service getStoreInfo and returns its response`() = runTest {
        // Given
        val expectedResponse: Response<StoreInfoData> = Response.success(StoreInfoData(
            name = "The Coffee Shop",
            rating = 4.5,
            openingTime = "15:01:01.772Z",
            closingTime = "19:45:51.365Z",
        ))
        coEvery { mockProductService.getStoreInfo() } returns expectedResponse

        // When
        val actualResponse = productRemote.getStoreInfo()

        // Then
        Assert.assertEquals(expectedResponse, actualResponse)
        coVerify { mockProductService.getStoreInfo() }
    }

    @Test
    fun `getProducts calls service getProducts and returns its response`() = runTest {
        // Given
        val expectedResponse: Response<List<ProductData>> = Response.success(
            listOf(
                ProductData("Latte", 50, "latte.jpg"),
                ProductData("Mocha", 60, "mocha.jpg")
            )
        )
        coEvery { mockProductService.getProducts() } returns expectedResponse

        // When
        val actualResponse = productRemote.getProducts()

        // Then
        Assert.assertEquals(expectedResponse, actualResponse)
        coVerify { mockProductService.getProducts() }
    }

    @Test
    fun `createOrder calls service createOrder with body and returns its response`() = runTest {
        // Given
        val orderBody = OrderBody(products = emptyList(), deliveryAddress = "Test Address")
        val expectedResponse: Response<String> = Response.success(
            "Success"
        )
        coEvery { mockProductService.createOrder(orderBody) } returns expectedResponse

        // When
        val actualResponse = productRemote.createOrder(orderBody)

        // Then
        Assert.assertEquals(expectedResponse, actualResponse)
        coVerify { mockProductService.createOrder(orderBody) }
    }
}