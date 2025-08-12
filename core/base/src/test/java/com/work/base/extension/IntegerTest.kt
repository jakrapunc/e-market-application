package com.work.base.extension

import org.junit.Assert
import org.junit.Test

class IntegerTest {
    @Test
    fun `toPriceString formats zero correctly`() {
        val number = 0
        val expected = "0"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats number less than 1000 correctly`() {
        val number = 123
        val expected = "123"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats number equal to 1000 correctly`() {
        val number = 1000
        val expected = "1,000"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats number greater than 1000 correctly`() {
        val number = 12345
        val expected = "12,345"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats large number with multiple separators correctly`() {
        val number = 1234567
        val expected = "1,234,567"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats another large number correctly`() {
        val number = 987654321
        val expected = "987,654,321"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats negative number correctly`() {
        // DecimalFormat typically handles negative numbers by prefixing with a minus sign.
        val number = -12345
        val expected = "-12,345"
        Assert.assertEquals(expected, number.toPriceString())
    }

    @Test
    fun `toPriceString formats negative number less than -1000 correctly`() {
        val number = -123
        val expected = "-123"
        Assert.assertEquals(expected, number.toPriceString())
    }
}