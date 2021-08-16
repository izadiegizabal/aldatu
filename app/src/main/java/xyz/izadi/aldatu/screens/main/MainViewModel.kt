package xyz.izadi.aldatu.screens.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.repository.CurrencyRepository
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.getFormattedString
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    var refreshDate = MutableLiveData<String?>(null)
    var currencyList = MutableLiveData<List<Currency>>(null)

    val currencyRatesState = MutableLiveData<Result<List<CurrencyRate>>>()
    val currencyRates = MutableLiveData<List<CurrencyRate>>(null)

    @VisibleForTesting
    val rates = MutableLiveData<Map<String, Double>>(null)

    val currentAmount = MutableLiveData<Float?>(preferencesManager.getDefaultAmount())
    val currentCurrency = MutableLiveData(preferencesManager.getDefaultCurrency())

    init {
        loadCurrencies()
    }

    fun loadCurrencies(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getCurrencies().collect {
                if (it.status == Result.Status.SUCCESS) {
                    currencyList.postValue(it.data)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            currencyRepository.getExchangeRates(forceRefresh).collect {
                currencyRatesState.postValue(it)
                if (it.status == Result.Status.SUCCESS) {
                    rates.postValue(CurrencyRate.from(it.data))
                    refreshDate.postValue(preferencesManager.getRefreshDate()?.getFormattedString())
                    currencyRates.postValue(it.data)
                }
            }
        }
    }

    fun setNewAmount(newValueString: String) {
        val newValue = newValueString.toFloatOrNull()
        currentAmount.value = newValue
        if (newValue != null) {
            preferencesManager.setDefaultAmount(newValue)
        }
    }

    fun selectCurrency(newCurrency: Currency) {
        currentCurrency.value = newCurrency
        preferencesManager.setDefaultCurrency(newCurrency)
    }

    fun getConversion(from: Currency?, to: String): Float? {
        rates.value?.let {
            return CurrencyRate.convert(
                currentAmount.value ?: 1f,
                from?.currencyCode ?: Constants.DEFAULT_CURRENCY.currencyCode,
                to.replaceFirst(Constants.API_CURRENCYLAYER_BASECURRENCY, ""),
                Constants.API_CURRENCYLAYER_BASECURRENCY,
                it
            )
        }
        return null
    }
}