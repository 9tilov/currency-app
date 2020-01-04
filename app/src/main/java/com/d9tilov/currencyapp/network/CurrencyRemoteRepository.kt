package com.d9tilov.currencyapp.network

import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.rates.repository.RemoteDataConverter
import com.d9tilov.currencyapp.storage.CurrencySharedPreferences
import io.reactivex.Observable

class CurrencyRemoteRepository(
    private val revolutApi: RevolutApi,
    private val sharedPreferences: CurrencySharedPreferences
) {

    fun updateCurrencyRates(): Observable<CurrencyRateData> {
        return revolutApi.getCurrencies(sharedPreferences.loadBaseCurrency().name)
            .map { RemoteDataConverter.convertFrom(it) }
    }
}
