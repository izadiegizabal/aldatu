package xyz.izadi.aldatu.domain.usecase

interface UseCase<T, K> {
    suspend fun invoke(param: T? = null): K
}