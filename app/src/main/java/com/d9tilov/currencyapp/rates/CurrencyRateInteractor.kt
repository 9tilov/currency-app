package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.db.CurrencyLocalRepository
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import io.reactivex.Scheduler
import io.reactivex.Single

class CurrencyRateInteractor(
    private val currencyLocalRepository: CurrencyLocalRepository,
    private val currencyRemoteRepository: CurrencyRemoteRepository,
    private val schedulerIo: Scheduler,
    private val schedulerMain: Scheduler
) {

    fun updateCurrencyRates(baseCurrency: String): Single<CurrencyRateData> {
        return currencyRemoteRepository.updateCurrencyRates(baseCurrency)
            .map { currencyRemoteRepository.fromCurrencyResponse(it) }
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }
}