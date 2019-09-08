package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.db.CurrencyLocalRepository
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.network.data.CurrencyResponse
import io.reactivex.Single
import javax.inject.Inject

class CurrencyRateInteractor @Inject constructor(
    private val currencyLocalRepository: CurrencyLocalRepository,
    private val currencyRemoteRepository: CurrencyRemoteRepository
) {

    fun updateCurrencyRates(baseCurrenncy: String): Single<CurrencyResponse> {
        return currencyRemoteRepository.updateCurrencyRates(baseCurrenncy)
    }

}