package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.BaseView
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData

interface CurrencyRateView : BaseView {

    fun updateCurrency(currencyList: List<CurrencyRateData.CurrencyItem>)

    fun stopUpdating()
}