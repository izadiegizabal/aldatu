package xyz.izadi.aldatu.data.repository

data class Result<out T>(
    val status: Status,
    val data: T?,
    val error: Int?,
    val message: String?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String? = null, errorCode: Int? = null): Result<T> {
            return Result(Status.ERROR, null, errorCode, message)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result(status:$status, data:$data, error:$error, message:$message)"
    }
}