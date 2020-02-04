package com.d9tilov.currencyapp.rates

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewStub
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
import com.d9tilov.currencyapp.rates.recycler.CurrencyRateAdapter.Companion.AD_POSITION_FREQUENCY
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.utils.listeners.OnItemClickListener
import com.d9tilov.currencyapp.utils.listeners.OnValueChangeListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class ExchangeRatesFragment : BaseMvpFragment<CurrencyRateView, CurrencyRatePresenter>(),
    CurrencyRateView {

    @Inject
    override lateinit var presenter: CurrencyRatePresenter
    private val adapter: CurrencyRateAdapter =
        CurrencyRateAdapter()
    private lateinit var rvCurrencyList: RecyclerView
    private lateinit var stub: ViewStub
    private lateinit var swipeToRefreshContainer: SwipeRefreshLayout
    private var snackBar: Snackbar? = null

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
        stub = view.findViewById(R.id.exchange_stub)
        stub.inflate()
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
        presenter.updateCurrencyList()
    }

    override fun onResume() {
        val items = adapter.getItems()
        for (item in items) {
            if (item is AdView) {
                item.resume()
            }
        }
        super.onResume()
    }

    override fun onPause() {
        val items = adapter.getItems()
        for (item in items) {
            if (item is AdView) {
                item.pause()
            }
        }
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        val items = adapter.getItems()
        for (item in items) {
            if (item is AdView) {
                item.destroy()
            }
        }
        super.onDestroy()
    }

    override fun updateCurrency(currencyList: List<CurrencyItem>) {
        if (currencyList.isEmpty()) {
            stub.visibility = VISIBLE
        } else {
            snackBar?.dismiss()
            stub.visibility = GONE
        }
        val listWithAd = LinkedList<Any>()
        listWithAd.addAll(currencyList)

        var i = 0
        while (i < listWithAd.size) {
            if (AD_POSITION_FREQUENCY == 0) {
                break
            }
            if (i != 0 && (i % AD_POSITION_FREQUENCY == 0)) {
                val adView = AdView(requireContext())
                adView.adSize = AdSize.BANNER
                adView.adUnitId = getString(R.string.banner_ad_view_type_id)
                listWithAd.add(i, adView)
            }
            ++i
        }
        adapter.updateCurrencyRate(listWithAd)
        stopUpdating()
        loadBannerAds()
        rvCurrencyList.adapter = adapter
    }

    private fun loadBannerAds() {
        loadBannerAd(AD_POSITION_FREQUENCY)
    }

    /**
     * Loads the banner ads in the items list.
     */
    private fun loadBannerAd(index: Int) {
        val items = adapter.getItems()
        if (index >= items.size) {
            return
        }
        val item: Any = items[index]
        if (item is AdView) {
            item.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loadBannerAd(index + AD_POSITION_FREQUENCY)
                }
                override fun onAdFailedToLoad(errorCode: Int) {
                    Log.e(
                        "MainActivity", "The previous banner ad failed to load. Attempting to"
                                + " load the next banner ad in the items list."
                    )
                    loadBannerAd(index + AD_POSITION_FREQUENCY)
                }
            }
            // Load the banner ad.
            item.loadAd(AdRequest.Builder().build())
        }
    }

    override fun stopUpdating() {
        swipeToRefreshContainer.isEnabled = true
        swipeToRefreshContainer.isRefreshing = false
    }

    override fun onError() {
        stopUpdating()
        snackBar = Snackbar.make(
            rvCurrencyList,
            getString(R.string.error_loading),
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar?.setAction(getString(R.string.retry)) { presenter.retryCall() }?.show()
        snackBar?.view?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.snackbarBackgroundColor
            )
        )
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
