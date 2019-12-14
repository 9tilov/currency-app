package com.d9tilov.currencyapp.network.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

typealias CurrencyRateMap = MutableMap<String, BigDecimal>

data class CurrencyResponse(
    @SerializedName("base") @Expose val base: String,
    @SerializedName("date") @Expose val date: String,
    @SerializedName("rates") @Expose val rates: CurrencyRateMap
) {
    companion object {
        fun createWithTimestamp(
            base: String, date: String,
            rates: CurrencyRateMap, timestamp: Long
        ): CurrencyResponse {
            val response = CurrencyResponse(base, date, rates)
            response.timestamp = timestamp
            return response
        }
    }

    var timestamp: Long = 0L
}
