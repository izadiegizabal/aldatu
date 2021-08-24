package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.ui.components.SquareCard
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.asPrettyString

@Composable
fun CurrencyRow(
    rates: List<CurrencyRate>,
    vm: MainViewModel,
    selectedCurrency: Currency?
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        rates.forEach {
            SquareCard(
                title = it.currencyCode.replaceFirst(Constants.API_CURRENCYLAYER_BASECURRENCY, ""),
                subtitle = vm.getConversion(selectedCurrency, it.currencyCode)?.asPrettyString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}