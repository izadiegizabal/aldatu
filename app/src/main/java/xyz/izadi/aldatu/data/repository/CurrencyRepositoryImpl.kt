package xyz.izadi.aldatu.data.repository

import android.content.Context
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyListDao
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.CurrencyRatesDao
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.repository.ErrorWhileFetchingException
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException
import xyz.izadi.aldatu.utils.isNetworkAvailable
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val currencyListDao: CurrencyListDao,
    private val currencyRatesDao: CurrencyRatesDao,
    private val appContext: Context,
    private val prefManager: PreferencesManager
) : CurrencyRepository {

    override suspend fun getCurrencies(): List<Currency> {
        if (!currencyListDao.doWeHaveCurrencies()) {
            if (appContext.isNetworkAvailable()) {
                val response = currencyApi.getSupportedCountries()
                when (response.isSuccessful) {
                    true -> response.body()?.currenciesMap?.let {
                        val currencies = Currency.from(it)
                        currencyListDao.saveCurrencies(currencies)
                        return currencies
                    }
                    false -> {
                        throw  ErrorWhileFetchingException(
                            message = response.message(),
                            errorCode = response.code()
                        )
                    }
                }
            } else {
                throw NoInternetConnectionException()
            }

        }
        return currencyListDao.loadCurrencyList()
    }

    override suspend fun getExchangeRates(forceRefresh: Boolean): List<CurrencyRate> {
        if (forceRefresh || prefManager.shouldRefreshRates() || !currencyRatesDao.doWeHaveRates()) {
            if (appContext.isNetworkAvailable()) {
                val response = currencyApi.getCurrencyRates()
                when (response.isSuccessful) {
                    true -> response.body()?.let {
                        val rates = CurrencyRate.from(
                            map = it.rates,
                            timestamp = it.timestamp?.toLong() ?: System.currentTimeMillis()
                        )
                        currencyRatesDao.saveCurrencyRates(rates)
                        prefManager.saveRefreshDate()
                        return rates
                    }
                    false -> {
                        throw  ErrorWhileFetchingException(
                            message = response.message(),
                            errorCode = response.code()
                        )
                    }
                }
            } else {
                // try to get existing currencies even though they are outdated to still have functionality
                val prevRates = currencyRatesDao.loadCurrencyRates()
                if (!forceRefresh && prevRates.isNotEmpty()) {
                    return prevRates
                } else {
                    throw NoInternetConnectionException()
                }
            }
        }
        return currencyRatesDao.loadCurrencyRates()
    }
}