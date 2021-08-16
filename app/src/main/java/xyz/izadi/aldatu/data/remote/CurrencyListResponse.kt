package xyz.izadi.aldatu.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CurrencyListResponse : BaseResponse() {
    @SerialName("currencies")
    var currenciesMap: Map<String, String>? = null
}