package xyz.izadi.aldatu.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyDao
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.utils.isNetworkAvailable
import java.util.*


class CurrencyRepository(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao,
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
            if (!currencyDao.doWeHaveCurrencies()) {
                if (appContext.isNetworkAvailable()) {
                    val response = currencyApi.getSupportedCountries()
                    when (response.isSuccessful) {
                        true -> response.body()?.currenciesMap?.let {
                            val currencies = Currency.from(it)
                            currencyDao.saveCurrencies(currencies)
                            emit(Result.success(currencies))
                            return@flow
                        }
                        false -> {
                            emit(
                                Result.error<List<Currency>>(
                                    message = response.message(),
                                    errorCode = response.code()
                                )
                            )
                            return@flow
                        }
                    }
                } else {
                    emit(noInternetError<List<Currency>>())
                    return@flow
                }

            }
            emit(Result.success(currencyDao.loadCurrencyList()))
        }
    }

    suspend fun getExchangeRates(forceRefresh: Boolean = false): Flow<Result<List<CurrencyRate>>> {
        return flow {
            emit(Result.loading())
            if (forceRefresh || prefManager.shouldRefreshRates() || !currencyDao.doWeHaveRates()) {
                if (appContext.isNetworkAvailable()) {
                    val response = currencyApi.getCurrencyRates()
                    when (response.isSuccessful) {
                        true -> response.body()?.let {
                            val rates = CurrencyRate.from(
                                map = it.rates,
                                timestamp = it.timestamp?.toLong() ?: System.currentTimeMillis()
                            )
                            currencyDao.saveCurrencyRates(rates)
                            prefManager.saveRefreshDate()
                            emit(Result.success(rates))
                            return@flow
                        }
                        false -> {
                            emit(
                                Result.error<List<CurrencyRate>>(
                                    message = response.message(),
                                    errorCode = response.code()
                                )
                            )
                            return@flow
                        }
                    }
                } else {
                    // try to get existing currencies even though they are outdated to still have functionality
                    val prevRates = currencyDao.loadCurrencyRates()
                    if (!forceRefresh && prevRates.isNotEmpty()) {
                        emit(Result.success(prevRates))
                    } else {
                        emit(noInternetError<List<CurrencyRate>>())
                    }
                    return@flow
                }
            }
            emit(Result.success(currencyDao.loadCurrencyRates()))
        }
    }
}