package com.d9tilov.currencyapp.rates.repository

data class CurrencyRateData(
    val lastUpdateTime: Long,
    val currencyList: Set<CurrencyItem>
)
