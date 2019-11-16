package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.base.Mapper
import com.d9tilov.currencyapp.network.data.CurrencyResponse
import java.text.SimpleDateFormat
import java.util.*

class CurrencyResponseMapperImpl : Mapper<CurrencyResponse, CurrencyRateData> {

    override fun convertFrom(input: CurrencyResponse): CurrencyRateData {
        val date =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(input.date)?.time ?: 0
        val currencyRateList = mutableListOf<CurrencyRateData.CurrencyItem>()
        for ((key, value) in input.rates) {
            val item = CurrencyRateData.CurrencyItem(key, value)
            currencyRateList.add(item)
        }
        return CurrencyRateData(input.base, date, currencyRateList)
    }
}
