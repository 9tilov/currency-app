package com.d9tilov.currencyapp.rates

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import io.reactivex.Flowable
import io.reactivex.Scheduler

class CurrencyRateInteractor(
    private val currencyLocalRepository: CurrencyLocalRepository,
    private val currencyRemoteRepository: CurrencyRemoteRepository,
    private val schedulerIo: Scheduler,
    private val schedulerMain: Scheduler
) {

    @WorkerThread
    fun updateCurrencyRates() {
        currencyRemoteRepository.updateCurrencyRates()
            .map { t: CurrencyRateData -> currencyLocalRepository.updateCurrencyList(t) }
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
            .subscribe()
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyRateData.CurrencyItem>> {
        return currencyLocalRepository.getAllCurrencies()
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }
}
