package xyz.izadi.aldatu.data.remote

import okio.IOException
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {

    @GET("/list")
    @Throws(IOException::class) // retrofit exceptions
    suspend fun getSupportedCountries(): Response<CurrencyListResponse>

    @GET("/live")
    @Throws(IOException::class) // retrofit exceptions
    suspend fun getCurrencyRates(): Response<CurrencyRatesResponse>

}