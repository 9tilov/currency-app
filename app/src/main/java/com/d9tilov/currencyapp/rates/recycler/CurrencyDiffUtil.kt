package com.d9tilov.currencyapp.rates.recycler

import androidx.recyclerview.widget.DiffUtil
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData

class CurrencyDiffUtil(
    private val oldCurrenciesList: List<CurrencyRateData.CurrencyItem>,
    private val newCurrenciesList: List<CurrencyRateData.CurrencyItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrenciesList[oldItemPosition].id == newCurrenciesList[newItemPosition].id
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
