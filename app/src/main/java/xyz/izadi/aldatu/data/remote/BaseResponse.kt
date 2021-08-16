package xyz.izadi.aldatu.data.remote

open class BaseResponse {

    var success: Boolean = false
    var error: Error? = null

    class Error {
        val code: Int = 0
        val info: String? = null
    }
    
}