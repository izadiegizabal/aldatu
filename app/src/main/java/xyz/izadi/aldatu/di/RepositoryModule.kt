package xyz.izadi.aldatu.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.izadi.aldatu.data.local.CurrencyDao
import xyz.izadi.aldatu.data.local.PreferencesManager
import xyz.izadi.aldatu.data.remote.CurrencyApi
import xyz.izadi.aldatu.data.repository.CurrencyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesRepository(
        api: CurrencyApi,
        dao: CurrencyDao,
        @ApplicationContext appContext: Context,
        prefManager: PreferencesManager
    ) = CurrencyRepository(api, dao, appContext, prefManager)
}