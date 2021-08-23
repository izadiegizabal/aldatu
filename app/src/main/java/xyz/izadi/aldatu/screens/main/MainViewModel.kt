package xyz.izadi.aldatu.screens.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
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
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyListUseCase
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyRatesUseCase
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.getFormattedString
import xyz.izadi.aldatu.utils.postValue
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrencyListUseCase: FetchCurrencyListUseCase,
    private val fetchCurrencyRatesUseCase: FetchCurrencyRatesUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val refreshDate: LiveData<String?> by lazy {
        MutableLiveData(null)
    }

    val currencyList: LiveData<List<Currency>> by lazy {
        MutableLiveData(null)
    }

    val currencyRatesState: LiveData<Result<List<CurrencyRate>>> by lazy {
        MutableLiveData()
    }
    val currencyRates: LiveData<List<CurrencyRate>> by lazy {
        MutableLiveData(null)
    }

    @VisibleForTesting
    val rates: LiveData<Map<String, Double>> by lazy {
        MutableLiveData(null)
    }

    val currentAmount: LiveData<Float?> by lazy {
        MutableLiveData(preferencesManager.getDefaultAmount())
    }
    val currentCurrency: LiveData<Currency> by lazy {
        MutableLiveData(preferencesManager.getDefaultCurrency())
    }

    init {
        loadCurrencies()
    }

    fun loadCurrencies(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrencyListUseCase.invoke().collect {
                if (it.status == Result.Status.SUCCESS) {
                    currencyList.postValue(it.data)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            fetchCurrencyRatesUseCase.invoke(forceRefresh).collect {
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
        currentAmount.postValue(newValue)
        if (newValue != null) {
            preferencesManager.setDefaultAmount(newValue)
        }
    }

    fun selectCurrency(newCurrency: Currency) {
        currentCurrency.postValue(newCurrency)
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