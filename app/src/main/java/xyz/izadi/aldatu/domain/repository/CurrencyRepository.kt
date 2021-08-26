package xyz.izadi.aldatu.domain.repository

import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.local.CurrencyRate

interface CurrencyRepository {
    @Throws(NoInternetConnectionException::class, ErrorWhileFetchingException::class)
    suspend fun getExchangeRates(forceRefresh: Boolean = false): List<CurrencyRate>

    @Throws(NoInternetConnectionException::class, ErrorWhileFetchingException::class)
    suspend fun getCurrencies(): List<Currency>
}

class NoInternetConnectionException(override val message: String? = null) : Exception()
class ErrorWhileFetchingException(
    override val message: String? = null,
    val errorCode: Int? = null
) : Exception()