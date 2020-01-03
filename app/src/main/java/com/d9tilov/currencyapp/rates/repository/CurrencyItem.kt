package com.d9tilov.currencyapp.rates.repository

import java.math.BigDecimal

data class CurrencyItem(val name: String, val value: BigDecimal, var isBase: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrencyItem) return false

        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + isBase.hashCode()
        return result
    }

}
