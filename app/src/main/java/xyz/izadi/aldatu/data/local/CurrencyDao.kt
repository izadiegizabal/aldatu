package xyz.izadi.aldatu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CurrencyDao {
    @Insert(onConflict = REPLACE)
    fun saveCurrencies(currencies: List<Currency>)

    @Insert(onConflict = REPLACE)
    fun saveCurrencyRates(rates: List<CurrencyRate>)

    @Query("SELECT * FROM currencies")
    fun loadCurrencyList(): List<Currency>

    @Query("SELECT EXISTS(SELECT * FROM currencies)")
    fun doWeHaveCurrencies(): Boolean

    @Query("SELECT * FROM currency_rates")
    fun loadCurrencyRates(): List<CurrencyRate>

    @Query("SELECT EXISTS(SELECT * FROM currency_rates)")
    fun doWeHaveRates(): Boolean
}