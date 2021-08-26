package xyz.izadi.aldatu.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.izadi.aldatu.data.local.Currency
import xyz.izadi.aldatu.data.repository.Result
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.repository.ErrorWhileFetchingException
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException

class FetchCurrencyListUseCase(private val repository: CurrencyRepository) :
    UseCase<Unit, Flow<Result<List<Currency>>>> {

    override suspend fun invoke(param: Unit?): Flow<Result<List<Currency>>> {
        return flow {
            emit(Result.loading())
            runCatching {
                repository.getCurrencies()
            }.getOrElse {
                when (it) {
                    is ErrorWhileFetchingException -> emit(
                        Result.error<List<Currency>>(
                            it.message,
                            it.errorCode
                        )
                    )
                    is NoInternetConnectionException -> emit(Result.error<List<Currency>>(it.javaClass.simpleName))
                    else -> emit(Result.error<List<Currency>>(it.message))
                }
                null
            }?.let {
                emit(Result.success(it))
            }
        }
    }

}