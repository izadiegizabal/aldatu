package xyz.izadi.aldatu.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CurrencyRatesResponse : BaseResponse() {
    var timestamp: Int? = null

    @SerialName("quotes")
    var rates: Map<String, Double>? = null
}