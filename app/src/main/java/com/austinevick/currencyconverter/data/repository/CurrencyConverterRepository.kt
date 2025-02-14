package com.austinevick.currencyconverter.data.repository

import com.austinevick.currencyconverter.data.model.ConversionRatesModel
import com.austinevick.currencyconverter.data.model.ConversionResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyConverterRepository {

    @GET("latest/{baseCurrency}")
    suspend fun getConversionRates(
        @Path("baseCurrency") baseCurrency: String
    ): Response<ConversionRatesModel>

    @GET("pair/{baseCurrency}/{targetCurrency}/{amount}")
    suspend fun convertCurrency(
        @Path("baseCurrency") baseCurrency: String,
        @Path("targetCurrency") targetCurrency: String,
        @Path("amount") amount: Double
    ): Response<ConversionResponseModel>


}