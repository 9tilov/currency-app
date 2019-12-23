package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BasePresenter
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import javax.inject.Inject

class CurrencyRatePresenter @Inject constructor(private val currencyRateInteractor: CurrencyRateInteractor) :
    BasePresenter<CurrencyRateView>() {

    fun updateCurrencyList(baseCurrency: CurrencyItem?) {
        unSubscribeOnDetach(
            currencyRateInteractor.updateCurrencyRates(baseCurrency)
                .subscribe({}, { view { stopUpdating() } })
        )
    }

    fun getAllCurrencies() {
        unSubscribeOnDetach(
            currencyRateInteractor
                .getAllCurrencies()
                .subscribe({
                    view { updateCurrency(it) }
                }, {})
        )
    }

    fun onCurrencyClick(baseCurrency: CurrencyItem) {
        unSubscribeOnDetach(
            currencyRateInteractor.changeBaseCurrency(baseCurrency)
                .subscribe({}, {})
        )
    }

    fun onValueChange(baseItem: CurrencyItem) {
        unSubscribeOnDetach(
            currencyRateInteractor.changeValue(baseItem)
                .subscribe({}, {})
        )
    }

    fun onStop() {
        currencyRateInteractor.writeDataAndCancel()
    }
}
