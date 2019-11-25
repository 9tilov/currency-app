package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.network.data.CurrencyResponse
import java.text.SimpleDateFormat
import java.util.*

object RemoteDataConverter {

    fun convertFrom(input: CurrencyResponse): CurrencyRateData {
        val date =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(input.date)?.time ?: 0
        val currencyRateList = mutableListOf<CurrencyRateData.CurrencyItem>()
        val baseItem = CurrencyRateData.CurrencyItem(input.base, 1.0, true)
        currencyRateList.add(baseItem)
        for ((key, value) in input.rates) {
            val item = CurrencyRateData.CurrencyItem(key, value)
            currencyRateList.add(item)
        }
        return CurrencyRateData(input.base, date, currencyRateList)
    }
}
