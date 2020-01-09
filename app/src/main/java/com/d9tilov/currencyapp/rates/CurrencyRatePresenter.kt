package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BasePresenter
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import javax.inject.Inject

class CurrencyRatePresenter @Inject constructor(private val currencyRateInteractor: CurrencyRateInteractor) :
    BasePresenter<CurrencyRateView>() {

    fun updateCurrencyList() {
        unSubscribeOnDetach(
            currencyRateInteractor.updateCurrencyRates()
                .doOnError { view { onError() } }
                .retryWhen { it.flatMap { retryManager.observeRetries() } }
                .subscribe({}, { view { stopUpdating() } })
        )
    }

    fun getAllCurrencies() {
        unSubscribeOnDetach(
            currencyRateInteractor
                .getAllCurrencies()
                .subscribe({
                    view {
                        updateCurrency(it)
                        stopUpdating()
                    }
                }, {})
        )
    }

    fun onCurrencyClick(baseCurrency: CurrencyItem) {
        unSubscribeOnDetach(
            currencyRateInteractor.changeBaseCurrency(baseCurrency)
                .subscribe({ view { updateCurrency(it) } }, {})
        )
    }

    fun onValueChange(baseItem: CurrencyItem) {
        unSubscribeOnDetach(
            currencyRateInteractor.changeValue(baseItem)
                .subscribe({ view { updateCurrency(it) } }, {})
        )
    }

    fun onStop() {
        currencyRateInteractor.writeDataAndCancel()
    }
}
