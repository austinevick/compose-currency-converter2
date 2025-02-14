package com.austinevick.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austinevick.currencyconverter.data.model.ConversionRatesModel
import com.austinevick.currencyconverter.data.model.ConversionResponseModel
import com.austinevick.currencyconverter.data.repository.CurrencyConverterRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CurrencyConverterRepository
) : ViewModel() {

    private val _currencyRateUiState = MutableStateFlow<UiState>(UiState.Loading)
    val currencyRateUiState = _currencyRateUiState.asStateFlow()

    private val _conversionUiState = MutableStateFlow<UiState>(UiState.Initial)
    val conversionUiState = _conversionUiState.asStateFlow()

    var currencyKey = MutableStateFlow(CurrencyKey.From.key)
        private set
    var fromCurrency = MutableStateFlow<Currency>(Currency("USD", 1.0))
        private set
    var toCurrency = MutableStateFlow<Currency>(Currency("USD", 1.0))
        private set


    init {
        getConversionRates()
    }

    fun setCurrencyType(key: String) {
        currencyKey.value = key
    }

    fun updateFromCurrency(amount: String) {
        fromCurrency.update {
            if (amount.isEmpty())
                Currency(it.code, 1.0)
            else
                Currency(it.code, amount.toDouble())
        }
    }

    fun flipCurrency() {
        val temp = fromCurrency.value
        fromCurrency.value = toCurrency.value
        toCurrency.value = temp
    }


    fun convertCurrency(amount: Double) {
        viewModelScope.launch {
            try {
                _conversionUiState.update {
                    UiState.Loading
                }
                val response = repository.convertCurrency(
                    fromCurrency.value.code,
                    toCurrency.value.code,
                    amount
                )
                if (response.isSuccessful) {
                    val json = Gson().toJson(response.body())
                    val data = Gson().fromJson(json, ConversionResponseModel::class.java)
                    Log.d(HomeViewModel::class.java.name, data.toString())
                    _conversionUiState.update {
                        UiState.Success<ConversionResponseModel>(data = data)
                    }
                } else {
                    Log.e(HomeViewModel::class.java.name, response.message())
                }

            } catch (e: HttpException) {
                Log.e(HomeViewModel::class.java.name, e.message())
                _conversionUiState.update {
                    UiState.Error("Something went wrong!")
                }
            }
        }
    }

    private fun getConversionRates() {
        viewModelScope.launch {
            try {
                val response = repository.getConversionRates(fromCurrency.value.code)
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d(HomeViewModel::class.java.name, data.toString())
                    _currencyRateUiState.update {
                        UiState.Success<ConversionRatesModel>(data = data as ConversionRatesModel)
                    }
                }
            } catch (e: Exception) {
                Log.e(HomeViewModel::class.java.name, e.message.toString())
                _currencyRateUiState.update {
                    UiState.Error("Something went wrong!")
                }
            }
        }
    }


}


data class Currency(
    val code: String,
    val rate: Double
)

sealed class UiState {
    data object Initial : UiState()
    data object Loading : UiState()
    data class Error(val message: String) : UiState()
    data class Success<T>(val data: T) : UiState()
}

enum class CurrencyKey(val key: String) {
    From("from"),
    To("to")
}


