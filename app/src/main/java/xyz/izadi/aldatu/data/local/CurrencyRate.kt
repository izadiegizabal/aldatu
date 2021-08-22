package xyz.izadi.aldatu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.izadi.aldatu.domain.entity.CurrencyRateCompanionContract
import xyz.izadi.aldatu.domain.entity.CurrencyRateContract

@Entity(tableName = "currency_rates")
class CurrencyRate(
    @PrimaryKey
    override val currencyCode: String,
    override val rate: Double,
    override val timestamp: Long
) : CurrencyRateContract {

    companion object : CurrencyRateCompanionContract {
        override fun from(map: Map<String, Double>?, timestamp: Long): List<CurrencyRate> {
            val list = mutableListOf<CurrencyRate>()
            map?.forEach { (key, value) -> list.add(CurrencyRate(key, value, timestamp)) }
            return list
        }

        override fun from(list: List<CurrencyRateContract>?): Map<String, Double> {
            val map = HashMap<String, Double>()
            list?.forEach {
                map[it.currencyCode] = it.rate
            }
            return map
        }

        override fun convert(
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