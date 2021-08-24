package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.ui.components.messagesections.LoadingSection
import xyz.izadi.aldatu.ui.components.messagesections.SelectAmountFirstSection

@Composable
fun MainView(vm: MainViewModel) {
    val currencyRatesState by vm.currencyRatesState.observeAsState()
    val selectedCurrency by vm.currentCurrency.observeAsState()
    val refreshDate by vm.refreshDate.observeAsState()
    val currentAmount by vm.currentAmount.observeAsState()
    val rates by vm.currencyRates.observeAsState()

    LazyColumn {
        item { MainHeader(refreshDate) }
        currentAmount?.takeIf { it > 0f }?.let {
            when (currencyRatesState?.status) {
                Result.Status.LOADING -> {
                    item { LoadingSection() }
                }
                Result.Status.ERROR -> {
                    item { MainErrorSection(vm, rates) }
                }
                else -> {
                    rates?.takeIf { it.isNotEmpty() }?.let { rates ->
                        items(rates.chunked(3)) { rowRates ->
                            CurrencyRow(rowRates, vm, selectedCurrency)
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    } ?: item {
                        MainErrorSection(vm, rates)
                    }
                }
            }
        } ?: item { SelectAmountFirstSection() }
    }
}