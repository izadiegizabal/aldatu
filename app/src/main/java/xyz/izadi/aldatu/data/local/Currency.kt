package xyz.izadi.aldatu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "currencies")
@Serializable
class Currency(
    @PrimaryKey
    val currencyCode: String,
    val currencyName: String?
) {
    companion object {
        fun from(map: Map<String, String>?): List<Currency> {
            val currencies = mutableListOf<Currency>()
            map?.forEach { (key, value) -> currencies.add(Currency(key, value)) }
            return currencies
        }
    }
}