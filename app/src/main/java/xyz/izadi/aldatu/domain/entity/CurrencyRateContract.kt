package xyz.izadi.aldatu.domain.entity

interface CurrencyRateContract {
    val currencyCode: String
    val rate: Double
    val timestamp: Long
}