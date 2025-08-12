package com.work.stores_service.data.model.request

import com.google.gson.annotations.SerializedName
import com.work.stores_service.data.model.ProductData

data class OrderBody(
    @SerializedName("products")
    val products: List<ProductData>,
    @SerializedName("delivery_address")
    val deliveryAddress: String
)