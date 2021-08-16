package xyz.izadi.aldatu.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {

    @GET("/list")
    suspend fun getSupportedCountries(): Response<CurrencyListResponse>

    @GET("/live")
    suspend fun getCurrencyRates(): Response<CurrencyRatesResponse>

}