package com.d9tilov.currencyapp.storage

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.model.CurrencyDto
import io.reactivex.Flowable
import java.util.concurrent.Executor

class CurrencyLocalRepository(
    private val database: AppDatabase,
    private val currencySharedPreferences: CurrencySharedPreferences,
    private val executor: Executor
) {

    private val localCopyOfCurrencyList: MutableList<CurrencyRateData.CurrencyItem> =
        mutableListOf()

    @WorkerThread
    fun updateCurrencyList(currencyRateData: CurrencyRateData) {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseItem = currencyRateData.base
        val baseValue = localCopyOfCurrencyList[0].value
        currencyRateList.add(CurrencyDto(baseItem, baseValue.toString()))
        for (item in currencyRateData.currencyList) {
            val newValue = baseValue * item.value
            currencyRateList.add(CurrencyDto(item.shortName, newValue.toString()))
        }
        database.currencyDao().updateCurrencyList(currencyRateList)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyRateData.CurrencyItem>> {
        return database.currencyDao().getCurrencyList()
            .doOnNext { localCopyOfCurrencyList.clear() }
            .flatMapSingle { list ->
                Flowable.fromIterable(list)
                    .map { convert(it) }
                    .doOnNext {
                        localCopyOfCurrencyList.add(it)
                    }
                    .toList()
            }
    }

    private fun convert(currencyDto: CurrencyDto): CurrencyRateData.CurrencyItem {
        val baseCurrency = currencySharedPreferences.loadBaseCurrency()
        return CurrencyRateData.CurrencyItem(
            currencyDto.name,
            currencyDto.value.toDouble(),
            baseCurrency.shortName == currencyDto.name
        )
    }

    fun updateBaseCurrency(baseCurrency: CurrencyRateData.CurrencyItem) {
        currencySharedPreferences.saveBaseCurrency(baseCurrency)
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        localCopyOfCurrencyList[0].isBase = false
        val oldValue =
            localCopyOfCurrencyList
                .find { it.id == baseCurrency.id }
                ?.value
                ?: return
        if (oldValue.compareTo(0) == 0) {
            return
        }
        localCopyOfCurrencyList.find { it.id == baseCurrency.id }?.isBase = true
        sortCurrency()
        val currencyRateList = mutableListOf<CurrencyDto>()
        currencyRateList.add(CurrencyDto(baseCurrency.shortName, "1"))
        for (item in localCopyOfCurrencyList) {
            val newValue = item.value / oldValue
            currencyRateList.add(CurrencyDto(item.shortName, newValue.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    fun changeValue(newValue: Double) {
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        val oldValue = localCopyOfCurrencyList[0].value
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            val newRecountedValue = item.value * newValue / oldValue
            currencyRateList.add(CurrencyDto(item.shortName, newRecountedValue.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    private fun sortCurrency() {
        localCopyOfCurrencyList.sortWith(compareBy({ !it.isBase }, { it.id }))
    }

    fun cancelWorking() {
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            currencyRateList.add(CurrencyDto(item.shortName, item.value.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }
}
