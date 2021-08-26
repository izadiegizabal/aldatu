package xyz.izadi.aldatu.ui.components.currencyselector

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.izadi.aldatu.ui.components.CurrencyFab
import xyz.izadi.aldatu.utils.getActivity
import xyz.izadi.aldatu.utils.hideKeyboard

@ExperimentalMaterialApi
@Composable
fun CurrencySelector(
    sheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    vm: CurrencySelectorViewModel,
    content: @Composable () -> Unit
) {
    val selectedCurrency by vm.currentCurrency.observeAsState()
    val currentAmount by vm.currentAmount.observeAsState()
    val currencyList by vm.currencyList.observeAsState()

    val activity = LocalContext.current.getActivity()
    val columnState = rememberLazyListState()

    if (!sheetState.isVisible) {
        hideKeyboard(activity)
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            LazyColumn(state = columnState) {
                item {
                    AmountSelector(
                        selectedCurrency = selectedCurrency,
                        currentAmount = currentAmount,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(16.dp),
                        onKeyboardAction = {
                            scope.launch {
                                hideKeyboard(activity)
                                sheetState.hide()
                            }
                        },
                        onValueChanged = { newValue -> vm.setNewAmount(newValue) }
                    )
                }
                currencyList?.takeIf { it.isNotEmpty() }?.let { currencies ->
                    items(currencies) {
                        CurrencyListItem(it, selectedCurrency, onClick = {
                            scope.launch {
                                sheetState.hide()
                                columnState.scrollToItem(0, 0)
                                vm.setNewCurrency(it)
                            }
                        })
                    }
                } ?: item { CurrencyListNotReady() }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                CurrencyFab(
                    scope = scope,
                    state = sheetState,
                    selectedCurrency = selectedCurrency,
                    selectedAmount = currentAmount
                )
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {
            content()
        }
    }
}

fun hideKeyboard(activity: ComponentActivity?) {
    activity?.hideKeyboard()
}
