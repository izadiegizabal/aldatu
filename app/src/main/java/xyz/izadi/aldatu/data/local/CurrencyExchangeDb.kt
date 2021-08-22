package xyz.izadi.aldatu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Currency::class, CurrencyRate::class], version = 1)
abstract class CurrencyExchangeDb : RoomDatabase() {
    abstract fun currencyListDao(): CurrencyListDao
    abstract fun currencyRatesDao(): CurrencyRatesDao
}