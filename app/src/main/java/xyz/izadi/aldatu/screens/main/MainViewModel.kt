package xyz.izadi.aldatu.screens.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyListUseCase
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyRatesUseCase
import xyz.izadi.aldatu.ui.components.currencyselector.CurrencySelectorViewModel
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.getFormattedString
import xyz.izadi.aldatu.utils.postValue
import xyz.izadi.aldatu.utils.setValue
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchCurrencyListUseCase: FetchCurrencyListUseCase,
    private val fetchCurrencyRatesUseCase: FetchCurrencyRatesUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel(), CurrencySelectorViewModel {

    @VisibleForTesting
    val rates: LiveData<Map<String, Double>?> by lazy {
        MutableLiveData(null)
    }
    val refreshDate: LiveData<String?> by lazy {
        MutableLiveData(null)
    }
    val currencyRatesState: LiveData<Result<List<CurrencyRate>>?> by lazy {
        MutableLiveData(null)
    }
    val currencyRates: LiveData<List<CurrencyRate>?> by lazy {
        MutableLiveData(null)
    }
    override val currencyList: LiveData<List<Currency>?> by lazy {
        MutableLiveData(null)
    }
    override val currentAmount: LiveData<Float?> by lazy {
        MutableLiveData(preferencesManager.getDefaultAmount())
    }
    override val currentCurrency: LiveData<Currency?> by lazy {
        MutableLiveData(preferencesManager.getDefaultCurrency())
    }

    init {
        loadCurrencies()
    }

    fun loadCurrencies(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            fetchCurrencyListUseCase.invoke()
                .flowOn(Dispatchers.IO).collect {
                    if (it.status == Result.Status.SUCCESS) {
                        currencyList.postValue(it.data)
                    }
                }
        }
        viewModelScope.launch {
            fetchCurrencyRatesUseCase.invoke(forceRefresh)
                .flowOn(Dispatchers.IO).collect {
                    currencyRatesState.postValue(it)
                    if (it.status == Result.Status.SUCCESS) {
                        rates.postValue(CurrencyRate.from(it.data))
                        refreshDate.postValue(
                            preferencesManager.getRefreshDate()?.getFormattedString()
                        )
                        currencyRates.postValue(it.data)
                    }
                }
        }
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

    override fun setNewAmount(newValueString: String) {
        val newValue = newValueString.toFloatOrNull()
        currentAmount.setValue(newValue)
        if (newValue != null) {
            preferencesManager.setDefaultAmount(newValue)
        }
    }

    override fun setNewCurrency(newCurrency: Currency) {
        currentCurrency.setValue(newCurrency)
        preferencesManager.setDefaultCurrency(newCurrency)
    }
}