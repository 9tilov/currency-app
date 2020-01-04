package com.d9tilov.currencyapp.rates

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.BuildConfig
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import java.math.BigDecimal

class CurrencyRateInteractor(
    private val currencyLocalRepository: CurrencyLocalRepository,
    private val currencyRemoteRepository: CurrencyRemoteRepository,
    private val schedulerIo: Scheduler,
    private val schedulerMain: Scheduler
) {

    @WorkerThread
    fun updateCurrencyRates(baseItem: CurrencyItem?): Observable<CurrencyRateData> {
        return currencyRemoteRepository.updateCurrencyRates()
            .flatMap { t: CurrencyRateData ->
                currencyLocalRepository.updateCurrencyList(
                    t,
                    baseItem
                )
            }
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyItem>> {
        return currencyLocalRepository.getAllCurrencies()
            .subscribeOn(schedulerIo)
            .observeOn(schedulerMain)
    }

    @WorkerThread
    fun changeBaseCurrency(baseCurrency: CurrencyItem): Single<List<CurrencyItem>> {
        var localBaseCurrency: CurrencyItem = baseCurrency
        if (BuildConfig.RESET_BASE_VALUE_AFTER_CHOOSE) {
            localBaseCurrency = CurrencyItem(baseCurrency.name, BigDecimal.ONE, true)
        }
        return currencyLocalRepository.changeBaseCurrency(localBaseCurrency)
    }

    @WorkerThread
    fun changeValue(baseCurrency: CurrencyItem): Single<List<CurrencyItem>> {
        return currencyLocalRepository.changeValue(baseCurrency)
    }

    fun writeDataAndCancel() {
        currencyLocalRepository.flushData()
    }
}
