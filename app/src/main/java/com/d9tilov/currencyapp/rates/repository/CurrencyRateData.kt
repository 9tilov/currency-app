package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.utils.CurrencyUtils

data class CurrencyRateData(
    val base: String,
    val lastUpdateTime: Long,
    val currencyList: List<CurrencyItem>
) {

    data class CurrencyItem(val id: String, val value: Double, var isBase: Boolean = false) {
        val shortName: String = id
        val longName: String = CurrencyUtils.getCurrencyFullName(id)
        val icon: String = CurrencyUtils.getCurrencyIcon(id)
        val sign: String = CurrencyUtils.getCurrencySignBy(id)
    }
}
