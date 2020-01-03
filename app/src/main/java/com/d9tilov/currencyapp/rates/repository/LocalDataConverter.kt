package com.d9tilov.currencyapp.rates.repository

import com.d9tilov.currencyapp.network.data.CurrencyResponse
import com.d9tilov.currencyapp.storage.model.CurrencyDto

object LocalDataConverter {

    fun convertFrom(input: CurrencyResponse): Set<CurrencyDto> {
        val currencyRateList = mutableSetOf<CurrencyDto>()
        for ((key, value) in input.rates) {
            val item = CurrencyDto(key, value.toString())
            currencyRateList.add(item)
        }
        return currencyRateList
    }
}
