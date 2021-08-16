package xyz.izadi.aldatu.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PriceChange
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.utils.asPrettyString

@ExperimentalMaterialApi
@Composable
fun CurrencyFab(
    scope: CoroutineScope,
    state: ModalBottomSheetState,
    selectedCurrency: Currency?,
    selectedAmount: Float?
) {
    ExtendedFloatingActionButton(
        onClick = { scope.launch { state.show() } },
        text = {
            Text(
                text = if (selectedAmount != null && selectedAmount > 0f) {
                    stringResource(
                        R.string.ms_fab_selected_amount,
                        selectedAmount.asPrettyString(),
                        selectedCurrency?.currencyCode ?: ""
                    )
                } else {
                    stringResource(R.string.ms_fab_default_text)
                }
            )
        },
        icon = {
            Icon(
                Icons.TwoTone.PriceChange,
                contentDescription = null
            )
        }
    )
}