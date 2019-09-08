package com.d9tilov.currencyapp.network

import com.d9tilov.currencyapp.network.data.CurrencyResponse
import io.reactivex.Single
import javax.inject.Inject

class CurrencyRemoteRepository @Inject constructor(private val revolutApi: RevolutApi) {

    fun updateCurrencyRates(baseCurrency: String): Single<CurrencyResponse> {
        return revolutApi.getCurrencies(baseCurrency)
    }
}
