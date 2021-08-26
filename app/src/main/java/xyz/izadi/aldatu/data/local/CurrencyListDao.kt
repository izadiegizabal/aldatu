package xyz.izadi.aldatu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CurrencyListDao {
    @Insert(onConflict = REPLACE)
    fun saveCurrencies(currencies: List<Currency>)

    @Query("SELECT * FROM currencies")
    fun loadCurrencyList(): List<Currency>

    @Query("SELECT EXISTS(SELECT * FROM currencies)")
    fun doWeHaveCurrencies(): Boolean
}