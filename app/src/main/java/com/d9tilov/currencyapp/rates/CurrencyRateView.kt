package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BaseView
import com.d9tilov.currencyapp.rates.repository.CurrencyItem

interface CurrencyRateView : BaseView {

    fun updateCurrency(currencyList: List<CurrencyItem>)

    fun stopUpdating()
}