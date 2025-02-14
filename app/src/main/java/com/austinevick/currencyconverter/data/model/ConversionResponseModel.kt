package com.austinevick.currencyconverter.data.model


import com.google.gson.annotations.SerializedName

data class ConversionResponseModel(
    @SerializedName("base_code")
    val baseCode: String? = null,
    @SerializedName("conversion_rate")
    val conversionRate: Double? = null,
    @SerializedName("conversion_result")
    val conversionResult: Double? = null,
    @SerializedName("result")
    val result: String? = null,
    @SerializedName("target_code")
    val targetCode: String? = null
)