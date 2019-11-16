package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class CurrencyRatePresenter @Inject constructor(private val currencyRateInteractor: CurrencyRateInteractor) :
    BasePresenter<CurrencyRateView>() {

    fun updateCurrencyList() {
        Timber.d("update")
        unSubscribeOnDetach(
            currencyRateInteractor.updateCurrencyRates("RUB")
                .subscribe({
                    view { updateCurrency(it.currencyList) }
                }, {})
        )

    }
}
