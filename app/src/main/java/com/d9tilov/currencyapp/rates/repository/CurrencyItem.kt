package com.d9tilov.currencyapp.rates.repository

import java.math.BigDecimal

data class CurrencyItem(val name: String, val value: BigDecimal, var isBase: Boolean = false)
