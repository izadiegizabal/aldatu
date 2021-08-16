package xyz.izadi.aldatu.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.screens.main.MainViewModel
import xyz.izadi.aldatu.utils.asPrettyString
import xyz.izadi.aldatu.utils.getActivity
import xyz.izadi.aldatu.utils.hideKeyboard

@ExperimentalMaterialApi
@Composable
fun CurrencySelector(
    sheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    vm: MainViewModel,
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
                                vm.selectCurrency(it)
                            }
                        })
                    }
                } ?: item { NotReady { CircularProgressIndicator() } }
            }
        }
    ) {
        content()
    }
}

fun hideKeyboard(activity: ComponentActivity?) {
    activity?.hideKeyboard()
}

@Composable
private fun NotReady(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(256.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@Composable
private fun AmountSelector(
    selectedCurrency: Currency?,
    currentAmount: Float?,
    modifier: Modifier = Modifier,
    onKeyboardAction: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = currentAmount?.asPrettyString() ?: "",
        label = { Text(text = stringResource(R.string.cs_amount_placeholder)) },
        trailingIcon = { Text(text = selectedCurrency?.currencyCode ?: "") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions { onKeyboardAction() },
        onValueChange = { newValue -> onValueChanged(newValue) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun CurrencyListItem(currency: Currency, selectedCurrency: Currency?, onClick: () -> Unit) {
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
