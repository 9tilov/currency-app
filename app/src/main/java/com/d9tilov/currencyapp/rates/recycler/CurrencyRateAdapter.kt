package com.d9tilov.currencyapp.rates.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.d9tilov.currencyapp.R
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.utils.listeners.OnItemClickListener
import com.d9tilov.currencyapp.utils.listeners.OnValueChangeListener
import com.d9tilov.currencyapp.view.CurrencyCardView
import com.d9tilov.currencyapp.view.CurrencyTextWatcher
import timber.log.Timber

class CurrencyRateAdapter : RecyclerView.Adapter<CurrencyRateAdapter.CurrencyRateViewHolder>() {

    private var currencies: List<CurrencyRateData.CurrencyItem> = ArrayList()

    private companion object {
        enum class CURRENCY_VIEW_TYPE {
            BASE,
            COMMON
        }
    }

    var itemClickListener: OnItemClickListener<CurrencyRateData.CurrencyItem>? = null
    var valueChangeLister: OnValueChangeListener<CurrencyRateData.CurrencyItem, String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
        val context = parent.context
        val layoutId =
            if (viewType == CURRENCY_VIEW_TYPE.BASE.ordinal) R.layout.base_currency_item else R.layout.currency_item
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        val viewHolder = CurrencyRateViewHolder(view)
        view.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != NO_POSITION) {
                itemClickListener?.onItemClick(currencies[adapterPosition], adapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CurrencyRateViewHolder, position: Int) {
        holder.bind(currencies[position], holder.itemViewType == CURRENCY_VIEW_TYPE.BASE.ordinal)
    }

    override fun onBindViewHolder(
        holder: CurrencyRateViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        if (position == 0) {
            return
        }
        val newValue = payloads[0] as Double
        holder.bindWithPayload(newValue)
    }

    override fun getItemViewType(position: Int) =
        if (currencies[position].isBase)
            CURRENCY_VIEW_TYPE.BASE.ordinal
        else
            CURRENCY_VIEW_TYPE.COMMON.ordinal

    override fun getItemCount() = currencies.size

    fun updateCurrencyRate(newCurrencies: List<CurrencyRateData.CurrencyItem>) {
        val diffUtilsCallback = CurrencyDiffUtil(currencies, newCurrencies)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtilsCallback, true)
        diffUtilsResult.dispatchUpdatesTo(this)
        currencies = newCurrencies
    }

    private val onTextWatcher: CurrencyTextWatcher = object : CurrencyTextWatcher() {
        override fun onValueChanged(newValue: String) {
            Timber.d("onValueChanged")
            valueChangeLister?.onValueChanged(currencies[0], newValue, 0)
        }
    }

    inner class CurrencyRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currencyItemView: CurrencyCardView = itemView.findViewById(R.id.currency_item)

        fun bind(currencyItem: CurrencyRateData.CurrencyItem, isBase: Boolean) {
            currencyItemView.setLongName(currencyItem.longName)
            currencyItemView.setShortName(currencyItem.shortName)
            currencyItemView.setValue(currencyItem.value)
            currencyItemView.setIcon(currencyItem.icon)
            currencyItemView.setSign(currencyItem.sign)
            if (isBase) {
                Timber.d("bind")
                currencyItemView.addTextWatcher(onTextWatcher)
            }
        }

        fun bindWithPayload(value: Double) {
            currencyItemView.setValue(value)
        }
    }
}
