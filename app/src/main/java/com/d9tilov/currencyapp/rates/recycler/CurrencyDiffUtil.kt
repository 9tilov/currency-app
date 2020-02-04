package com.d9tilov.currencyapp.rates.recycler

import androidx.recyclerview.widget.DiffUtil
import com.d9tilov.currencyapp.rates.repository.CurrencyItem

class CurrencyDiffUtil(
    private val oldCurrenciesList: List<Any>,
    private val newCurrenciesList: List<Any>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldCurrenciesList[oldItemPosition] is CurrencyItem)
                && (newCurrenciesList[newItemPosition] is CurrencyItem)
                && (oldCurrenciesList[oldItemPosition] as CurrencyItem).name ==
                (newCurrenciesList[newItemPosition] as CurrencyItem).name
    }

    override fun getOldListSize(): Int {
        return oldCurrenciesList.size
    }

    override fun getNewListSize(): Int {
        return newCurrenciesList.size
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return (newCurrenciesList[newItemPosition] as CurrencyItem).value
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldCurrenciesList[oldItemPosition] as CurrencyItem).value ==
                (newCurrenciesList[newItemPosition] as CurrencyItem).value
    }
}
