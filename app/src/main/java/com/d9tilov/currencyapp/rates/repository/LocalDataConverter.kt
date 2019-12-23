package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.network.data.CurrencyResponse
import com.d9tilov.currencyapp.storage.model.CurrencyDto

object LocalDataConverter {

    fun convertFrom(input: CurrencyResponse): List<CurrencyDto> {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseItem = CurrencyDto(input.base, "1")
        currencyRateList.add(baseItem)
        for ((key, value) in input.rates) {
            val item = CurrencyDto(key, value.toString())
            currencyRateList.add(item)
        }
        return currencyRateList
    }
}
