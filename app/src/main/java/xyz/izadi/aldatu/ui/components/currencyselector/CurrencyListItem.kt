package xyz.izadi.aldatu.ui.components.currencyselector

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.izadi.aldatu.data.local.Currency

@ExperimentalMaterialApi
@Composable
fun CurrencyListItem(currency: Currency, selectedCurrency: Currency?, onClick: () -> Unit) {
    ListItem(
        modifier = if (currency.currencyCode == selectedCurrency?.currencyCode) {
            Modifier
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small
                )
                .background(MaterialTheme.colors.primary.copy(alpha = 0.08f))
        } else {
            Modifier
        }.clickable {
            onClick()
        },
        icon = { Text(text = currency.currencyCode) },
        text = { Text(text = currency.currencyName ?: "") }
    )
}