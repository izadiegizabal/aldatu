package xyz.izadi.aldatu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyRatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrencyRates(rates: List<CurrencyRate>)

    @Query("SELECT * FROM currency_rates")
    fun loadCurrencyRates(): List<CurrencyRate>

    @Query("SELECT EXISTS(SELECT * FROM currency_rates)")
    fun doWeHaveRates(): Boolean
}