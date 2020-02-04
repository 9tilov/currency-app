package com.d9tilov.currencyapp.rates.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.utils.CurrencyUtils
import com.d9tilov.currencyapp.utils.listeners.OnItemClickListener
import com.d9tilov.currencyapp.utils.listeners.OnValueChangeListener
import com.d9tilov.currencyapp.view.CurrencyCardView
import com.d9tilov.currencyapp.view.CurrencyTextWatcher
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.math.BigDecimal
import java.util.concurrent.TimeUnit


class CurrencyRateAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currencies: MutableList<Any> = mutableListOf()
    private val disposable = CompositeDisposable()

    companion object {
        private enum class CurrencyViewType {
            BASE,
            COMMON,
            AD
        }

        private const val EDIT_TEXT_DEBOUNCE_IN_MILLS = 200L
        const val AD_POSITION_FREQUENCY = 7
    }

    var itemClickListener: OnItemClickListener<CurrencyItem>? = null
    var valueChangeLister: OnValueChangeListener<CurrencyItem, BigDecimal>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val layoutId: Int
        val viewHolder: RecyclerView.ViewHolder
        when (viewType) {
            CurrencyViewType.AD.ordinal -> {
                layoutId = R.layout.ad_currency_item
                val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
                viewHolder = AdViewHolder(view)
            }
            CurrencyViewType.BASE.ordinal -> {
                layoutId = R.layout.base_currency_item
                val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
                viewHolder = CurrencyRateViewHolder(view)
                view.setOnClickListener {
                    val adapterPosition = viewHolder.adapterPosition
                    if (adapterPosition != NO_POSITION && !isAdBannerPosition(adapterPosition)) {
                        itemClickListener?.onItemClick(
                            currencies[adapterPosition] as CurrencyItem,
                            adapterPosition
                        )
                    }
                }
            }
            else -> {
                layoutId = R.layout.currency_item
                val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
                viewHolder = CurrencyRateViewHolder(view)
                view.setOnClickListener {
                    val adapterPosition = viewHolder.adapterPosition
                    if (adapterPosition != NO_POSITION && !isAdBannerPosition(adapterPosition)) {
                        itemClickListener?.onItemClick(
                            currencies[adapterPosition] as CurrencyItem,
                            adapterPosition
                        )
                    }
                }
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            CurrencyViewType.AD.ordinal -> {
                val bannerViewHolder = holder as AdViewHolder
                Timber.d("moggot pos = %s", position)
                val adView = currencies[position] as AdView
                val adCardView = bannerViewHolder.itemView as ViewGroup
                if (adCardView.childCount > 0) {
                    adCardView.removeAllViews()
                }
                if (adView.parent != null) {
                    (adView.parent as ViewGroup).removeView(adView)
                }

                // Add the banner ad to the ad view.
                adView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                adCardView.addView(adView)
            }
            else -> {
                val currencyViewHolder = holder as CurrencyRateViewHolder
                currencyViewHolder.bind(
                    currencies[position],
                    holder.itemViewType == CurrencyViewType.BASE.ordinal
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        if (position == 0 || isAdBannerPosition(position)) {
            return
        }
        val newValue = payloads[0] as BigDecimal
        (holder as CurrencyRateViewHolder).bindWithPayload(newValue)
    }

    override fun getItemViewType(position: Int): Int {
        if (isAdBannerPosition(position)) {
            return CurrencyViewType.AD.ordinal
        }
        if ((currencies[position] as CurrencyItem).isBase)
            return CurrencyViewType.BASE.ordinal
        return CurrencyViewType.COMMON.ordinal
    }

    private fun isAdBannerPosition(position: Int): Boolean {
        if (AD_POSITION_FREQUENCY == 0) {
            return false
        }
        return position != 0 && position % AD_POSITION_FREQUENCY == 0
    }

    override fun getItemCount() = currencies.size

    fun updateCurrencyRate(newCurrencies: List<Any>) {
        val diffUtilsCallback = CurrencyDiffUtil(currencies, newCurrencies)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtilsCallback, false)
        currencies.clear()
        currencies.addAll(newCurrencies)
        diffUtilsResult.dispatchUpdatesTo(this)
    }

    fun getItems(): List<Any> {
        return currencies
    }

    inner class AdViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view)

    inner class CurrencyRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val currencyItemView: CurrencyCardView = itemView.findViewById(R.id.currency_item)

        fun bind(item: Any, isBase: Boolean) {
            if (item is AdView) {
                loadNativeAd(itemView.context)
                return
            }
            val currencyItem: CurrencyItem = item as CurrencyItem
            currencyItemView.setLongName(CurrencyUtils.getCurrencyFullName(currencyItem.name))
            currencyItemView.setShortName(item.name)
            currencyItemView.setValue(item.value)
            currencyItemView.setIcon(CurrencyUtils.getCurrencyIcon(item.name))
            currencyItemView.setSign(CurrencyUtils.getCurrencySignBy(item.name))
            if (isBase) {
                disposable.clear()
                disposable.add(formEditTextDisposable())
            }
        }

        private fun loadNativeAd(context: Context) {
            val adLoader =
                AdLoader.Builder(context, context.getString(R.string.banner_ad_view_type_id))
                    .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                        // Show the ad.
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(errorCode: Int) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build()
                    )
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        fun bindWithPayload(value: BigDecimal) {
            currencyItemView.setValue(value)
        }

        private fun formEditTextDisposable(): Disposable {
            return CurrencyTextWatcher.fromView(currencyItemView.value)
                .debounce(EDIT_TEXT_DEBOUNCE_IN_MILLS, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe({
                    valueChangeLister?.onValueChanged(
                        currencies[0] as CurrencyItem,
                        it ?: BigDecimal.ONE,
                        0
                    )
                }, {})
        }
    }
}
