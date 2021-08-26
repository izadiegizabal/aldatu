package xyz.izadi.aldatu.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.izadi.aldatu.data.local.CurrencyListDao
import xyz.izadi.aldatu.data.local.CurrencyRatesDao
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.data.repository.CurrencyRepositoryImpl
import xyz.izadi.aldatu.domain.repository.CurrencyRepository
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyListUseCase
import xyz.izadi.aldatu.domain.usecase.FetchCurrencyRatesUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesCurrencyRepository(
        api: CurrencyApi,
        listDao: CurrencyListDao,
        ratesDao: CurrencyRatesDao,
        prefManager: PreferencesManager
    ): CurrencyRepository = CurrencyRepositoryImpl(api, listDao, ratesDao, prefManager)

    @Singleton
    @Provides
    fun providesFetchCurrencyListUseCase(repository: CurrencyRepository): FetchCurrencyListUseCase =
        FetchCurrencyListUseCase(repository)

    @Singleton
    @Provides
    fun providesFetchCurrencyRatesUseCase(repository: CurrencyRepository): FetchCurrencyRatesUseCase =
        FetchCurrencyRatesUseCase(repository)
}