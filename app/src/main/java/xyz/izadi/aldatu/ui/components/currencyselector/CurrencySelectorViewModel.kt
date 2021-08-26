package xyz.izadi.aldatu.ui.components.currencyselector

import androidx.lifecycle.LiveData
import xyz.izadi.aldatu.data.local.Currency

interface CurrencySelectorViewModel {
    val currentCurrency: LiveData<Currency?>
    val currentAmount: LiveData<Float?>
    val currencyList: LiveData<List<Currency>?>

    fun setNewCurrency(newCurrency: Currency)
    fun setNewAmount(newValueString: String)
}