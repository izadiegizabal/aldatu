package xyz.izadi.aldatu.utils

import xyz.izadi.aldatu.BuildConfig
import xyz.izadi.aldatu.data.local.Currency

object Constants {
    const val API_CURRENCYLAYER_KEY = BuildConfig.API_CURRENCYLAYER_KEY
    const val API_CURRENCYLAYER_BASEURL = "http://api.currencylayer.com/"
    const val API_CURRENCYLAYER_REFRESH_INTERVAL = 30 * 60 * 1000L
    const val API_CURRENCYLAYER_BASECURRENCY = "USD"
    val DEFAULT_CURRENCY = Currency("JPY", "Japanese Yen")
    const val DEFAULT_AMOUNT = 100f
}