package com.work.base.extension

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class StringTest {
    private lateinit var originalDefaultTimeZone: TimeZone
    private lateinit var originalDefaultLocale: Locale

    @Before
    fun setUp() {
        // Save original default timezone and locale
        originalDefaultTimeZone = TimeZone.getDefault()
        originalDefaultLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        // Restore original default timezone and locale
        TimeZone.setDefault(originalDefaultTimeZone)
        Locale.setDefault(originalDefaultLocale)
    }

    @Test
    fun `formatDateTime with valid UTC string returns correctly formatted time`() {
        val inputTimeString = "14:30:15.123Z"
        val expectedFormattedTime = "14.30"
        Assert.assertEquals(expectedFormattedTime, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with valid UTC string at midnight returns correctly formatted time`() {
        val inputTimeString = "00:00:00.000Z"
        val expectedFormattedTime = "00.00"
        Assert.assertEquals(expectedFormattedTime, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with valid UTC string just before midnight returns correctly formatted time`() {
        val inputTimeString = "23:59:59.999Z"
        val expectedFormattedTime = "23.59"
        Assert.assertEquals(expectedFormattedTime, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with null string returns null`() {
        val inputTimeString: String? = null
        Assert.assertNull(inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with empty string returns empty string (due to parse error)`() {
        val inputTimeString = ""
        Assert.assertEquals(inputTimeString, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with invalid time format string returns original string`() {
        val inputTimeString = "INVALID_DATA"
        Assert.assertEquals(inputTimeString, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with incorrect format returns original string`() {
        val inputTimeString = "14:30Z"
        Assert.assertEquals(inputTimeString, inputTimeString.formatDateTime())
    }

    @Test
    fun `formatDateTime with different valid milliseconds`() {
        val inputTimeString1 = "10:00:00.001Z"
        val expectedFormattedTime1 = "10.00"
        Assert.assertEquals(expectedFormattedTime1, inputTimeString1.formatDateTime())

        val inputTimeString2 = "19:45:30.987Z"
        val expectedFormattedTime2 = "19.45"
        Assert.assertEquals(expectedFormattedTime2, inputTimeString2.formatDateTime())
    }
}