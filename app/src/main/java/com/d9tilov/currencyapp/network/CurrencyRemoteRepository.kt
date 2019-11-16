package com.d9tilov.currencyapp.network

import com.d9tilov.currencyapp.base.Mapper
import com.d9tilov.currencyapp.network.data.CurrencyResponse
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import io.reactivex.Single

class CurrencyRemoteRepository(
    private val revolutApi: RevolutApi,
    private val mapper: Mapper<CurrencyResponse, CurrencyRateData>
) {

    fun updateCurrencyRates(baseCurrency: String): Single<CurrencyResponse> {
        return revolutApi.getCurrencies(baseCurrency)
    }

    fun fromCurrencyResponse(currencyResponse: CurrencyResponse): CurrencyRateData =
        mapper.convertFrom(currencyResponse)

}
