package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.network.data.CurrencyResponse
import java.text.SimpleDateFormat
import java.util.*

object RemoteDataConverter {

    fun convertFrom(input: CurrencyResponse): CurrencyRateData {
        val date =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(input.date)?.time ?: 0
        val currencyRateList = mutableSetOf<CurrencyItem>()
        for ((key, value) in input.rates) {
            val item = CurrencyItem(key, value)
            currencyRateList.add(item)
        }
        return CurrencyRateData(date, currencyRateList)
    }
}
