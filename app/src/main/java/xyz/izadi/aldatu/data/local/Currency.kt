package xyz.izadi.aldatu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import xyz.izadi.aldatu.domain.entity.CurrencyCompanionContract
import xyz.izadi.aldatu.domain.entity.CurrencyContract


@Entity(tableName = "currencies")
@Serializable
class Currency(
    @PrimaryKey
    override val currencyCode: String,
    override val currencyName: String?
) : CurrencyContract {

    companion object : CurrencyCompanionContract {
        override fun from(map: Map<String, String>?): List<Currency> {
            val currencies = mutableListOf<Currency>()
            map?.forEach { (key, value) -> currencies.add(Currency(key, value)) }
            return currencies
        }
    }
}