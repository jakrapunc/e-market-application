package com.work.stores_service.repository.local

import com.work.stores_service.data.service.repository.local.BASKET_DATABASE
import com.work.stores_service.data.service.repository.local.BasketDao
import com.work.stores_service.data.service.repository.local.BasketDatabase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class BasketDatabaseTest {

    @Test
    fun `basketDao abstract method is defined and can be mocked`() {
        // Given
        val mockBasketDao = mockk<BasketDao>() // Create a mock for the DAO
        val mockBasketDatabase = mockk<BasketDatabase>() // Create a mock for the Database

        // When
        every { mockBasketDatabase.basketDao() } returns mockBasketDao

        val returnedDao = mockBasketDatabase.basketDao()

        // Then
        verify(exactly = 1) { mockBasketDatabase.basketDao() }

        Assert.assertEquals(mockBasketDao, returnedDao)
        Assert.assertNotNull(returnedDao) // Also a good check
    }

    @Test
    fun `BASKET_DATABASE constant has correct value`() {
        Assert.assertEquals("e-market-db", BASKET_DATABASE)
    }
}