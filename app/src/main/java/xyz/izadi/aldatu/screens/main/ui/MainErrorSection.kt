package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.runtime.Composable
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.ui.components.messagesections.ErrorSection

@Composable
fun MainErrorSection(vm: MainViewModel, rates: List<CurrencyRate>?) {
    ErrorSection(
        onRetry = { vm.loadCurrencies(true) },
        onSecondary = if (rates?.isNotEmpty() == true) {
            { vm.loadCurrencies((false)) }
        } else {
            null
        }
    )
}