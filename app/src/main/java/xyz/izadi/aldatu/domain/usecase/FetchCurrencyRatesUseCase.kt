package xyz.izadi.aldatu.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.izadi.aldatu.data.local.CurrencyRate
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.repository.ErrorWhileFetchingException
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException

class FetchCurrencyRatesUseCase(private val repository: CurrencyRepository) :
    UseCase<Boolean, Flow<Result<List<CurrencyRate>>>> {

    /**
     * @param param: boolean to force re-fetching the currency rates from the server
     */
    override suspend fun invoke(param: Boolean?): Flow<Result<List<CurrencyRate>>> {
        return flow {
            emit(Result.loading())
            runCatching {
                repository.getExchangeRates(param ?: false)
            }.getOrElse {
                when (it) {
                    is ErrorWhileFetchingException -> emit(
                        Result.error<List<CurrencyRate>>(
                            it.message,
                            it.errorCode
                        )
                    )
                    is NoInternetConnectionException -> emit(Result.error<List<CurrencyRate>>(it.javaClass.simpleName))
                    else -> emit(Result.error<List<CurrencyRate>>(it.message))
                }
                null
            }?.let {
                emit(Result.success(it))
            }
        }
    }

}