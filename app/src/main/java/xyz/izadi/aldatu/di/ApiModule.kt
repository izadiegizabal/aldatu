package xyz.izadi.aldatu.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import retrofit2.Converter
import retrofit2.Retrofit
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.domain.repository.NoInternetConnectionException
import xyz.izadi.aldatu.utils.Constants
import xyz.izadi.aldatu.utils.isNetworkAvailable
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    companion object {
        @Qualifier
        @Retention(AnnotationRetention.RUNTIME)
        annotation class ApiKeyInterceptor

        @Qualifier
        @Retention(AnnotationRetention.RUNTIME)
        annotation class NetworkConnectionInterceptor
    }

    @Singleton
    @Provides
    @ApiKeyInterceptor
    fun providesApiKeyInterceptor() = Interceptor {
        val url = it.request()
            .url
            .newBuilder()
            .addQueryParameter("access_key", Constants.API_CURRENCYLAYER_KEY)
            .build()
        val request = it.request()
            .newBuilder()
            .url(url)
            .build()
        it.proceed(request)
    }

    @Singleton
    @Provides
    @NetworkConnectionInterceptor
    fun providesNetworkConnectionInterceptor(@ApplicationContext appContext: Context) =
        Interceptor {
            if (!appContext.isNetworkAvailable()) {
                throw IOException(NoInternetConnectionException())
            }

            val builder: Request.Builder = it.request().newBuilder()
            it.proceed(builder.build())
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        @ApiKeyInterceptor apiKeyInterceptor: Interceptor,
        @NetworkConnectionInterceptor networkInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(networkInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .build()

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory {
        val json = Json { ignoreUnknownKeys = true }
        return json.asConverterFactory("application/json".toMediaType())

    }


    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(converterFactory)
            .baseUrl(Constants.API_CURRENCYLAYER_BASEURL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): CurrencyApi =
        retrofit.create(CurrencyApi::class.java)
}