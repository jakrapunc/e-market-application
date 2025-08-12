package com.work.stores_service.data.model

import com.google.gson.annotations.SerializedName

data class ProductData(
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("imageUrl")
    val imageUrl: String
)