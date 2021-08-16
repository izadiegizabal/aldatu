package xyz.izadi.aldatu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.asPrettyString

@Composable
fun MainView(vm: MainViewModel) {
    val currencyRatesState by vm.currencyRatesState.observeAsState()
    val selectedCurrency by vm.currentCurrency.observeAsState()
    val refreshDate by vm.refreshDate.observeAsState()
    val currentAmount by vm.currentAmount.observeAsState()
    val rates by vm.currencyRates.observeAsState()

    LazyColumn {
        item { Header(refreshDate) }
        currentAmount?.takeIf { it > 0f }?.let {
            when (currencyRatesState?.status) {
                Result.Status.SUCCESS -> {
                    rates?.let { rates ->
                        if (rates.isEmpty()) {
                            item { DefaultErrorSection(vm = vm, rates = rates) }
                        }
                        items(rates.chunked(3)) { rowRates ->
                            CurrencyCardRows(rowRates, vm, selectedCurrency)
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    } ?: item {
                        DefaultErrorSection(vm, rates)
                    }
                }
                Result.Status.ERROR -> {
                    item {
                        DefaultErrorSection(vm, rates)
                    }
                }
                else -> {
                    item { LoadingSection() }
                }
            }
        } ?: item { SelectAnAmountFirst() }
    }
}

@Composable
fun DefaultErrorSection(vm: MainViewModel, rates: List<CurrencyRate>?) {
    ErrorSection(
        onRetry = { vm.loadCurrencies(true) },
        onSecondary = if (rates?.isNotEmpty() == true) {
            { vm.loadCurrencies((false)) }
        } else {
            null
        }
    )
}

@Composable
fun CurrencyCardRows(rates: List<CurrencyRate>, vm: MainViewModel, selectedCurrency: Currency?) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        rates.forEach {
            CurrencyCard(
                rate = it,
                vm = vm,
                selectedCurrency = selectedCurrency,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CurrencyCard(
    rate: CurrencyRate,
    vm: MainViewModel,
    selectedCurrency: Currency?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = rate.currencyCode.replaceFirst(Constants.API_CURRENCYLAYER_BASECURRENCY, ""),
                style = MaterialTheme.typography.h5
            )
            Text(
                text = vm.getConversion(selectedCurrency, rate.currencyCode)?.asPrettyString()
                    ?: "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2
            )
        }
    }
}