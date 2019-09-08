package com.d9tilov.currencyapp.rates

import android.os.Bundle
import android.view.View
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.base.BaseMvpFragment
import com.d9tilov.currencyapp.di.ComponentHolder
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_exchange_rates.cur_view as curView

class ExchangeRatesFragment : BaseMvpFragment<CurrencyRateView, CurrencyRatePresenter>(),
    CurrencyRateView {

    @Inject
    override lateinit var presenter: CurrencyRatePresenter

    override val layoutRes: Int
        get() = R.layout.fragment_exchange_rates
    override val componentName: String
        get() = ExchangeComponent::class.java.name
    override val fragmentTag: String
        get() = TAG
    override val isComponentDestroyable: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ComponentHolder.provideComponent(componentName) {
            ExchangeComponent.Initializer.init()
        }.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        curView.setSign("RUB")
        curView.setValue(11)
        curView.setLongName("rubles")
    }

    companion object {

        const val TAG = "ExchangeRatesFragment"

        @JvmStatic
        fun newInstance() = ExchangeRatesFragment()
    }
}