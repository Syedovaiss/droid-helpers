package com.vend.movanos.customer.data.geocoding.geocoding

import com.google.gson.annotations.SerializedName

data class GeoCodingModel(
    @SerializedName("plus_code")
    val plusCode: PlusCode,
    val results: List<Result>,
    val status: String
)