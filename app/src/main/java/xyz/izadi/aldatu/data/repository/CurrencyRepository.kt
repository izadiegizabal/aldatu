package xyz.izadi.aldatu.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyListDao
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.CurrencyRatesDao
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.utils.isNetworkAvailable

class CurrencyRepository(
    private val currencyApi: CurrencyApi,
    private val currencyListDao: CurrencyListDao,
    private val currencyRatesDao: CurrencyRatesDao,
    private val appContext: Context,
    private val prefManager: PreferencesManager
) {
    companion object {
        fun <T> noInternetError() = Result.error<T>(
            message = "Internet is not available",
            errorCode = 503
        )
    }

    suspend fun getCurrencies(): Flow<Result<List<Currency>>> {
        return flow {
            emit(Result.loading())
            if (!currencyListDao.doWeHaveCurrencies()) {
                if (appContext.isNetworkAvailable()) {
                    val response = currencyApi.getSupportedCountries()
                    when (response.isSuccessful) {
                        true -> response.body()?.currenciesMap?.let {
                            val currencies = Currency.from(it)
                            currencyListDao.saveCurrencies(currencies)
                            emit(Result.success(currencies))
                            return@flow
                        }
                        false -> {
                            emit(
                                Result.error(
                                    message = response.message(),
                                    errorCode = response.code()
                                )
                            )
                            return@flow
                        }
                    }
                } else {
                    emit(noInternetError())
                    return@flow
                }

            }
            emit(Result.success(currencyListDao.loadCurrencyList()))
        }
    }

    suspend fun getExchangeRates(forceRefresh: Boolean = false): Flow<Result<List<CurrencyRate>>> {
        return flow {
            emit(Result.loading())
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
                            emit(Result.success(rates))
                            return@flow
                        }
                        false -> {
                            emit(
                                Result.error(
                                    message = response.message(),
                                    errorCode = response.code()
                                )
                            )
                            return@flow
                        }
                    }
                } else {
                    // try to get existing currencies even though they are outdated to still have functionality
                    val prevRates = currencyRatesDao.loadCurrencyRates()
                    if (!forceRefresh && prevRates.isNotEmpty()) {
                        emit(Result.success(prevRates))
                    } else {
                        emit(noInternetError<List<CurrencyRate>>())
                    }
                    return@flow
                }
            }
            emit(Result.success(currencyRatesDao.loadCurrencyRates()))
        }
    }
}