package com.d9tilov.currencyapp.storage

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.model.CurrencyDto
import io.reactivex.Flowable

class CurrencyLocalRepository(
    private val database: AppDatabase,
    private val currencySharedPreferences: CurrencySharedPreferences
) {

    @WorkerThread
    fun updateCurrencyList(currencyRateData: CurrencyRateData) {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseItem = currencyRateData.base
        currencyRateList.add(CurrencyDto(baseItem, "1"))
        for (item in currencyRateData.currencyList) {
            currencyRateList.add(CurrencyDto(item.shortName, item.value.toString()))
        }
        database.currencyDao().updateCurrencyList(currencyRateList)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyRateData.CurrencyItem>> {
        return database.currencyDao().getCurrencyList()
            .flatMapSingle { list ->
                Flowable.fromIterable(list)
                    .map { convert(it) }
                    .toList()
            }
    }

    private fun convert(currencyDto: CurrencyDto): CurrencyRateData.CurrencyItem {
        val baseCurrency = currencySharedPreferences.loadBaseCurrency()
        return CurrencyRateData.CurrencyItem(
            currencyDto.name,
            currencyDto.value.toDouble(),
            baseCurrency == currencyDto.name
        )
    }
}
