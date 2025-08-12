package com.work.network.base

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor

class NetworkHttpLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (!message.startsWith("{") && !message.startsWith("[") && !message.startsWith("\r")) {
            Log.d(TAG, message)
            return
        }
        try {
            val prettyPrintJson = GsonBuilder().setPrettyPrinting().create()
                .toJson(JsonParser.parseString(message))
            Log.d(TAG, prettyPrintJson)
        } catch (m: JsonSyntaxException) {
            m.printStackTrace()
            Log.d(TAG, message)
        }
    }

    companion object {
        private val TAG = NetworkHttpLogger::class.java.simpleName
    }
}