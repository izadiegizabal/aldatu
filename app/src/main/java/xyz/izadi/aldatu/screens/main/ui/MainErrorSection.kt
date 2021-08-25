package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.ui.components.messagesections.ErrorSection

@Composable
fun MainErrorSection(vm: MainViewModel, showSecondary: Boolean) {
    ErrorSection(
        primaryText = stringResource(id = R.string.ms_btn_error_fetching_rates_try_again),
        secondaryText = stringResource(id = R.string.ms_btn_error_fetching_not_optimal),
        onPrimary = { vm.loadCurrencies(true) },
        onSecondary = if (showSecondary) {
            { vm.loadCurrencies((false)) }
        } else {
            null
        }
    )
}