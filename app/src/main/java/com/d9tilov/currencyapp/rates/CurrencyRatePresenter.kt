package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BasePresenter
import javax.inject.Inject

class CurrencyRatePresenter @Inject constructor(private val currencyRateInteractor: CurrencyRateInteractor) :
    BasePresenter<CurrencyRateView>() {

    fun updateCurrencyList() {
        currencyRateInteractor.updateCurrencyRates()
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
}
