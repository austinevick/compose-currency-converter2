package com.austinevick.currencyconverter.data.model

import com.google.gson.annotations.SerializedName


data class ConversionRatesModel (
    @SerializedName("result"                ) var result             : String?          = null,
    @SerializedName("base_code"             ) var baseCode           : String?          = null,
    @SerializedName("conversion_rates"      ) var conversionRates    : Map<String, Double>? = null
)

