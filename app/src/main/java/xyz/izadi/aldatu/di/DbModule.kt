package xyz.izadi.aldatu.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.izadi.aldatu.data.local.CurrencyExchangeDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Singleton
    @Provides
    fun providesDb(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        CurrencyExchangeDb::class.java,
        "currency-db"
    ).build()

    @Singleton
    @Provides
    fun providesCurrencyListDao(db: CurrencyExchangeDb) = db.currencyListDao()

    @Singleton
    @Provides
    fun providesCurrencyRatesDao(db: CurrencyExchangeDb) = db.currencyRatesDao()
}