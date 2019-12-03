package com.d9tilov.currencyapp.network

import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.rates.repository.RemoteDataConverter
import com.d9tilov.currencyapp.storage.CurrencySharedPreferences
import io.reactivex.Single

class CurrencyRemoteRepository(
    private val revolutApi: RevolutApi,
    private val sharedPreferences: CurrencySharedPreferences
) {

    fun updateCurrencyRates(): Single<CurrencyRateData> {
        return revolutApi.getCurrencies(sharedPreferences.loadBaseCurrency().shortName)
            .map { RemoteDataConverter.convertFrom(it) }
    }
}
