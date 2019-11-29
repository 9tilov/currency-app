package com.d9tilov.currencyapp.rates

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.base.BaseMvpFragment
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.utils.listeners.OnItemClickListener
import javax.inject.Inject

class ExchangeRatesFragment : BaseMvpFragment<CurrencyRateView, CurrencyRatePresenter>(),
    CurrencyRateView {

    @Inject
    override lateinit var presenter: CurrencyRatePresenter
    private val adapter: CurrencyRateAdapter = CurrencyRateAdapter()
    private lateinit var rvCurrencyList: RecyclerView
    private lateinit var swipeToRefreshContainer: SwipeRefreshLayout

    override val layoutRes: Int
        get() = R.layout.fragment_exchange_rates
    override val componentName: String
        get() = ExchangeComponent::class.java.name
    override val fragmentTag: String
        get() = TAG
    override val isComponentDestroyable: Boolean
        get() = true

    private val onItemClickListener = object : OnItemClickListener<CurrencyRateData.CurrencyItem> {
        override fun onItemClick(item: CurrencyRateData.CurrencyItem, position: Int) {
            presenter.onCurrencyClick(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.provideComponent(componentName) {
            ExchangeComponent.Initializer.init()
        }.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        rvCurrencyList = view.findViewById(R.id.rv_currency_rate_list)
        swipeToRefreshContainer = view.findViewById(R.id.currency_container)
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
        adapter.itemClickListener = onItemClickListener
        swipeToRefreshContainer.setProgressBackgroundColorSchemeResource(
            R.color.swipeRefreshBackground
        )
        swipeToRefreshContainer.setColorSchemeResources(
            R.color.swipeRefreshProgressOne,
            R.color.swipeRefreshProgressTwo,
            R.color.swipeRefreshProgressThree
        )
        swipeToRefreshContainer.setOnRefreshListener(swipeToRefreshListener)
        presenter.getAllCurrencies()
    }

    override fun onStart() {
        super.onStart()
        presenter.updateCurrencyList()
    }


    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun updateCurrency(currencyList: List<CurrencyRateData.CurrencyItem>) {
        adapter.updateCurrencyRate(currencyList)
        stopUpdating()
    }

    override fun stopUpdating() {
        swipeToRefreshContainer.isEnabled = true
        swipeToRefreshContainer.isRefreshing = false
    }

    private val swipeToRefreshListener = {
        presenter.updateCurrencyList()
    }

    companion object {

        const val TAG = "ExchangeRatesFragment"

        @JvmStatic
        fun newInstance() = ExchangeRatesFragment()
    }
}
