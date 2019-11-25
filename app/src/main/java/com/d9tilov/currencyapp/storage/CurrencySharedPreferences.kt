package com.d9tilov.currencyapp.storage

import android.content.SharedPreferences

class CurrencySharedPreferences(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val PREF_KEY_BASE_CURRENCY = "base_currency"
        private const val PREF_KEY_LAST_UPDATE_TIME = "last_update_time"
    }

    fun saveBaseCurrency(baseCurrency: String) {
        val editor = sharedPreferences.edit()
        editor.putString(PREF_KEY_BASE_CURRENCY, baseCurrency)
        editor.apply()
    }

    fun loadBaseCurrency(): String {
        return sharedPreferences.getString(PREF_KEY_BASE_CURRENCY, "EUR")!!
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
