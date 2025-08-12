package com.work.stores_service.data.model

import com.google.gson.annotations.SerializedName
import kotlin.String

data class StoreInfoData(
    @SerializedName("name")
    val name: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("openingTime")
    val openingTime: String,
    @SerializedName("closingTime")
    val closingTime: String
)