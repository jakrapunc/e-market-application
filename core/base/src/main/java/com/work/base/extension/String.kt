package com.work.base.extension

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String?.formatDateTime(): String? {
    return try {
        this?.let {
            val inputPattern = "HH:mm:ss.SSS'Z'"
            val inputFormat = SimpleDateFormat(
                inputPattern,
                Locale.getDefault()
            )
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = inputFormat.parse(this)

            val outputPattern = "HH.mm"
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("UTC")

            date?.let { outputFormat.format(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}