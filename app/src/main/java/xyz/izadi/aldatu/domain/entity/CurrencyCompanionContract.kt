package xyz.izadi.aldatu.domain.entity

interface CurrencyCompanionContract {
    fun from(map: Map<String, String>?): List<CurrencyContract>
}