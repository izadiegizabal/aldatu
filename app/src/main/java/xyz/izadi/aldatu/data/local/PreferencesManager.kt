package xyz.izadi.aldatu.data.local

import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xyz.izadi.aldatu.utils.Constants
import java.util.*

class PreferencesManager(private val preferences: SharedPreferences) {
    companion object {
        const val PREFERENCES_REFRESH_KEY = "refreshed_time"
        const val PREFERENCES_DEFAULT_CURRENCY_KEY = "default_currency"
        const val PREFERENCES_DEFAULT_AMOUNT_KEY = "default_amount"
    }

    fun shouldRefreshRates(): Boolean {
        val lastUpdate = preferences.getLong(PREFERENCES_REFRESH_KEY, Long.MAX_VALUE)
        val delta = System.currentTimeMillis() - lastUpdate
        return delta < 0 || delta > Constants.API_CURRENCYLAYER_REFRESH_INTERVAL
    }

    fun saveRefreshDate() {
        preferences
            .edit()
            .putLong(PREFERENCES_REFRESH_KEY, System.currentTimeMillis())
            .apply()
    }

    fun getRefreshDate(): Date? {
        return preferences.getLong(PREFERENCES_REFRESH_KEY, -1).takeIf { it > 0 }?.let {
            Date(it)
        }
    }

    fun setDefaultCurrency(newDef: Currency) {
        preferences
            .edit()
            .putString(PREFERENCES_DEFAULT_CURRENCY_KEY, Json.encodeToString(newDef))
            .apply()
    }

    fun getDefaultCurrency(): Currency {
        return preferences
            .getString(
                PREFERENCES_DEFAULT_CURRENCY_KEY,
                Json.encodeToString(Constants.DEFAULT_CURRENCY)
            )?.let {
                Json.decodeFromString<Currency>(it)
            } ?: Constants.DEFAULT_CURRENCY
    }

    fun setDefaultAmount(newAmount: Float) {
        preferences
            .edit()
            .putFloat(PREFERENCES_DEFAULT_AMOUNT_KEY, newAmount)
            .apply()
    }

    fun getDefaultAmount(): Float {
        return preferences.getFloat(PREFERENCES_DEFAULT_AMOUNT_KEY, Constants.DEFAULT_AMOUNT)
    }
}