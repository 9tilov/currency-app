package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BasePresenter
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import javax.inject.Inject

class CurrencyRatePresenter @Inject constructor(private val currencyRateInteractor: CurrencyRateInteractor) :
    BasePresenter<CurrencyRateView>() {

    fun updateCurrencyList() {
        unSubscribeOnDetach(
            currencyRateInteractor.updateCurrencyRates()
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

    fun onCurrencyClick(baseCurrency: CurrencyRateData.CurrencyItem) {
        unSubscribeOnDetach(
            currencyRateInteractor.changeBaseCurrency(baseCurrency)
                .subscribe({}, {})
        )
    }

    fun onStop() {
        currencyRateInteractor.writeDataAndCancel()
    }
}
