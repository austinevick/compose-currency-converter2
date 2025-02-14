package com.austinevick.currencyconverter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.austinevick.currencyconverter.data.model.ConversionRates
import com.austinevick.currencyconverter.data.model.ConversionRatesModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListModal(
    viewModel: HomeViewModel = hiltViewModel(),
    onItemSelect:(Currency)-> Unit,
    onDismiss: () -> Unit
) {

    val uiState = viewModel.currencyRateUiState.collectAsStateWithLifecycle()


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            when (uiState.value) {
                UiState.Initial -> {}
                UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> {}
                is UiState.Success<*> -> {
                    val data = uiState.value as UiState.Success<ConversionRatesModel>

                    data.data.conversionRates?.forEach {

                        ListItem(
                            modifier = Modifier.clickable{
                               onItemSelect(Currency(it.key.toString(),it.value))
                                onDismiss()
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.White),
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xff26278D), CircleShape)
                                ) {
                                    Text(it.key.substring(0, 1), color = Color.White)

                                }
                            },
                            trailingContent = {
                                Text(it.value.toString())
                            },
                            headlineContent = {
                                Text(it.key)
                            })
                    }

                }
            }


        }


    }
}