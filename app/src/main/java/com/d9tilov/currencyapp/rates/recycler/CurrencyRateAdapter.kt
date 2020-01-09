package com.d9tilov.currencyapp.rates.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CurrencyRateAdapter : RecyclerView.Adapter<CurrencyRateAdapter.CurrencyRateViewHolder>() {

    private var currencies: MutableList<CurrencyItem> = mutableListOf()
    private val disposable = CompositeDisposable()

    private companion object {
        enum class CurrencyViewType {
            BASE,
            COMMON
        }

        private const val EDIT_TEXT_DEBOUNCE_IN_MILLS = 200L
    }

    var itemClickListener: OnItemClickListener<CurrencyItem>? = null
    var valueChangeLister: OnValueChangeListener<CurrencyItem, BigDecimal>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
        val context = parent.context
        val layoutId =
            if (viewType == CurrencyViewType.BASE.ordinal) R.layout.base_currency_item else R.layout.currency_item
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
        holder.bind(currencies[position], holder.itemViewType == CurrencyViewType.BASE.ordinal)
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
        val newValue = payloads[0] as BigDecimal
        holder.bindWithPayload(newValue)
    }

    override fun getItemViewType(position: Int) =
        if (currencies[position].isBase)
            CurrencyViewType.BASE.ordinal
        else
            CurrencyViewType.COMMON.ordinal

    override fun getItemCount() = currencies.size

    fun updateCurrencyRate(newCurrencies: List<CurrencyItem>) {
        val diffUtilsCallback = CurrencyDiffUtil(currencies, newCurrencies)
        val diffUtilsResult = DiffUtil.calculateDiff(diffUtilsCallback, true)
        currencies.clear()
        currencies.addAll(newCurrencies)
        diffUtilsResult.dispatchUpdatesTo(this)
    }

    fun getBaseItem(): CurrencyItem? {
        return currencies[0]
    }

    inner class CurrencyRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val currencyItemView: CurrencyCardView = itemView.findViewById(R.id.currency_item)

        fun bind(currencyItem: CurrencyItem, isBase: Boolean) {
            currencyItemView.setLongName(CurrencyUtils.getCurrencyFullName(currencyItem.name))
            currencyItemView.setShortName(currencyItem.name)
            currencyItemView.setValue(currencyItem.value)
            currencyItemView.setIcon(CurrencyUtils.getCurrencyIcon(currencyItem.name))
            currencyItemView.setSign(CurrencyUtils.getCurrencySignBy(currencyItem.name))
            if (isBase) {
                disposable.clear()
                disposable.add(formEditTextDisposable())
            }
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
                        currencies[0],
                        it ?: BigDecimal.ONE,
                        0
                    )
                }, {})
        }
    }
}
