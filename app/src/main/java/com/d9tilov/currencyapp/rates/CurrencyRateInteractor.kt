package com.d9tilov.currencyapp.rates

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single

class CurrencyRateInteractor(
    private val currencyLocalRepository: CurrencyLocalRepository,
    private val currencyRemoteRepository: CurrencyRemoteRepository,
    private val schedulerIo: Scheduler,
    private val schedulerMain: Scheduler
) {

    @WorkerThread
    fun updateCurrencyRates(): Single<Unit> {
        return currencyRemoteRepository.updateCurrencyRates()
            .map { t: CurrencyRateData -> currencyLocalRepository.updateCurrencyList(t) }
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyRateData.CurrencyItem>> {
        return currencyLocalRepository.getAllCurrencies()
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }

    @WorkerThread
    fun changeBaseCurrency(baseCurrency: CurrencyRateData.CurrencyItem): Single<Unit> {
        currencyLocalRepository.updateBaseCurrency(baseCurrency)
        return updateCurrencyRates()
    }

    fun writeDataAndCancel() {
        currencyLocalRepository.cancelWorking()
    }
}
