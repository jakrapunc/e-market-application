package com.work.base.extension

import java.text.DecimalFormat

fun Int.toPriceString(): String {
    val pattern = "#,##0"
    val decimalFormatter = DecimalFormat(pattern)

    return decimalFormatter.format(this)
}