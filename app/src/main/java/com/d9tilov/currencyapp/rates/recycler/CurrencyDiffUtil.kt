package com.d9tilov.currencyapp.rates.recycler

import androidx.recyclerview.widget.DiffUtil
import com.d9tilov.currencyapp.rates.repository.CurrencyItem

class CurrencyDiffUtil(
    private val oldCurrenciesList: List<CurrencyItem>,
    private val newCurrenciesList: List<CurrencyItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrenciesList[oldItemPosition].name == newCurrenciesList[newItemPosition].name
    }

    override fun getOldListSize(): Int {
        return oldCurrenciesList.size
    }

    override fun getNewListSize(): Int {
        return newCurrenciesList.size
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newCurrenciesList[newItemPosition].value
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrenciesList[oldItemPosition].value ==
                newCurrenciesList[newItemPosition].value
    }
}
