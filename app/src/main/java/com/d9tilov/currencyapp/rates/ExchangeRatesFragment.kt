package com.d9tilov.currencyapp.rates

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.base.BaseMvpFragment
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.rates.recycler.CurrencyRateAdapter
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.utils.listeners.OnItemClickListener
import com.d9tilov.currencyapp.utils.listeners.OnValueChangeListener
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeRatesFragment : BaseMvpFragment<CurrencyRateView, CurrencyRatePresenter>(),
    CurrencyRateView {

    @Inject
    override lateinit var presenter: CurrencyRatePresenter
    private val adapter: CurrencyRateAdapter =
        CurrencyRateAdapter()
    private lateinit var rvCurrencyList: RecyclerView
    private lateinit var swipeToRefreshContainer: SwipeRefreshLayout

    override val layoutRes: Int
        get() = R.layout.fragment_exchange_rates
    override val componentName: String
        get() = ExchangeComponent::class.java.name
    override val isComponentDestroyable: Boolean
        get() = true

    private val onItemClickListener = object : OnItemClickListener<CurrencyItem> {
        override fun onItemClick(item: CurrencyItem, position: Int) {
            presenter.onCurrencyClick(item)
            Handler().postDelayed({ rvCurrencyList.smoothScrollToPosition(0) }, 500)
        }
    }

    private val onValueChangeListener =
        object : OnValueChangeListener<CurrencyItem, BigDecimal> {
            override fun onValueChanged(
                item: CurrencyItem,
                value: BigDecimal,
                position: Int
            ) {
                presenter.onValueChange(CurrencyItem(item.name, value, true))
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
        rvCurrencyList.itemAnimator = DefaultItemAnimator()
        rvCurrencyList.adapter = adapter
        adapter.itemClickListener = onItemClickListener
        adapter.valueChangeLister = onValueChangeListener
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
        presenter.updateCurrencyList(CurrencyItem("EUR", BigDecimal.ONE, true))
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun updateCurrency(currencyList: List<CurrencyItem>) {
        adapter.updateCurrencyRate(currencyList)
        stopUpdating()
    }

    override fun stopUpdating() {
        swipeToRefreshContainer.isEnabled = true
        swipeToRefreshContainer.isRefreshing = false
    }

    override fun onError() {
        stopUpdating()
        val snackbar = Snackbar.make(
            rvCurrencyList,
            getString(R.string.error_loading),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar
            .setAction(getString(R.string.retry)) { presenter.retryCall() }
            .show()
        snackbar.view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.snackbarBackgroundColor
            )
        )
    }

    private val swipeToRefreshListener = {
        presenter.updateCurrencyList(adapter.getBaseItem())
    }

    companion object {

        const val TAG = "ExchangeRatesFragment"

        @JvmStatic
        fun newInstance() = ExchangeRatesFragment()
    }
}
