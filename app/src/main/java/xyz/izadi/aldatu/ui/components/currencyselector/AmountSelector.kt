package xyz.izadi.aldatu.ui.components.currencyselector

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.data.local.Currency

@Composable
fun AmountSelector(
    selectedCurrency: Currency?,
    modifier: Modifier = Modifier,
    startValue: String = "",
    onKeyboardAction: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    var currentAmountString by remember { mutableStateOf(startValue) }

    OutlinedTextField(
        modifier = modifier,
        value = currentAmountString,
        label = { Text(text = stringResource(R.string.cs_amount_placeholder)) },
        trailingIcon = { Text(text = selectedCurrency?.currencyCode ?: "") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions { onKeyboardAction() },
        onValueChange = { newValue ->
            currentAmountString = newValue
            onValueChanged(newValue)
        }
    )
}