package com.austinevick.currencyconverter.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.austinevick.currencyconverter.R
import com.austinevick.currencyconverter.common.GreyColor
import com.austinevick.currencyconverter.data.model.ConversionResponseModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val amount = remember { mutableStateOf("") }
    val showModal = remember { mutableStateOf(false) }

    val fromCurrency = viewModel.fromCurrency.collectAsStateWithLifecycle().value
    val toCurrency = viewModel.toCurrency.collectAsStateWithLifecycle().value
    val currencyKey = viewModel.currencyKey.collectAsStateWithLifecycle().value
    val uiState = viewModel.conversionUiState.collectAsStateWithLifecycle().value


   if(amount.value.isNotBlank()) LaunchedEffect(amount.value) {
        delay(1000)
        viewModel.convertCurrency(amount.value.toDouble())
    }


    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults
                    .topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        "Currency Converter",
                        style = MaterialTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.W600)
                    )
                })
        }) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
                .imePadding()
        ) {
            Spacer(Modifier.height(20.dp))

            CurrencyCard(
                leading = "From",
                currency = fromCurrency.code,
                amount = fromCurrency.rate.toString(),
                onCurrencyChange = {
                    viewModel.setCurrencyType("from")
                    showModal.value = true
                })
            Spacer(Modifier.height(16.dp))

            Image(painterResource(R.drawable.switch_arrow), "Switch Currency",
                modifier.clickable { viewModel.flipCurrency() })
            Spacer(Modifier.height(16.dp))
            CurrencyCard(
                leading = "To",
                currency = toCurrency.code,
                amount = toCurrency.rate.toString(),
                onCurrencyChange = {
                    viewModel.setCurrencyType("to")
                    showModal.value = true
                })
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = amount.value,
                onValueChange = {
                    amount.value = it
                    viewModel.updateFromCurrency(it)
                },
                placeholder = {
                    Text(
                        "Enter amount", color = Color.LightGray,
                        fontSize = 25.sp, fontWeight = FontWeight.W600
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .height(68.dp),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.W800),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    focusedIndicatorColor = GreyColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = GreyColor
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedCard(
                modifier = modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, color = GreyColor),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        "Indicative exchange rate",
                        fontWeight = FontWeight.W600,
                        color = Color.Gray
                    )

                    if (uiState is UiState.Success<*>) {
                        val data = uiState.data as ConversionResponseModel
                        Text(
                            "${amount.value} ${data.baseCode} = ${data.conversionResult} ${data.targetCode}",
                            modifier.align(Alignment.CenterHorizontally),
                            fontWeight = FontWeight.W800
                        )
                    } else
                        Text(
                            "${fromCurrency.rate} ${fromCurrency.code} = ${toCurrency.rate} ${toCurrency.code}",
                            modifier.align(Alignment.CenterHorizontally),
                            fontWeight = FontWeight.W800,
                        )
                }
            }
            Spacer(Modifier.height(25.dp))

            Button(
                onClick = { viewModel.convertCurrency(amount.value.toDouble()) },
                shape = RoundedCornerShape(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults
                    .buttonColors(containerColor = Color(0xff26278D))
            ) {
                if (uiState is UiState.Loading)
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = modifier.size(30.dp)
                    )
                else
                    Text("Convert")
            }

            if (showModal.value) {
                CurrencyListModal(
                    onItemSelect = {
                        if (currencyKey == "from")
                            viewModel.fromCurrency.value = it
                        else
                            viewModel.toCurrency.value = it
                    }
                ) { showModal.value = false }
            }

        }
    }
}

@Composable
fun CurrencyCard(
    leading: String, currency: String,
    amount: String, onCurrencyChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, color = GreyColor),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier.padding(12.dp)) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(leading, fontWeight = FontWeight.W600)
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    onClick = onCurrencyChange
                ) {
                    Text(
                        currency,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.W800
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Black)
                }
            }
            Text(amount, fontWeight = FontWeight.W600)
        }
    }

}