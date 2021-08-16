package xyz.izadi.aldatu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
class CurrencyRate(
    @PrimaryKey
    val currencyCode: String,
    val rate: Double,
    val timestamp: Long
) {
    companion object {
        fun from(map: Map<String, Double>?, timestamp: Long): List<CurrencyRate> {
            val list = mutableListOf<CurrencyRate>()
            map?.forEach { (key, value) -> list.add(CurrencyRate(key, value, timestamp)) }
            return list
        }

        fun from(list: List<CurrencyRate>?): Map<String, Double> {
            val map = HashMap<String, Double>()
            list?.forEach {
                map[it.currencyCode] = it.rate
            }
            return map
        }

        fun convert(
            amount: Float,
            from: String,
            to: String,
            base: String,
            rates: Map<String, Double>
        ): Float? {
            val toBase = rates["$base$from"]
            val fromBase = rates["$base$to"]
            if (toBase != null && fromBase != null) {
                return ((amount / toBase) * fromBase).toFloat()
            }
            return null
        }
    }
}