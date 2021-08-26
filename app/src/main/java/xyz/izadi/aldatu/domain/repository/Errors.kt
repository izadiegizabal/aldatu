package xyz.izadi.aldatu.domain.repository

class NoInternetConnectionException(override val message: String? = null) : Exception()
class ErrorWhileFetchingException(
    override val message: String? = null,
    val errorCode: Int? = null
) : Exception()