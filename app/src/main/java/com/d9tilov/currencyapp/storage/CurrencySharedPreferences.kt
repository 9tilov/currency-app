package com.d9tilov.currencyapp.storage

import android.content.SharedPreferences
import com.d9tilov.currencyapp.rates.repository.CurrencyItem

class CurrencySharedPreferences(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val PREF_KEY_BASE_CURRENCY = "base_currency"
        private const val PREF_KEY_BASE_CURRENCY_VALUE = "base_currency_value"
        private const val PREF_KEY_LAST_UPDATE_TIME = "last_update_time"
    }

    fun saveBaseCurrency(baseCurrency: CurrencyItem) {
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_BASE_CURRENCY, baseCurrency.name)
        editor.putString(PREF_KEY_BASE_CURRENCY_VALUE, baseCurrency.value.toString())
        editor.apply()
    }

    fun loadBaseCurrency(): CurrencyItem {
        val name = sharedPreferences.getString(PREF_KEY_BASE_CURRENCY, "EUR")
        val value = sharedPreferences.getString(PREF_KEY_BASE_CURRENCY_VALUE, "1")
        return CurrencyItem(name!!, value!!.toBigDecimal(), true)
    }

    fun saveLastUpdateTime(time: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(PREF_KEY_LAST_UPDATE_TIME, time)
        editor.apply()
    }


    fun loadLastUpdateTime(): Long {
        return sharedPreferences.getLong(PREF_KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
    }
}
