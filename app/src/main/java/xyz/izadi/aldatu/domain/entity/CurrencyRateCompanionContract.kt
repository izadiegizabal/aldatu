package xyz.izadi.aldatu.domain.entity

interface CurrencyRateCompanionContract {
    fun from(map: Map<String, Double>?, timestamp: Long): List<CurrencyRateContract>
    fun from(list: List<CurrencyRateContract>?): Map<String, Double>
    fun convert(
        amount: Float,
        from: String,
        to: String,
        base: String,
        rates: Map<String, Double>
    ): Float?
}