package com.d9tilov.currencyapp.rates

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.base.BaseMvpFragment
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_exchange_rates.rv_currency_rate_list as rvCurrencyList


class ExchangeRatesFragment : BaseMvpFragment<CurrencyRateView, CurrencyRatePresenter>(),
    CurrencyRateView {

    @Inject
    override lateinit var presenter: CurrencyRatePresenter
    private val adapter: CurrencyRateAdapter = CurrencyRateAdapter()

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
        rvCurrencyList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        rvCurrencyList.layoutManager = layoutManager
        rvCurrencyList.addItemDecoration(
            DividerItemDecoration(
                activity,
                layoutManager.orientation
            )
        )

        rvCurrencyList.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        presenter.updateCurrencyList()
    }

    override fun updateCurrency(currencyList: List<CurrencyRateData.CurrencyItem>) {
        adapter.updateCurrencyRate(currencyList)
    }

    companion object {

        const val TAG = "ExchangeRatesFragment"

        @JvmStatic
        fun newInstance() = ExchangeRatesFragment()
    }
}
