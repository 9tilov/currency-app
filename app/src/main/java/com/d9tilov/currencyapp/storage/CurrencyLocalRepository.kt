package com.d9tilov.currencyapp.storage

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.model.CurrencyDto
import io.reactivex.Flowable
import java.math.BigDecimal
import java.util.concurrent.Executor

class CurrencyLocalRepository(
    private val database: AppDatabase,
    private val currencySharedPreferences: CurrencySharedPreferences,
    private val executor: Executor
) {

    private val localCopyOfCurrencyList: MutableList<CurrencyItem> =
        mutableListOf()

    @WorkerThread
    fun updateCurrencyList(currencyRateData: CurrencyRateData, baseItem: CurrencyItem?) {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseValue = baseItem?.value ?: throw IllegalAccessException("Can't find base item")
        for (item in currencyRateData.currencyList) {
            val newValue = baseValue * item.value
            currencyRateList.add(CurrencyDto(item.name, newValue.toString()))
        }
        sortCurrency(currencyRateList, baseItem)
        database.currencyDao().updateCurrencyList(currencyRateList)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyItem>> {
        return database.currencyDao().getCurrencyList()
            .doOnNext {
                localCopyOfCurrencyList.clear()
            }
            .flatMapSingle { list ->
                Flowable.fromIterable(list)
                    .map { convert(it) }
                    .doOnNext {
                        localCopyOfCurrencyList.add(it)
                    }
                    .toList()
            }
    }

    private fun convert(currencyDto: CurrencyDto): CurrencyItem {
        val baseCurrency = currencySharedPreferences.loadBaseCurrency()
        return CurrencyItem(
            currencyDto.name,
            currencyDto.value.toBigDecimal(),
            baseCurrency.name == currencyDto.name
        )
    }

    fun changeBaseCurrency(baseCurrency: CurrencyItem) {
        currencySharedPreferences.saveBaseCurrency(baseCurrency)
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        localCopyOfCurrencyList[0].isBase = false
        var oldValue =
            localCopyOfCurrencyList.find { baseCurrency.name == it.name }?.value ?: BigDecimal.ONE
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            oldValue = BigDecimal.ONE
        }
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            val newValue = item.value / oldValue
            currencyRateList.add(CurrencyDto(item.name, newValue.toString()))
        }
        sortCurrency(currencyRateList, baseCurrency)
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    fun changeValue(newBaseItem: CurrencyItem) {
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        var oldValue = localCopyOfCurrencyList[0].value
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            oldValue = BigDecimal.ONE
        }
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            val newRecountedValue = item.value * newBaseItem.value / oldValue
            currencyRateList.add(CurrencyDto(item.name, newRecountedValue.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    private fun sortCurrency(currencyList: MutableList<CurrencyDto>, baseCurrency: CurrencyItem) {
        currencyList.sortWith(compareBy({ it.name != baseCurrency.name }, { it.name }))
    }

    fun cancelWorking() {
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            currencyRateList.add(CurrencyDto(item.name, item.value.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }
}
